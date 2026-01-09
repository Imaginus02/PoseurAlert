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
class AppointmentDaoTest {

    @Autowired
    private AppointmentDao appointmentDao;

    @Autowired
    private UserDao userDao;

    private UserEntity testUser;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        // Créer un utilisateur de test
        testUser = new UserEntity("test@example.com", passwordEncoder.encode("password123"),
                                "Test Company", Sector.RESTAURANT);
        testUser = userDao.save(testUser);
    }

    @Test
    void testSaveAndFindAppointment() {
        // Créer un rendez-vous
        LocalDateTime appointmentDate = LocalDateTime.now().plusDays(1);
        AppointmentEntity appointment = new AppointmentEntity(testUser, "Jean Dupont", "0123456789", appointmentDate);
        appointment.setNotes("Test appointment");
        appointment.setStatus(AppointmentStatus.CONFIRMED);

        // Sauvegarder
        AppointmentEntity savedAppointment = appointmentDao.save(appointment);

        // Vérifier que le rendez-vous a été sauvegardé
        assertNotNull(savedAppointment.getId());
        assertEquals(testUser.getId(), savedAppointment.getProfessional().getId());
        assertEquals("Jean Dupont", savedAppointment.getClientName());
        assertEquals("0123456789", savedAppointment.getClientPhone());
        assertEquals(AppointmentStatus.CONFIRMED, savedAppointment.getStatus());
        assertEquals("Test appointment", savedAppointment.getNotes());
    }

    @Test
    void testFindByProfessional() {
        // Créer plusieurs rendez-vous
        LocalDateTime date1 = LocalDateTime.now().plusDays(1);
        LocalDateTime date2 = LocalDateTime.now().plusDays(2);

        AppointmentEntity appointment1 = new AppointmentEntity(testUser, "Client 1", "0111111111", date1);
        AppointmentEntity appointment2 = new AppointmentEntity(testUser, "Client 2", "0222222222", date2);

        appointmentDao.save(appointment1);
        appointmentDao.save(appointment2);

        // Récupérer les rendez-vous du professionnel
        List<AppointmentEntity> appointments = appointmentDao.findByProfessional(testUser);

        assertEquals(2, appointments.size());
        assertTrue(appointments.stream().anyMatch(a -> "Client 1".equals(a.getClientName())));
        assertTrue(appointments.stream().anyMatch(a -> "Client 2".equals(a.getClientName())));
    }

    @Test
    void testFindByProfessionalAndClientPhone() {
        // Créer des rendez-vous avec le même numéro de téléphone
        LocalDateTime date1 = LocalDateTime.now().plusDays(1);
        LocalDateTime date2 = LocalDateTime.now().plusDays(2);

        AppointmentEntity appointment1 = new AppointmentEntity(testUser, "Client A", "0333333333", date1);
        AppointmentEntity appointment2 = new AppointmentEntity(testUser, "Client B", "0333333333", date2);

        appointmentDao.save(appointment1);
        appointmentDao.save(appointment2);

        // Récupérer les rendez-vous par professionnel et numéro de téléphone
        List<AppointmentEntity> appointments = appointmentDao.findByProfessionalAndClientPhone(testUser, "0333333333");

        assertEquals(2, appointments.size());
        assertTrue(appointments.stream().allMatch(a -> "0333333333".equals(a.getClientPhone())));
    }

    @Test
    void testFindByProfessionalAndStatus() {
        // Créer des rendez-vous avec différents statuts
        LocalDateTime date = LocalDateTime.now().plusDays(1);

        AppointmentEntity scheduled = new AppointmentEntity(testUser, "Client Scheduled", "0444444444", date);
        scheduled.setStatus(AppointmentStatus.SCHEDULED);

        AppointmentEntity confirmed = new AppointmentEntity(testUser, "Client Confirmed", "0555555555", date);
        confirmed.setStatus(AppointmentStatus.CONFIRMED);

        appointmentDao.save(scheduled);
        appointmentDao.save(confirmed);

        // Récupérer les rendez-vous programmés
        List<AppointmentEntity> scheduledAppointments = appointmentDao.findByProfessionalAndStatus(testUser, AppointmentStatus.SCHEDULED);
        assertEquals(1, scheduledAppointments.size());
        assertEquals("Client Scheduled", scheduledAppointments.get(0).getClientName());

        // Récupérer les rendez-vous confirmés
        List<AppointmentEntity> confirmedAppointments = appointmentDao.findByProfessionalAndStatus(testUser, AppointmentStatus.CONFIRMED);
        assertEquals(1, confirmedAppointments.size());
        assertEquals("Client Confirmed", confirmedAppointments.get(0).getClientName());
    }

    @Test
    void testFindByProfessionalAndAppointmentDateBetween() {
        // Créer des rendez-vous à différentes dates
        LocalDateTime startDate = LocalDateTime.now().plusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(3);
        LocalDateTime outsideDate = LocalDateTime.now().plusDays(5);

        AppointmentEntity withinRange1 = new AppointmentEntity(testUser, "Client Within 1", "0666666666", startDate);
        AppointmentEntity withinRange2 = new AppointmentEntity(testUser, "Client Within 2", "0777777777", endDate);
        AppointmentEntity outsideRange = new AppointmentEntity(testUser, "Client Outside", "0888888888", outsideDate);

        appointmentDao.save(withinRange1);
        appointmentDao.save(withinRange2);
        appointmentDao.save(outsideRange);

        // Récupérer les rendez-vous dans la plage
        List<AppointmentEntity> appointmentsInRange = appointmentDao.findByProfessionalAndAppointmentDateBetween(
            testUser, startDate.minusMinutes(1), endDate.plusMinutes(1));

        assertEquals(2, appointmentsInRange.size());
        assertTrue(appointmentsInRange.stream().anyMatch(a -> "Client Within 1".equals(a.getClientName())));
        assertTrue(appointmentsInRange.stream().anyMatch(a -> "Client Within 2".equals(a.getClientName())));
        assertFalse(appointmentsInRange.stream().anyMatch(a -> "Client Outside".equals(a.getClientName())));
    }
}