<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.briefly.report.dto.ReportDto" %>
<%@ page import="com.briefly.ai.dto.AiExplanationDto" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>Briefly - 운용 브리프</title>
</head>
<body>
<h1>운용 브리프 (상품 #${fundId})</h1>
<%
    AiExplanationDto aiExplanation = (AiExplanationDto) request.getAttribute("aiExplanation");
    if (aiExplanation != null) {
%>
<section>
    <h2>최신 브리프 쉬운 요약 (보조)</h2>
    <p><%= aiExplanation.getExplanation() %></p>
    <p><small><%= aiExplanation.getDisclaimer() %></small></p>
</section>
<%
    }
%>
<ul>
<%
    List<ReportDto> reports = (List<ReportDto>) request.getAttribute("reports");
    if (reports == null || reports.isEmpty()) {
%>
    <li>등록된 브리프가 없습니다.</li>
<%
    } else {
        for (ReportDto report : reports) {
%>
    <li>
        <strong><%= report.getTitle() %></strong> (<%= report.getReportDate() %>)
        <p><%= report.getContent() %></p>
    </li>
<%
        }
    }
%>
</ul>
<p><a href="${pageContext.request.contextPath}/funds/detail?id=${fundId}">상품 상세</a></p>
</body>
</html>
