package com.reynaud.poseuralert.model;

public enum ReportReason {
    NO_SHOW("Absence sans prévenir"),
    LATE("Retard important"),
    CANCELLED_LAST_MINUTE("Annulation de dernière minute"),
    REPEATED_MISSED("Absences répétées");

    private final String displayName;

    ReportReason(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}