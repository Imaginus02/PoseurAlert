package com.reynaud.poseuralert.controller;

import com.reynaud.poseuralert.dao.UserDao;
import com.reynaud.poseuralert.model.Sector;
import com.reynaud.poseuralert.model.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RegisterControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserDao userDao;

    private MockMvc mockMvc;

    @Autowired
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void testSuccessfulRegistrationForRestaurant() throws Exception {
        // Test d'inscription pour un restaurant
        mockMvc.perform(post("/inscription")
                .param("email", "restaurant@test.com")
                .param("password", "password123")
                .param("passwordConfirm", "password123")
                .param("companyName", "Le Bon Restaurant")
                .param("sector", "RESTAURANT")
                .param("address", "123 Rue de la Paix")
                .param("phoneNumber", "0123456789")
                .param("siret", "12345678901234")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("login"));

        // Vérifier que l'utilisateur a été créé en base
        UserEntity savedUser = userDao.findByEmail("restaurant@test.com");
        System.out.println("Looking for user: restaurant@test.com");
        System.out.println("Found user: " + savedUser);
        if (savedUser != null) {
            System.out.println("User company: " + savedUser.getCompanyName());
            System.out.println("User sector: " + savedUser.getSector());
        }
        assert savedUser != null;
        assert "Le Bon Restaurant".equals(savedUser.getCompanyName());
        assert Sector.RESTAURANT.equals(savedUser.getSector());
        assert "123 Rue de la Paix".equals(savedUser.getAddress());
        assert "0123456789".equals(savedUser.getPhoneNumber());
        assert "12345678901234".equals(savedUser.getSiret());
    }

    @Test
    void testSuccessfulRegistrationForHealthProfessional() throws Exception {
        // Test d'inscription pour un professionnel de santé
        mockMvc.perform(post("/inscription")
                .param("email", "medecin@test.com")
                .param("password", "password123")
                .param("passwordConfirm", "password123")
                .param("companyName", "Cabinet Médical Dupont")
                .param("sector", "HEALTH_PROFESSIONAL")
                .param("address", "456 Avenue des Médecins")
                .param("phoneNumber", "0987654321")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("login"));

        // Vérifier que l'utilisateur a été créé
        UserEntity savedUser = userDao.findByEmail("medecin@test.com");
        assert savedUser != null;
        assert "Cabinet Médical Dupont".equals(savedUser.getCompanyName());
        assert Sector.HEALTH_PROFESSIONAL.equals(savedUser.getSector());
    }

    @Test
    void testSuccessfulRegistrationForGarage() throws Exception {
        // Test d'inscription pour un garage
        mockMvc.perform(post("/inscription")
                .param("email", "garage@test.com")
                .param("password", "password123")
                .param("passwordConfirm", "password123")
                .param("companyName", "Garage Martin")
                .param("sector", "GARAGE")
                .param("address", "789 Rue des Mécaniciens")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("login"));

        UserEntity savedUser = userDao.findByEmail("garage@test.com");
        assert savedUser != null;
        assert Sector.GARAGE.equals(savedUser.getSector());
    }

    @Test
    void testSuccessfulRegistrationForHairdresser() throws Exception {
        // Test d'inscription pour un coiffeur
        mockMvc.perform(post("/inscription")
                .param("email", "coiffeur@test.com")
                .param("password", "password123")
                .param("passwordConfirm", "password123")
                .param("companyName", "Salon de Coiffure Marie")
                .param("sector", "HAIRDRESSER")
                .param("address", "321 Boulevard des Coiffeurs")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("login"));

        UserEntity savedUser = userDao.findByEmail("coiffeur@test.com");
        assert savedUser != null;
        assert Sector.HAIRDRESSER.equals(savedUser.getSector());
    }

    @Test
    void testSuccessfulRegistrationForBeautyInstitute() throws Exception {
        // Test d'inscription pour un institut de beauté
        mockMvc.perform(post("/inscription")
                .param("email", "beaute@test.com")
                .param("password", "password123")
                .param("passwordConfirm", "password123")
                .param("companyName", "Institut de Beauté Éclat")
                .param("sector", "BEAUTY_INSTITUTE")
                .param("address", "654 Avenue du Spa")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("login"));

        UserEntity savedUser = userDao.findByEmail("beaute@test.com");
        assert savedUser != null;
        assert Sector.BEAUTY_INSTITUTE.equals(savedUser.getSector());
    }

    @Test
    void testSuccessfulRegistrationForVeterinarian() throws Exception {
        // Test d'inscription pour un vétérinaire
        mockMvc.perform(post("/inscription")
                .param("email", "veto@test.com")
                .param("password", "password123")
                .param("passwordConfirm", "password123")
                .param("companyName", "Clinique Vétérinaire des Animaux")
                .param("sector", "VETERINARIAN")
                .param("address", "987 Rue des Vétérinaires")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("login"));

        UserEntity savedUser = userDao.findByEmail("veto@test.com");
        assert savedUser != null;
        assert Sector.VETERINARIAN.equals(savedUser.getSector());
    }

    @Test
    void testRegistrationWithPasswordMismatch() throws Exception {
        // Test avec mots de passe différents
        mockMvc.perform(post("/inscription")
                .param("email", "test@test.com")
                .param("password", "password123")
                .param("passwordConfirm", "differentpassword")
                .param("companyName", "Test Company")
                .param("sector", "RESTAURANT")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/inscription?error=true"));
    }

    @Test
    void testRegistrationWithMissingRequiredFields() throws Exception {
        // Test avec champs obligatoires manquants
        mockMvc.perform(post("/inscription")
                .param("email", "test@test.com")
                .param("password", "password123")
                .param("passwordConfirm", "password123")
                // companyName manquant
                .param("sector", "RESTAURANT")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/inscription?error=true"));
    }
}