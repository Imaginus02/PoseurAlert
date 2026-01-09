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
class ReportingDaoTest {

    @Autowired
    private ReportDao reportDao;

    @Autowired
    private ClientPhoneDao clientPhoneDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AppointmentDao appointmentDao;

    private UserEntity testUser;
    private AppointmentEntity testAppointment;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        // Créer un utilisateur de test
        testUser = new UserEntity("test@example.com", passwordEncoder.encode("password123"),
                                "Test Company", Sector.RESTAURANT);
        testUser = userDao.save(testUser);

        // Créer un rendez-vous de test
        LocalDateTime appointmentDate = LocalDateTime.now().minusHours(2);
        testAppointment = new AppointmentEntity(testUser, "Test Client", "0123456789", appointmentDate);
        testAppointment = appointmentDao.save(testAppointment);
    }

    @Test
    void testSaveAndFindReport() {
        // Créer un signalement
        ReportEntity report = new ReportEntity("0123456789", testUser, testAppointment, ReportReason.NO_SHOW);
        report.setAdditionalNotes("Client n'est jamais arrivé");

        // Sauvegarder
        ReportEntity savedReport = reportDao.save(report);

        // Vérifier que le signalement a été sauvegardé
        assertNotNull(savedReport.getId());
        assertEquals("0123456789", savedReport.getReportedPhone());
        assertEquals(testUser.getId(), savedReport.getProfessional().getId());
        assertEquals(testAppointment.getId(), savedReport.getAppointment().getId());
        assertEquals(ReportReason.NO_SHOW, savedReport.getReason());
        assertEquals("Client n'est jamais arrivé", savedReport.getAdditionalNotes());
    }

    @Test
    void testCountByReportedPhone() {
        // Créer plusieurs signalements pour le même numéro
        ReportEntity report1 = new ReportEntity("0999999999", testUser, ReportReason.NO_SHOW);
        ReportEntity report2 = new ReportEntity("0999999999", testUser, ReportReason.LATE);
        ReportEntity report3 = new ReportEntity("0888888888", testUser, ReportReason.NO_SHOW);

        reportDao.save(report1);
        reportDao.save(report2);
        reportDao.save(report3);

        // Compter les signalements pour chaque numéro
        long countForPhone1 = reportDao.countByReportedPhone("0999999999");
        long countForPhone2 = reportDao.countByReportedPhone("0888888888");
        long countForPhone3 = reportDao.countByReportedPhone("0777777777");

        assertEquals(2, countForPhone1);
        assertEquals(1, countForPhone2);
        assertEquals(0, countForPhone3);
    }

    @Test
    void testFindByReportedPhone() {
        // Créer des signalements pour le même numéro
        ReportEntity report1 = new ReportEntity("0666666666", testUser, ReportReason.NO_SHOW);
        ReportEntity report2 = new ReportEntity("0666666666", testUser, ReportReason.LATE);

        reportDao.save(report1);
        reportDao.save(report2);

        // Récupérer les signalements par numéro
        List<ReportEntity> reports = reportDao.findByReportedPhone("0666666666");

        assertEquals(2, reports.size());
        assertTrue(reports.stream().allMatch(r -> "0666666666".equals(r.getReportedPhone())));
        assertTrue(reports.stream().anyMatch(r -> ReportReason.NO_SHOW.equals(r.getReason())));
        assertTrue(reports.stream().anyMatch(r -> ReportReason.LATE.equals(r.getReason())));
    }

    @Test
    void testFindByProfessional() {
        // Créer des utilisateurs et signalements différents
        UserEntity otherUser = new UserEntity("other@example.com", passwordEncoder.encode("password123"),
                                             "Other Company", Sector.GARAGE);
        otherUser = userDao.save(otherUser);

        ReportEntity report1 = new ReportEntity("0555555555", testUser, ReportReason.NO_SHOW);
        ReportEntity report2 = new ReportEntity("0444444444", testUser, ReportReason.LATE);
        ReportEntity report3 = new ReportEntity("0333333333", otherUser, ReportReason.NO_SHOW);

        reportDao.save(report1);
        reportDao.save(report2);
        reportDao.save(report3);

        // Récupérer les signalements par professionnel
        List<ReportEntity> testUserReports = reportDao.findByProfessional(testUser);
        List<ReportEntity> otherUserReports = reportDao.findByProfessional(otherUser);

        assertEquals(2, testUserReports.size());
        assertEquals(1, otherUserReports.size());
        Long testUserId = testUser.getId();
        Long otherUserId = otherUser.getId();
        assertTrue(testUserReports.stream().allMatch(r -> testUserId.equals(r.getProfessional().getId())));
        assertTrue(otherUserReports.stream().allMatch(r -> otherUserId.equals(r.getProfessional().getId())));
    }

    @Test
    void testClientPhoneEntityFlagging() {
        // Créer une entrée ClientPhone
        ClientPhoneEntity clientPhone = new ClientPhoneEntity("0222222222");
        clientPhone = clientPhoneDao.save(clientPhone);

        // Vérifier l'état initial
        assertEquals(0, clientPhone.getReportCount());
        assertFalse(clientPhone.getIsFlagged());

        // Incrémenter une fois (devrait rester non flagged)
        clientPhone.incrementReportCount();
        clientPhone = clientPhoneDao.save(clientPhone);
        assertEquals(1, clientPhone.getReportCount());
        assertFalse(clientPhone.getIsFlagged());

        // Incrémenter une deuxième fois (devrait devenir flagged)
        clientPhone.incrementReportCount();
        clientPhone = clientPhoneDao.save(clientPhone);
        assertEquals(2, clientPhone.getReportCount());
        assertTrue(clientPhone.getIsFlagged());
    }

    @Test
    void testClientPhoneEntityFindByPhoneNumber() {
        // Créer et sauvegarder un numéro
        ClientPhoneEntity clientPhone = new ClientPhoneEntity("0111111111");
        clientPhone.incrementReportCount();
        clientPhone = clientPhoneDao.save(clientPhone);

        // Récupérer par numéro
        ClientPhoneEntity foundPhone = clientPhoneDao.findByPhoneNumber("0111111111").orElse(null);
        assertNotNull(foundPhone);
        assertEquals("0111111111", foundPhone.getPhoneNumber());
        assertEquals(1, foundPhone.getReportCount());

        // Tester un numéro inexistant
        ClientPhoneEntity notFoundPhone = clientPhoneDao.findByPhoneNumber("0999999999").orElse(null);
        assertNull(notFoundPhone);
    }

    @Test
    void testClientPhoneEntityExistsByPhoneNumber() {
        // Créer et sauvegarder un numéro
        ClientPhoneEntity clientPhone = new ClientPhoneEntity("0000000000");
        clientPhoneDao.save(clientPhone);

        // Vérifier l'existence
        assertTrue(clientPhoneDao.existsByPhoneNumber("0000000000"));
        assertFalse(clientPhoneDao.existsByPhoneNumber("0987654321"));
    }

    @Test
    void testReportCreationWithoutAppointment() {
        // Créer un signalement sans rendez-vous associé
        ReportEntity report = new ReportEntity("0666666666", testUser, ReportReason.REPEATED_MISSED);
        report.setAdditionalNotes("Client avec plusieurs absences");

        ReportEntity savedReport = reportDao.save(report);

        assertNotNull(savedReport.getId());
        assertEquals("0666666666", savedReport.getReportedPhone());
        assertNull(savedReport.getAppointment()); // Pas de rendez-vous associé
        assertEquals(ReportReason.REPEATED_MISSED, savedReport.getReason());
    }
}