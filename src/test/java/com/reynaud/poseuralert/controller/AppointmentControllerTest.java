package com.reynaud.poseuralert.controller;

import com.reynaud.poseuralert.dao.*;
import com.reynaud.poseuralert.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AppointmentControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AppointmentDao appointmentDao;

    @Autowired
    private ClientPhoneDao clientPhoneDao;

    @Autowired
    private ReportDao reportDao;

    @Autowired
    private AuditLogDao auditLogDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private UserEntity restaurantUser;
    private UserEntity healthUser;

    @Autowired
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp() {
        // Créer des utilisateurs de test pour différents secteurs
        restaurantUser = userDao.findByEmail("restaurant@test.com");
        if (restaurantUser == null) {
            restaurantUser = new UserEntity("restaurant@test.com", passwordEncoder.encode("password123"),
                                          "Le Bon Restaurant", Sector.RESTAURANT);
            userDao.save(restaurantUser);
        }

        healthUser = userDao.findByEmail("health@test.com");
        if (healthUser == null) {
            healthUser = new UserEntity("health@test.com", passwordEncoder.encode("password123"),
                                       "Cabinet Médical", Sector.HEALTH_PROFESSIONAL);
            userDao.save(healthUser);
        }
    }

    @Test
    void testListAppointmentsForRestaurant() throws Exception {
        // Créer quelques rendez-vous de test pour le restaurant
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        AppointmentEntity appointment = new AppointmentEntity(restaurantUser, "Jean Dupont", "0123456789", futureDate);
        appointmentDao.save(appointment);

        mockMvc.perform(get("/rendez-vous")
                .with(user(restaurantUser.getEmail()).password("password123").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments"))
                .andExpect(model().attributeExists("appointments"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("sectorLabels"));
    }

    @Test
    void testCreateAppointmentForRestaurant() throws Exception {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        String dateTimeString = futureDate.toString().replace("T", "T").substring(0, 16);

        mockMvc.perform(post("/rendez-vous/nouveau")
                .param("clientName", "Marie Martin")
                .param("clientPhone", "0987654321")
                .param("appointmentDateTime", dateTimeString)
                .param("notes", "Réservation pour anniversaire")
                .with(user(restaurantUser.getEmail()).password("password123").roles("USER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rendez-vous"))
                .andExpect(flash().attribute("success", "Rendez-vous créé avec succès."));

        // Vérifier que le rendez-vous a été créé
        AppointmentEntity savedAppointment = appointmentDao.findByProfessionalAndClientPhone(restaurantUser, "0987654321").get(0);
        assert savedAppointment != null;
        assert "Marie Martin".equals(savedAppointment.getClientName());
        assert "0987654321".equals(savedAppointment.getClientPhone());
        assert "Réservation pour anniversaire".equals(savedAppointment.getNotes());
    }

    @Test
    void testCreateAppointmentForHealthProfessional() throws Exception {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        String dateTimeString = futureDate.toString().replace("T", "T").substring(0, 16);

        mockMvc.perform(post("/rendez-vous/nouveau")
                .param("clientName", "Pierre Durand")
                .param("clientPhone", "0111111111")
                .param("appointmentDateTime", dateTimeString)
                .param("notes", "Consultation de suivi")
                .with(user(healthUser.getEmail()).password("password123").roles("USER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rendez-vous"));

        // Vérifier que l'audit a été créé pour les professionnels de santé
        AuditLogEntity auditLog = auditLogDao.findByProfessional(healthUser).get(0);
        assert auditLog != null;
        assert "CREATE".equals(auditLog.getAction());
        assert "APPOINTMENT".equals(auditLog.getResourceType());
        assert auditLog.getDetails().contains("Création de rendez-vous pour Pierre Durand");
    }

    @Test
    void testCreateAppointmentWithFlaggedPhoneNumber() throws Exception {
        // Créer un numéro déjà signalé
        ClientPhoneEntity flaggedPhone = new ClientPhoneEntity("0666666666");
        flaggedPhone.incrementReportCount(); // Premier signalement
        flaggedPhone.incrementReportCount(); // Deuxième signalement - devient flagged
        clientPhoneDao.save(flaggedPhone);

        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        String dateTimeString = futureDate.toString().replace("T", "T").substring(0, 16);

        mockMvc.perform(post("/rendez-vous/nouveau")
                .param("clientName", "Jean Flagged")
                .param("clientPhone", "0666666666")
                .param("appointmentDateTime", dateTimeString)
                .with(user(restaurantUser.getEmail()).password("password123").roles("USER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("warning"));
    }

    @Test
    void testReportAppointmentForRestaurant() throws Exception {
        // Créer un rendez-vous à signaler
        LocalDateTime pastDate = LocalDateTime.now().minusHours(2);
        AppointmentEntity appointment = new AppointmentEntity(restaurantUser, "Bad Client", "0777777777", pastDate);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        AppointmentEntity savedAppointment = appointmentDao.save(appointment);

        mockMvc.perform(post("/rendez-vous/" + savedAppointment.getId() + "/signaler")
                .param("reason", "NO_SHOW")
                .param("additionalNotes", "Client n'est jamais venu")
                .with(user(restaurantUser.getEmail()).password("password123").roles("USER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("success", "Signalement enregistré. Le numéro est maintenant surveillé."));

        // Vérifier que le signalement a été créé
        ReportEntity report = reportDao.findByReportedPhone("0777777777").get(0);
        assert report != null;
        assert ReportReason.NO_SHOW.equals(report.getReason());
        assert "Client n'est jamais venu".equals(report.getAdditionalNotes());

        // Vérifier que le numéro est maintenant surveillé
        ClientPhoneEntity clientPhone = clientPhoneDao.findByPhoneNumber("0777777777").orElse(null);
        assert clientPhone != null;
        assert clientPhone.getReportCount() == 1;
    }

    @Test
    void testReportAppointmentForHealthProfessionalCreatesAudit() throws Exception {
        // Créer un rendez-vous à signaler
        LocalDateTime pastDate = LocalDateTime.now().minusHours(2);
        AppointmentEntity appointment = new AppointmentEntity(healthUser, "Patient Absent", "0888888888", pastDate);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        AppointmentEntity savedAppointment = appointmentDao.save(appointment);

        mockMvc.perform(post("/rendez-vous/" + savedAppointment.getId() + "/signaler")
                .param("reason", "NO_SHOW")
                .param("additionalNotes", "Patient n'est pas venu à la consultation")
                .with(user(healthUser.getEmail()).password("password123").roles("USER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Vérifier que l'audit a été créé
        AuditLogEntity auditLog = auditLogDao.findByProfessionalAndResourceType(healthUser, "APPOINTMENT").stream()
                .filter(log -> "REPORT".equals(log.getAction()))
                .findFirst().orElse(null);
        assert auditLog != null;
        assert auditLog.getDetails().contains("Signalement de rendez-vous - Motif: Absence sans prévenir");
    }

    @Test
    void testUpdateAppointmentStatus() throws Exception {
        // Créer un rendez-vous
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        AppointmentEntity appointment = new AppointmentEntity(restaurantUser, "Test Client", "0999999999", futureDate);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        AppointmentEntity savedAppointment = appointmentDao.save(appointment);

        mockMvc.perform(post("/rendez-vous/" + savedAppointment.getId() + "/statut")
                .param("status", "CONFIRMED")
                .with(user(restaurantUser.getEmail()).password("password123").roles("USER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("success", "Statut du rendez-vous mis à jour."));

        // Vérifier que le statut a été mis à jour
        AppointmentEntity updatedAppointment = appointmentDao.findById(savedAppointment.getId()).orElse(null);
        assert updatedAppointment != null;
        assert AppointmentStatus.CONFIRMED.equals(updatedAppointment.getStatus());
    }

    @Test
    void testAccessAppointmentOfAnotherUserForbidden() throws Exception {
        // Créer un rendez-vous pour le restaurant
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        AppointmentEntity appointment = new AppointmentEntity(restaurantUser, "Test Client", "0555555555", futureDate);
        AppointmentEntity savedAppointment = appointmentDao.save(appointment);

        // Essayer d'y accéder avec l'utilisateur santé
        mockMvc.perform(post("/rendez-vous/" + savedAppointment.getId() + "/statut")
                .param("status", "CONFIRMED")
                .with(user(healthUser.getEmail()).password("password123").roles("USER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("error", "Accès non autorisé."));
    }
}