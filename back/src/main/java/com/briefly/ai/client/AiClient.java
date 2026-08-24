package com.briefly.ai.client;

import com.briefly.ai.dto.AiExplanationDto;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HTTP client for FastAPI assistive AI. Failures return empty Optional so core
 * Servlet/JSP flows keep working with original fund/brief/alert data.
 */
public class AiClient {
    private static final Logger LOGGER = Logger.getLogger(AiClient.class.getName());

    private final boolean enabled;
    private final String baseUrl;
    private final HttpClient httpClient;
    private final Duration timeout;

    public AiClient() {
        Properties props = loadProperties();
        this.enabled = Boolean.parseBoolean(props.getProperty("ai.enabled", "true"));
        this.baseUrl = trimTrailingSlash(props.getProperty("ai.baseUrl", "http://127.0.0.1:8000"));
        int timeoutMs = Integer.parseInt(props.getProperty("ai.timeoutMs", "1500"));
        this.timeout = Duration.ofMillis(timeoutMs);
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(this.timeout)
                .build();
    }

    public AiClient(boolean enabled, String baseUrl, Duration timeout, HttpClient httpClient) {
        this.enabled = enabled;
        this.baseUrl = trimTrailingSlash(baseUrl);
        this.timeout = timeout;
        this.httpClient = httpClient;
    }

    public Optional<AiExplanationDto> explainFund(
            long fundId,
            String name,
            String description,
            int riskGrade,
            BigDecimal expectedReturn
    ) {
        StringBuilder json = new StringBuilder(256);
        json.append('{')
                .append("\"fund_id\":").append(fundId).append(',')
                .append("\"name\":").append(jsonString(name)).append(',')
                .append("\"description\":").append(jsonString(nullToEmpty(description))).append(',')
                .append("\"risk_grade\":").append(riskGrade);
        if (expectedReturn != null) {
            json.append(",\"expected_return\":").append(expectedReturn.toPlainString());
        }
        json.append('}');
        return post("/v1/explain/fund", json.toString());
    }

    public Optional<AiExplanationDto> explainBrief(
            long fundId,
            String title,
            String content,
            String reportDate
    ) {
        StringBuilder json = new StringBuilder(256);
        json.append('{')
                .append("\"fund_id\":").append(fundId).append(',')
                .append("\"title\":").append(jsonString(title)).append(',')
                .append("\"content\":").append(jsonString(content));
        if (reportDate != null && !reportDate.isBlank()) {
            json.append(",\"report_date\":").append(jsonString(reportDate));
        }
        json.append('}');
        return post("/v1/explain/brief", json.toString());
    }

    public Optional<AiExplanationDto> explainRisk(
            long fundId,
            String title,
            String message,
            int previousGrade,
            int newGrade
    ) {
        String json = "{"
                + "\"fund_id\":" + fundId + ","
                + "\"title\":" + jsonString(title) + ","
                + "\"message\":" + jsonString(message) + ","
                + "\"previous_grade\":" + previousGrade + ","
                + "\"new_grade\":" + newGrade
                + "}";
        return post("/v1/explain/risk", json);
    }

    private Optional<AiExplanationDto> post(String path, String body) {
        if (!enabled) {
            return Optional.empty();
        }
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .timeout(timeout)
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                LOGGER.log(Level.WARNING, "AI service HTTP {0} for {1}", new Object[]{response.statusCode(), path});
                return Optional.empty();
            }
            return parseExplainResponse(response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.WARNING, "AI service interrupted: " + path, e);
            return Optional.empty();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "AI service unavailable for " + path + " — serving original content only", e);
            return Optional.empty();
        }
    }

    static Optional<AiExplanationDto> parseExplainResponse(String body) {
        if (body == null || body.isBlank()) {
            return Optional.empty();
        }
        if (!extractBoolean(body, "ok")) {
            return Optional.empty();
        }
        String explanation = extractString(body, "explanation");
        if (explanation == null || explanation.isBlank()) {
            return Optional.empty();
        }
        String disclaimer = extractString(body, "disclaimer");
        String model = extractString(body, "model");
        String generatedAt = extractString(body, "generated_at");
        return Optional.of(new AiExplanationDto(explanation, disclaimer, model, generatedAt));
    }

    private static boolean extractBoolean(String json, String key) {
        String needle = "\"" + key + "\"";
        int idx = json.indexOf(needle);
        if (idx < 0) {
            return false;
        }
        int colon = json.indexOf(':', idx + needle.length());
        if (colon < 0) {
            return false;
        }
        String rest = json.substring(colon + 1).trim();
        return rest.startsWith("true");
    }

    private static String extractString(String json, String key) {
        String needle = "\"" + key + "\"";
        int idx = json.indexOf(needle);
        if (idx < 0) {
            return null;
        }
        int colon = json.indexOf(':', idx + needle.length());
        if (colon < 0) {
            return null;
        }
        int startQuote = json.indexOf('"', colon + 1);
        if (startQuote < 0) {
            return null;
        }
        StringBuilder out = new StringBuilder();
        for (int i = startQuote + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char n = json.charAt(++i);
                switch (n) {
                    case 'n' -> out.append('\n');
                    case 't' -> out.append('\t');
                    case 'r' -> out.append('\r');
                    case '"' -> out.append('"');
                    case '\\' -> out.append('\\');
                    case 'u' -> {
                        if (i + 4 < json.length()) {
                            String hex = json.substring(i + 1, i + 5);
                            out.append((char) Integer.parseInt(hex, 16));
                            i += 4;
                        }
                    }
                    default -> out.append(n);
                }
                continue;
            }
            if (c == '"') {
                return out.toString();
            }
            out.append(c);
        }
        return null;
    }

    private static String jsonString(String value) {
        if (value == null) {
            return "\"\"";
        }
        StringBuilder sb = new StringBuilder(value.length() + 16);
        sb.append('"');
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '"' -> sb.append("\\\"");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String trimTrailingSlash(String url) {
        if (url == null || url.isBlank()) {
            return "http://127.0.0.1:8000";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = AiClient.class.getClassLoader().getResourceAsStream("ai.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load ai.properties; using defaults", e);
        }
        return props;
    }
}
