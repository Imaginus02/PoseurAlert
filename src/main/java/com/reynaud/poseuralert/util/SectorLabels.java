package com.reynaud.poseuralert.util;

import com.reynaud.poseuralert.model.Sector;

public class SectorLabels {

    public static String getAppointmentLabel(Sector sector) {
        switch (sector) {
            case RESTAURANT: return "réservation";
            case HEALTH_PROFESSIONAL: return "consultation";
            case GARAGE: return "intervention";
            case HAIRDRESSER: return "rendez-vous coiffure";
            case BEAUTY_INSTITUTE: return "soin";
            case VETERINARIAN: return "consultation vétérinaire";
            default: return "rendez-vous";
        }
    }

    public static String getClientLabel(Sector sector) {
        switch (sector) {
            case RESTAURANT: return "client";
            case HEALTH_PROFESSIONAL: return "patient";
            case GARAGE: return "client";
            case HAIRDRESSER: return "client";
            case BEAUTY_INSTITUTE: return "client";
            case VETERINARIAN: return "propriétaire";
            default: return "client";
        }
    }

    public static String getAppointmentPluralLabel(Sector sector) {
        switch (sector) {
            case RESTAURANT: return "réservations";
            case HEALTH_PROFESSIONAL: return "consultations";
            case GARAGE: return "interventions";
            case HAIRDRESSER: return "rendez-vous";
            case BEAUTY_INSTITUTE: return "soins";
            case VETERINARIAN: return "consultations";
            default: return "rendez-vous";
        }
    }

    public static String getNewAppointmentButtonLabel(Sector sector) {
        switch (sector) {
            case RESTAURANT: return "Nouvelle réservation";
            case HEALTH_PROFESSIONAL: return "Nouvelle consultation";
            case GARAGE: return "Nouvelle intervention";
            case HAIRDRESSER: return "Nouveau rendez-vous";
            case BEAUTY_INSTITUTE: return "Nouveau soin";
            case VETERINARIAN: return "Nouvelle consultation";
            default: return "Nouveau rendez-vous";
        }
    }

    public static String getAppointmentDateLabel(Sector sector) {
        switch (sector) {
            case RESTAURANT: return "Date et heure de la réservation";
            case HEALTH_PROFESSIONAL: return "Date et heure de la consultation";
            case GARAGE: return "Date et heure de l'intervention";
            case HAIRDRESSER: return "Date et heure du rendez-vous";
            case BEAUTY_INSTITUTE: return "Date et heure du soin";
            case VETERINARIAN: return "Date et heure de la consultation";
            default: return "Date et heure du rendez-vous";
        }
    }

    public static String getHDSWarning(Sector sector) {
        if (sector == Sector.HEALTH_PROFESSIONAL) {
            return "⚠️ Conformément aux normes HDS (Hébergement de Données de Santé), seules les informations nécessaires à la gestion des rendez-vous sont stockées. " +
                   "Les données médicales ne doivent pas être saisies dans ce système.";
        }
        return null;
    }

    public static boolean requiresHDSCompliance(Sector sector) {
        return sector == Sector.HEALTH_PROFESSIONAL;
    }
}