package com.reynaud.poseuralert.model;

public enum Sector {
    RESTAURANT("Restaurant"),
    HEALTH_PROFESSIONAL("Professionnel de santé"),
    GARAGE("Garage"),
    HAIRDRESSER("Coiffeur"),
    BEAUTY_INSTITUTE("Institut de beauté"),
    VETERINARIAN("Vétérinaire");

    private final String displayName;

    Sector(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}