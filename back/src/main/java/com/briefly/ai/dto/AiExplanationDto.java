package com.briefly.ai.dto;

/**
 * Assistive AI explanation returned by FastAPI. Never an investment decision.
 */
public class AiExplanationDto {
    private final String explanation;
    private final String disclaimer;
    private final String model;
    private final String generatedAt;

    public AiExplanationDto(String explanation, String disclaimer, String model, String generatedAt) {
        this.explanation = explanation;
        this.disclaimer = disclaimer;
        this.model = model;
        this.generatedAt = generatedAt;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public String getModel() {
        return model;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }
}
