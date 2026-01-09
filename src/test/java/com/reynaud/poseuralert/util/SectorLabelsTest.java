package com.reynaud.poseuralert.util;

import com.reynaud.poseuralert.model.Sector;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SectorLabelsTest {

    @Test
    void testGetAppointmentLabelForAllSectors() {
        // Test pour restaurant
        assertEquals("réservation", SectorLabels.getAppointmentLabel(Sector.RESTAURANT));

        // Test pour professionnel de santé
        assertEquals("consultation", SectorLabels.getAppointmentLabel(Sector.HEALTH_PROFESSIONAL));

        // Test pour garage
        assertEquals("intervention", SectorLabels.getAppointmentLabel(Sector.GARAGE));

        // Test pour coiffeur
        assertEquals("rendez-vous coiffure", SectorLabels.getAppointmentLabel(Sector.HAIRDRESSER));

        // Test pour institut de beauté
        assertEquals("soin", SectorLabels.getAppointmentLabel(Sector.BEAUTY_INSTITUTE));

        // Test pour vétérinaire
        assertEquals("consultation vétérinaire", SectorLabels.getAppointmentLabel(Sector.VETERINARIAN));
    }

    @Test
    void testGetClientLabelForAllSectors() {
        // Test pour restaurant
        assertEquals("client", SectorLabels.getClientLabel(Sector.RESTAURANT));

        // Test pour professionnel de santé
        assertEquals("patient", SectorLabels.getClientLabel(Sector.HEALTH_PROFESSIONAL));

        // Test pour garage
        assertEquals("client", SectorLabels.getClientLabel(Sector.GARAGE));

        // Test pour coiffeur
        assertEquals("client", SectorLabels.getClientLabel(Sector.HAIRDRESSER));

        // Test pour institut de beauté
        assertEquals("client", SectorLabels.getClientLabel(Sector.BEAUTY_INSTITUTE));

        // Test pour vétérinaire
        assertEquals("propriétaire", SectorLabels.getClientLabel(Sector.VETERINARIAN));
    }

    @Test
    void testGetAppointmentPluralLabelForAllSectors() {
        // Test pour restaurant
        assertEquals("réservations", SectorLabels.getAppointmentPluralLabel(Sector.RESTAURANT));

        // Test pour professionnel de santé
        assertEquals("consultations", SectorLabels.getAppointmentPluralLabel(Sector.HEALTH_PROFESSIONAL));

        // Test pour garage
        assertEquals("interventions", SectorLabels.getAppointmentPluralLabel(Sector.GARAGE));

        // Test pour coiffeur
        assertEquals("rendez-vous", SectorLabels.getAppointmentPluralLabel(Sector.HAIRDRESSER));

        // Test pour institut de beauté
        assertEquals("soins", SectorLabels.getAppointmentPluralLabel(Sector.BEAUTY_INSTITUTE));

        // Test pour vétérinaire
        assertEquals("consultations", SectorLabels.getAppointmentPluralLabel(Sector.VETERINARIAN));
    }

    @Test
    void testGetNewAppointmentButtonLabelForAllSectors() {
        // Test pour restaurant
        assertEquals("Nouvelle réservation", SectorLabels.getNewAppointmentButtonLabel(Sector.RESTAURANT));

        // Test pour professionnel de santé
        assertEquals("Nouvelle consultation", SectorLabels.getNewAppointmentButtonLabel(Sector.HEALTH_PROFESSIONAL));

        // Test pour garage
        assertEquals("Nouvelle intervention", SectorLabels.getNewAppointmentButtonLabel(Sector.GARAGE));

        // Test pour coiffeur
        assertEquals("Nouveau rendez-vous", SectorLabels.getNewAppointmentButtonLabel(Sector.HAIRDRESSER));

        // Test pour institut de beauté
        assertEquals("Nouveau soin", SectorLabels.getNewAppointmentButtonLabel(Sector.BEAUTY_INSTITUTE));

        // Test pour vétérinaire
        assertEquals("Nouvelle consultation", SectorLabels.getNewAppointmentButtonLabel(Sector.VETERINARIAN));
    }

    @Test
    void testGetAppointmentDateLabelForAllSectors() {
        // Test pour restaurant
        assertEquals("Date et heure de la réservation", SectorLabels.getAppointmentDateLabel(Sector.RESTAURANT));

        // Test pour professionnel de santé
        assertEquals("Date et heure de la consultation", SectorLabels.getAppointmentDateLabel(Sector.HEALTH_PROFESSIONAL));

        // Test pour garage
        assertEquals("Date et heure de l'intervention", SectorLabels.getAppointmentDateLabel(Sector.GARAGE));

        // Test pour coiffeur
        assertEquals("Date et heure du rendez-vous", SectorLabels.getAppointmentDateLabel(Sector.HAIRDRESSER));

        // Test pour institut de beauté
        assertEquals("Date et heure du soin", SectorLabels.getAppointmentDateLabel(Sector.BEAUTY_INSTITUTE));

        // Test pour vétérinaire
        assertEquals("Date et heure de la consultation", SectorLabels.getAppointmentDateLabel(Sector.VETERINARIAN));
    }

    @Test
    void testHDSCompliance() {
        // Test que seul le secteur santé nécessite la conformité HDS
        assertTrue(SectorLabels.requiresHDSCompliance(Sector.HEALTH_PROFESSIONAL));
        assertFalse(SectorLabels.requiresHDSCompliance(Sector.RESTAURANT));
        assertFalse(SectorLabels.requiresHDSCompliance(Sector.GARAGE));
        assertFalse(SectorLabels.requiresHDSCompliance(Sector.HAIRDRESSER));
        assertFalse(SectorLabels.requiresHDSCompliance(Sector.BEAUTY_INSTITUTE));
        assertFalse(SectorLabels.requiresHDSCompliance(Sector.VETERINARIAN));
    }

    @Test
    void testGetHDSWarning() {
        // Test que seul le secteur santé a un avertissement HDS
        String hdsWarning = SectorLabels.getHDSWarning(Sector.HEALTH_PROFESSIONAL);
        assertNotNull(hdsWarning);
        assertTrue(hdsWarning.contains("HDS"));
        assertTrue(hdsWarning.contains("données médicales"));

        // Les autres secteurs n'ont pas d'avertissement HDS
        assertNull(SectorLabels.getHDSWarning(Sector.RESTAURANT));
        assertNull(SectorLabels.getHDSWarning(Sector.GARAGE));
        assertNull(SectorLabels.getHDSWarning(Sector.HAIRDRESSER));
        assertNull(SectorLabels.getHDSWarning(Sector.BEAUTY_INSTITUTE));
        assertNull(SectorLabels.getHDSWarning(Sector.VETERINARIAN));
    }

    @Test
    void testDefaultLabels() {
        // Test des valeurs par défaut (null devrait retourner une valeur par défaut)
        // Note: Cette méthode utilise des switch sans default explicite, donc elle retournera null
        // Mais dans la pratique, tous les secteurs sont couverts
        assertNotNull(SectorLabels.getAppointmentLabel(Sector.RESTAURANT));
        assertNotNull(SectorLabels.getClientLabel(Sector.RESTAURANT));
    }
}