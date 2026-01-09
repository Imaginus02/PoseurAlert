package com.reynaud.poseuralert.dao;

import com.reynaud.poseuralert.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AuditLogDaoTest {

    @Autowired
    private AuditLogDao auditLogDao;

    @Autowired
    private UserDao userDao;

    private UserEntity healthProfessional;
    private UserEntity restaurantUser;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        // Créer des utilisateurs de test
        healthProfessional = new UserEntity("health@test.com", passwordEncoder.encode("password123"),
                                          "Cabinet Médical", Sector.HEALTH_PROFESSIONAL);
        healthProfessional = userDao.save(healthProfessional);

        restaurantUser = new UserEntity("restaurant@test.com", passwordEncoder.encode("password123"),
                                      "Le Bon Restaurant", Sector.RESTAURANT);
        restaurantUser = userDao.save(restaurantUser);
    }

    @Test
    void testSaveAndFindAuditLog() {
        // Créer un log d'audit
        AuditLogEntity auditLog = new AuditLogEntity(healthProfessional, "VIEW", "PATIENT_DATA", "patient123");
        auditLog.setDetails("Consultation du dossier patient");
        auditLog.setIpAddress("192.168.1.100");

        // Sauvegarder
        AuditLogEntity savedAuditLog = auditLogDao.save(auditLog);

        // Vérifier que le log a été sauvegardé
        assertNotNull(savedAuditLog.getId());
        assertEquals(healthProfessional.getId(), savedAuditLog.getProfessional().getId());
        assertEquals("VIEW", savedAuditLog.getAction());
        assertEquals("PATIENT_DATA", savedAuditLog.getResourceType());
        assertEquals("patient123", savedAuditLog.getResourceId());
        assertEquals("Consultation du dossier patient", savedAuditLog.getDetails());
        assertEquals("192.168.1.100", savedAuditLog.getIpAddress());
        assertNotNull(savedAuditLog.getTimestamp());
    }

    @Test
    void testFindByProfessional() {
        // Créer plusieurs logs pour différents professionnels
        AuditLogEntity log1 = new AuditLogEntity(healthProfessional, "CREATE", "APPOINTMENT", "apt1");
        AuditLogEntity log2 = new AuditLogEntity(healthProfessional, "VIEW", "PATIENT_DATA", "patient1");
        AuditLogEntity log3 = new AuditLogEntity(restaurantUser, "CREATE", "APPOINTMENT", "apt2");

        auditLogDao.save(log1);
        auditLogDao.save(log2);
        auditLogDao.save(log3);

        // Récupérer les logs par professionnel
        List<AuditLogEntity> healthLogs = auditLogDao.findByProfessional(healthProfessional);
        List<AuditLogEntity> restaurantLogs = auditLogDao.findByProfessional(restaurantUser);

        assertEquals(2, healthLogs.size());
        assertEquals(1, restaurantLogs.size());

        // Vérifier que tous les logs du professionnel de santé appartiennent bien à lui
        assertTrue(healthLogs.stream().allMatch(log ->
            healthProfessional.getId().equals(log.getProfessional().getId())));

        // Vérifier que tous les logs du restaurant appartiennent bien à lui
        assertTrue(restaurantLogs.stream().allMatch(log ->
            restaurantUser.getId().equals(log.getProfessional().getId())));
    }

    @Test
    void testFindByProfessionalAndResourceType() {
        // Créer des logs avec différents types de ressources
        AuditLogEntity appointmentLog1 = new AuditLogEntity(healthProfessional, "CREATE", "APPOINTMENT", "apt1");
        AuditLogEntity appointmentLog2 = new AuditLogEntity(healthProfessional, "UPDATE", "APPOINTMENT", "apt2");
        AuditLogEntity patientLog = new AuditLogEntity(healthProfessional, "VIEW", "PATIENT_DATA", "patient1");
        AuditLogEntity otherLog = new AuditLogEntity(restaurantUser, "CREATE", "APPOINTMENT", "apt3");

        auditLogDao.save(appointmentLog1);
        auditLogDao.save(appointmentLog2);
        auditLogDao.save(patientLog);
        auditLogDao.save(otherLog);

        // Récupérer les logs d'appointments pour le professionnel de santé
        List<AuditLogEntity> appointmentLogs = auditLogDao.findByProfessionalAndResourceType(healthProfessional, "APPOINTMENT");

        assertEquals(2, appointmentLogs.size());
        assertTrue(appointmentLogs.stream().allMatch(log -> "APPOINTMENT".equals(log.getResourceType())));
        assertTrue(appointmentLogs.stream().allMatch(log ->
            healthProfessional.getId().equals(log.getProfessional().getId())));

        // Récupérer les logs de données patient
        List<AuditLogEntity> patientLogs = auditLogDao.findByProfessionalAndResourceType(healthProfessional, "PATIENT_DATA");

        assertEquals(1, patientLogs.size());
        assertEquals("PATIENT_DATA", patientLogs.get(0).getResourceType());
        assertEquals("patient1", patientLogs.get(0).getResourceId());
    }

    @Test
    void testAuditLogTimestampIsSetAutomatically() {
        LocalDateTime beforeSave = LocalDateTime.now();

        // Créer et sauvegarder un log
        AuditLogEntity auditLog = new AuditLogEntity(healthProfessional, "LOGIN", "SYSTEM", "login");
        AuditLogEntity savedAuditLog = auditLogDao.save(auditLog);

        LocalDateTime afterSave = LocalDateTime.now();

        // Vérifier que le timestamp a été défini automatiquement
        assertNotNull(savedAuditLog.getTimestamp());
        assertTrue(savedAuditLog.getTimestamp().isAfter(beforeSave.minusSeconds(1)));
        assertTrue(savedAuditLog.getTimestamp().isBefore(afterSave.plusSeconds(1)));
    }

    @Test
    void testAuditLogForAppointmentCreation() {
        // Simuler la création d'un log lors de la création d'un rendez-vous
        AuditLogEntity auditLog = new AuditLogEntity(healthProfessional, "CREATE", "APPOINTMENT", "apt123");
        auditLog.setDetails("Création de rendez-vous pour Marie Dupont (0123456789)");

        AuditLogEntity savedAuditLog = auditLogDao.save(auditLog);

        assertNotNull(savedAuditLog.getId());
        assertEquals("CREATE", savedAuditLog.getAction());
        assertEquals("APPOINTMENT", savedAuditLog.getResourceType());
        assertEquals("apt123", savedAuditLog.getResourceId());
        assertTrue(savedAuditLog.getDetails().contains("Marie Dupont"));
        assertTrue(savedAuditLog.getDetails().contains("0123456789"));
    }

    @Test
    void testAuditLogForAppointmentReporting() {
        // Simuler la création d'un log lors du signalement d'un rendez-vous
        AuditLogEntity auditLog = new AuditLogEntity(healthProfessional, "REPORT", "APPOINTMENT", "apt456");
        auditLog.setDetails("Signalement de rendez-vous - Motif: Absence sans prévenir");

        AuditLogEntity savedAuditLog = auditLogDao.save(auditLog);

        assertNotNull(savedAuditLog.getId());
        assertEquals("REPORT", savedAuditLog.getAction());
        assertEquals("APPOINTMENT", savedAuditLog.getResourceType());
        assertEquals("apt456", savedAuditLog.getResourceId());
        assertTrue(savedAuditLog.getDetails().contains("Absence sans prévenir"));
    }

    @Test
    void testAuditLogForDataAccess() {
        // Simuler l'accès aux données pour audit HDS
        AuditLogEntity auditLog = new AuditLogEntity(healthProfessional, "VIEW", "PATIENT_DATA", "patient789");
        auditLog.setDetails("Consultation de la liste des rendez-vous");
        auditLog.setIpAddress("10.0.0.1");

        AuditLogEntity savedAuditLog = auditLogDao.save(auditLog);

        assertNotNull(savedAuditLog.getId());
        assertEquals("VIEW", savedAuditLog.getAction());
        assertEquals("PATIENT_DATA", savedAuditLog.getResourceType());
        assertEquals("patient789", savedAuditLog.getResourceId());
        assertEquals("10.0.0.1", savedAuditLog.getIpAddress());
    }
}