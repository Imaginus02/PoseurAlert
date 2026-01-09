package com.reynaud.poseuralert.integration;

import com.reynaud.poseuralert.dao.*;
import com.reynaud.poseuralert.model.*;
import com.reynaud.poseuralert.util.SectorLabels;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CompleteWorkflowTest {

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

    @Autowired
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void testCompleteWorkflowForRestaurant() throws Exception {
        // 1. Inscription d'un restaurant
        mockMvc.perform(post("/inscription")
                .param("email", "workflow.restaurant@test.com")
                .param("password", "password123")
                .param("passwordConfirm", "password123")
                .param("companyName", "Restaurant Workflow Test")
                .param("sector", "RESTAURANT")
                .param("address", "123 Rue du Test")
                .param("phoneNumber", "0123456789")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("login"));

        // Vérifier que l'utilisateur a été créé
        UserEntity restaurant = userDao.findByEmail("workflow.restaurant@test.com");
        assertNotNull(restaurant);
        assertEquals(Sector.RESTAURANT, restaurant.getSector());
        assertEquals("Restaurant Workflow Test", restaurant.getCompanyName());

        // 2. Créer un premier rendez-vous
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        String dateTimeString = futureDate.toString().replace("T", "T").substring(0, 16);

        mockMvc.perform(post("/rendez-vous/nouveau")
                .param("clientName", "Jean Test")
                .param("clientPhone", "0666666666")
                .param("appointmentDateTime", dateTimeString)
                .param("notes", "Réservation anniversaire")
                .with(user(restaurant.getEmail()).password("password123").roles("USER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rendez-vous"));

        // Vérifier que le rendez-vous a été créé
        AppointmentEntity appointment = appointmentDao.findByProfessionalAndClientPhone(restaurant, "0666666666").get(0);
        assertNotNull(appointment);
        assertEquals("Jean Test", appointment.getClientName());
        assertEquals("0666666666", appointment.getClientPhone());

        // 3. Signaler le rendez-vous comme absence
        mockMvc.perform(post("/rendez-vous/" + appointment.getId() + "/signaler")
                .param("reason", "NO_SHOW")
                .param("additionalNotes", "Client n'est jamais venu")
                .with(user(restaurant.getEmail()).password("password123").roles("USER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("success", "Signalement enregistré. Le numéro est maintenant surveillé."));

        // Vérifier que le signalement a été créé et que le numéro est surveillé
        ReportEntity report = reportDao.findByReportedPhone("0666666666").get(0);
        assertNotNull(report);
        assertEquals(ReportReason.NO_SHOW, report.getReason());

        ClientPhoneEntity flaggedPhone = clientPhoneDao.findByPhoneNumber("0666666666").orElse(null);
        assertNotNull(flaggedPhone);
        assertEquals(1, flaggedPhone.getReportCount());
        assertFalse(flaggedPhone.getIsFlagged()); // 1 signalement = pas encore flagged

        // 4. Créer un deuxième rendez-vous avec le même numéro (devrait passer sans alerte)
        LocalDateTime secondDate = LocalDateTime.now().plusDays(2);
        String secondDateTimeString = secondDate.toString().replace("T", "T").substring(0, 16);

        mockMvc.perform(post("/rendez-vous/nouveau")
                .param("clientName", "Jean Test")
                .param("clientPhone", "0666666666")
                .param("appointmentDateTime", secondDateTimeString)
                .param("notes", "Deuxième réservation")
                .with(user(restaurant.getEmail()).password("password123").roles("USER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rendez-vous"));
    }

    @Test
    void testCompleteWorkflowForHealthProfessionalWithAudit() throws Exception {
        // 1. Inscription d'un professionnel de santé
        mockMvc.perform(post("/inscription")
                .param("email", "workflow.health@test.com")
                .param("password", "password123")
                .param("passwordConfirm", "password123")
                .param("companyName", "Cabinet Médical Workflow")
                .param("sector", "HEALTH_PROFESSIONAL")
                .param("address", "456 Avenue des Médecins")
                .param("phoneNumber", "0987654321")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("login"));

        UserEntity healthProfessional = userDao.findByEmail("workflow.health@test.com");
        assertNotNull(healthProfessional);
        assertEquals(Sector.HEALTH_PROFESSIONAL, healthProfessional.getSector());

        // Vérifier que les adaptations HDS sont actives
        assertTrue(SectorLabels.requiresHDSCompliance(healthProfessional.getSector()));
        assertNotNull(SectorLabels.getHDSWarning(healthProfessional.getSector()));

        // 2. Accéder à la page des rendez-vous (devrait créer un log d'audit)
        mockMvc.perform(get("/rendez-vous")
                .with(user(healthProfessional.getEmail()).password("password123").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("HDS")));

        // Vérifier que l'audit a été créé
        AuditLogEntity viewAudit = auditLogDao.findByProfessional(healthProfessional).stream()
                .filter(log -> "VIEW".equals(log.getAction()) && "APPOINTMENTS".equals(log.getResourceType()))
                .findFirst().orElse(null);
        assertNotNull(viewAudit);
        assertEquals("Consultation de la liste des rendez-vous", viewAudit.getDetails());

        // 3. Créer un rendez-vous (devrait créer un log d'audit)
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        String dateTimeString = futureDate.toString().replace("T", "T").substring(0, 16);

        mockMvc.perform(post("/rendez-vous/nouveau")
                .param("clientName", "Patient Test")
                .param("clientPhone", "0777777777")
                .param("appointmentDateTime", dateTimeString)
                .param("notes", "Consultation de suivi")
                .with(user(healthProfessional.getEmail()).password("password123").roles("USER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rendez-vous"));

        // Vérifier que le rendez-vous a été créé et que l'audit existe
        AppointmentEntity appointment = appointmentDao.findByProfessionalAndClientPhone(healthProfessional, "0777777777").get(0);
        assertNotNull(appointment);
        assertEquals("Patient Test", appointment.getClientName());

        AuditLogEntity createAudit = auditLogDao.findByProfessional(healthProfessional).stream()
                .filter(log -> "CREATE".equals(log.getAction()) && "APPOINTMENT".equals(log.getResourceType()))
                .findFirst().orElse(null);
        assertNotNull(createAudit);
        assertTrue(createAudit.getDetails().contains("Patient Test"));
        assertTrue(createAudit.getDetails().contains("0777777777"));
    }

    @Test
    void testFlaggedPhoneNumberAlert() throws Exception {
        // Créer un utilisateur restaurant
        UserEntity restaurant = new UserEntity("flagged.test@test.com", passwordEncoder.encode("password123"),
                                             "Restaurant Flagged Test", Sector.RESTAURANT);
        restaurant = userDao.save(restaurant);

        // Créer un numéro déjà signalé 2 fois (flagged)
        ClientPhoneEntity flaggedPhone = new ClientPhoneEntity("0555555555");
        flaggedPhone.incrementReportCount();
        flaggedPhone.incrementReportCount();
        clientPhoneDao.save(flaggedPhone);

        // Tenter de créer un rendez-vous avec ce numéro
        LocalDateTime futureDate = LocalDateTime.now().plusDays(1);
        String dateTimeString = futureDate.toString().replace("T", "T").substring(0, 16);

        mockMvc.perform(post("/rendez-vous/nouveau")
                .param("clientName", "Client Suspect")
                .param("clientPhone", "0555555555")
                .param("appointmentDateTime", dateTimeString)
                .with(user(restaurant.getEmail()).password("password123").roles("USER"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("warning"));

        // Vérifier que le rendez-vous a quand même été créé malgré l'avertissement
        AppointmentEntity appointment = appointmentDao.findByProfessionalAndClientPhone(restaurant, "0555555555").get(0);
        assertNotNull(appointment);
        assertEquals("Client Suspect", appointment.getClientName());
    }

    @Test
    void testInterfaceLabelsForAllSectors() throws Exception {
        // Créer des utilisateurs pour tous les secteurs et tester les étiquettes
        String[] sectors = {"RESTAURANT", "HEALTH_PROFESSIONAL", "GARAGE", "HAIRDRESSER", "BEAUTY_INSTITUTE", "VETERINARIAN"};
        String[] expectedLabels = {"réservations", "consultations", "interventions", "rendez-vous", "soins", "consultations"};
        String[] expectedClientLabels = {"client", "patient", "client", "client", "client", "propriétaire"};

        for (int i = 0; i < sectors.length; i++) {
            String email = "labeltest" + i + "@test.com";
            String companyName = "Company " + i;

            // Inscription
            mockMvc.perform(post("/inscription")
                    .param("email", email)
                    .param("password", "password123")
                    .param("passwordConfirm", "password123")
                    .param("companyName", companyName)
                    .param("sector", sectors[i])
                    .with(csrf()))
                    .andExpect(status().is3xxRedirection());

            UserEntity user = userDao.findByEmail(email);
            assertNotNull(user);

            // Tester les étiquettes dans les vues
            mockMvc.perform(get("/rendez-vous")
                    .with(user(email).password("password123").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Mes " + expectedLabels[i])));

            // Tester les étiquettes dans le formulaire de nouveau rendez-vous
            mockMvc.perform(get("/rendez-vous/nouveau")
                    .with(user(email).password("password123").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(content().string(org.hamcrest.Matchers.containsString("Nom du " + expectedClientLabels[i])));
        }
    }
}