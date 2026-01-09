package com.reynaud.poseuralert.controller;

import com.reynaud.poseuralert.dao.UserDao;
import com.reynaud.poseuralert.model.Sector;
import com.reynaud.poseuralert.model.UserEntity;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ViewControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;
    private UserEntity restaurantUser;
    private UserEntity healthUser;
    private UserEntity garageUser;
    private UserEntity hairdresserUser;
    private UserEntity beautyUser;
    private UserEntity vetUser;

    @Autowired
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @BeforeEach
    void setUp() {
        // Créer des utilisateurs pour tous les secteurs
        restaurantUser = createUserIfNotExists("restaurant@test.com", "Restaurant Test", Sector.RESTAURANT);
        healthUser = createUserIfNotExists("health@test.com", "Cabinet Médical Test", Sector.HEALTH_PROFESSIONAL);
        garageUser = createUserIfNotExists("garage@test.com", "Garage Test", Sector.GARAGE);
        hairdresserUser = createUserIfNotExists("hairdresser@test.com", "Salon Coiffure Test", Sector.HAIRDRESSER);
        beautyUser = createUserIfNotExists("beauty@test.com", "Institut Beauté Test", Sector.BEAUTY_INSTITUTE);
        vetUser = createUserIfNotExists("vet@test.com", "Clinique Vétérinaire Test", Sector.VETERINARIAN);
    }

    private UserEntity createUserIfNotExists(String email, String companyName, Sector sector) {
        UserEntity user = userDao.findByEmail(email);
        if (user == null) {
            user = new UserEntity(email, passwordEncoder.encode("password123"), companyName, sector);
            userDao.save(user);
        }
        return user;
    }

    @Test
    void testHomePageShowsIndexWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("sectors"));
    }

    @Test
    void testHomePageRedirectsToAppointmentsWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/")
                .with(user(restaurantUser.getEmail()).password("password123").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rendez-vous"));
    }

    @Test
    void testLoginPageAccess() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void testRegistrationPageAccess() throws Exception {
        mockMvc.perform(get("/inscription"))
                .andExpect(status().isOk())
                .andExpect(view().name("inscription"));
    }

    // Tests des adaptations d'interface pour les rendez-vous selon les secteurs
    @Test
    void testAppointmentsPageForRestaurant() throws Exception {
        mockMvc.perform(get("/rendez-vous")
                .with(user(restaurantUser.getEmail()).password("password123").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments"))
                .andExpect(model().attribute("user", restaurantUser))
                .andExpect(model().attributeExists("sectorLabels"))
                // Vérifier que les bonnes étiquettes sont passées au modèle
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Mes réservations")));
    }

    @Test
    void testAppointmentsPageForHealthProfessional() throws Exception {
        mockMvc.perform(get("/rendez-vous")
                .with(user(healthUser.getEmail()).password("password123").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments"))
                .andExpect(model().attribute("user", healthUser))
                .andExpect(model().attributeExists("sectorLabels"))
                // Vérifier que les avertissements HDS sont présents
                .andExpect(content().string(org.hamcrest.Matchers.containsString("HDS")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("données médicales")));
    }

    @Test
    void testAppointmentsPageForGarage() throws Exception {
        mockMvc.perform(get("/rendez-vous")
                .with(user(garageUser.getEmail()).password("password123").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments"))
                .andExpect(model().attribute("user", garageUser))
                .andExpect(model().attributeExists("sectorLabels"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Mes interventions")));
    }

    @Test
    void testAppointmentsPageForHairdresser() throws Exception {
        mockMvc.perform(get("/rendez-vous")
                .with(user(hairdresserUser.getEmail()).password("password123").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments"))
                .andExpect(model().attribute("user", hairdresserUser))
                .andExpect(model().attributeExists("sectorLabels"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Mes rendez-vous")));
    }

    @Test
    void testAppointmentsPageForBeautyInstitute() throws Exception {
        mockMvc.perform(get("/rendez-vous")
                .with(user(beautyUser.getEmail()).password("password123").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments"))
                .andExpect(model().attribute("user", beautyUser))
                .andExpect(model().attributeExists("sectorLabels"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Mes soins")));
    }

    @Test
    void testAppointmentsPageForVeterinarian() throws Exception {
        mockMvc.perform(get("/rendez-vous")
                .with(user(vetUser.getEmail()).password("password123").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments"))
                .andExpect(model().attribute("user", vetUser))
                .andExpect(model().attributeExists("sectorLabels"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Mes consultations")));
    }

    // Tests des formulaires de nouveau rendez-vous selon les secteurs
    @Test
    void testNewAppointmentFormForRestaurant() throws Exception {
        mockMvc.perform(get("/rendez-vous/nouveau")
                .with(user(restaurantUser.getEmail()).password("password123").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("new-appointment"))
                .andExpect(model().attribute("user", restaurantUser))
                .andExpect(model().attributeExists("sectorLabels"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nouvelle réservation")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Date et heure de la réservation")));
    }

    @Test
    void testNewAppointmentFormForHealthProfessional() throws Exception {
        mockMvc.perform(get("/rendez-vous/nouveau")
                .with(user(healthUser.getEmail()).password("password123").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments"))
                .andExpect(model().attribute("user", healthUser))
                .andExpect(model().attributeExists("sectorLabels"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nouvelle consultation")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Date et heure de la consultation")))
                // Vérifier les avertissements HDS
                .andExpect(content().string(org.hamcrest.Matchers.containsString("HDS")));
    }

    @Test
    void testNewAppointmentFormForGarage() throws Exception {
        mockMvc.perform(get("/rendez-vous/nouveau")
                .with(user(garageUser.getEmail()).password("password123").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("new-appointment"))
                .andExpect(model().attribute("user", garageUser))
                .andExpect(model().attributeExists("sectorLabels"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nouvelle intervention")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Date et heure de l'intervention")));
    }

    @Test
    void testNewAppointmentFormForHairdresser() throws Exception {
        mockMvc.perform(get("/rendez-vous/nouveau")
                .with(user(hairdresserUser.getEmail()).password("password123").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("new-appointment"))
                .andExpect(model().attribute("user", hairdresserUser))
                .andExpect(model().attributeExists("sectorLabels"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nouveau rendez-vous")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Date et heure du rendez-vous")));
    }

    @Test
    void testNewAppointmentFormForBeautyInstitute() throws Exception {
        mockMvc.perform(get("/rendez-vous/nouveau")
                .with(user(beautyUser.getEmail()).password("password123").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("new-appointment"))
                .andExpect(model().attribute("user", beautyUser))
                .andExpect(model().attributeExists("sectorLabels"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nouveau soin")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Date et heure du soin")));
    }

    @Test
    void testNewAppointmentFormForVeterinarian() throws Exception {
        mockMvc.perform(get("/rendez-vous/nouveau")
                .with(user(vetUser.getEmail()).password("password123").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("new-appointment"))
                .andExpect(model().attribute("user", vetUser))
                .andExpect(model().attributeExists("sectorLabels"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nouvelle consultation")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Date et heure de la consultation")));
    }
}