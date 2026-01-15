package com.reynaud.poseuralert.model;

public enum SMSCategory {
    REMINDER("Rappel de rendez-vous"),
    PROMOTIONAL("Texte promotionnel");

    private final String displayName;

    SMSCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
