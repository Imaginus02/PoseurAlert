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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LoginControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserDao userDao;

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

    @BeforeEach
    void setUp() {
        // Créer un utilisateur de test pour les connexions
        UserEntity testUser = userDao.findByEmail("test@example.com");
        if (testUser == null) {
            testUser = new UserEntity("test@example.com", passwordEncoder.encode("password123"), "Test Company", Sector.RESTAURANT);
            userDao.save(testUser);
        }
    }

    @Test
    void testLoginPageAccess() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void testSuccessfulLogin() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "test@example.com")
                .param("password", "password123")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void testFailedLoginWithWrongPassword() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "test@example.com")
                .param("password", "wrongpassword")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void testFailedLoginWithNonExistentUser() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "nonexistent@example.com")
                .param("password", "password123")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void testAccessToHomePageRedirectsToAppointmentsWhenLoggedIn() throws Exception {
        // Tester que la page d'accueil redirige vers les rendez-vous quand connecté
        mockMvc.perform(get("/")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("test@example.com").password("password123").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rendez-vous"));
    }

    @Test
    void testAccessToHomePageShowsIndexWhenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
}