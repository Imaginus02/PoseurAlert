package com.reynaud.poseuralert.dao;

import com.reynaud.poseuralert.model.Sector;
import com.reynaud.poseuralert.model.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserDaoTest {

    @Autowired
    private UserDao userDao;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void testSaveAndFindUser() {
        // Créer un utilisateur
        UserEntity user = new UserEntity("test@example.com", passwordEncoder.encode("password123"),
                                       "Test Company", Sector.RESTAURANT);
        user.setAddress("123 Test Street");
        user.setPhoneNumber("0123456789");
        user.setSiret("12345678901234");

        // Sauvegarder
        UserEntity savedUser = userDao.save(user);

        // Vérifier que l'utilisateur a été sauvegardé avec un ID
        assertNotNull(savedUser.getId());

        // Récupérer par email
        UserEntity foundUser = userDao.findByEmail("test@example.com");

        // Vérifier que l'utilisateur a été trouvé
        assertNotNull(foundUser);
        assertEquals("test@example.com", foundUser.getEmail());
        assertEquals("Test Company", foundUser.getCompanyName());
        assertEquals(Sector.RESTAURANT, foundUser.getSector());
        assertEquals("123 Test Street", foundUser.getAddress());
        assertEquals("0123456789", foundUser.getPhoneNumber());
        assertEquals("12345678901234", foundUser.getSiret());
    }

    @Test
    void testFindByEmailWhenUserDoesNotExist() {
        UserEntity foundUser = userDao.findByEmail("nonexistent@example.com");
        assertNull(foundUser);
    }

    @Test
    void testSaveUsersForAllSectors() {
        // Créer et sauvegarder des utilisateurs pour tous les secteurs
        String[] emails = {
            "restaurant@test.com", "health@test.com", "garage@test.com",
            "hairdresser@test.com", "beauty@test.com", "vet@test.com"
        };

        Sector[] sectors = {
            Sector.RESTAURANT, Sector.HEALTH_PROFESSIONAL, Sector.GARAGE,
            Sector.HAIRDRESSER, Sector.BEAUTY_INSTITUTE, Sector.VETERINARIAN
        };

        String[] companyNames = {
            "Le Bon Restaurant", "Cabinet Médical", "Garage Martin",
            "Salon Coiffure", "Institut Beauté", "Clinique Vétérinaire"
        };

        for (int i = 0; i < emails.length; i++) {
            UserEntity user = new UserEntity(emails[i], passwordEncoder.encode("password123"),
                                           companyNames[i], sectors[i]);
            UserEntity savedUser = userDao.save(user);

            // Vérifier que l'utilisateur a été sauvegardé
            assertNotNull(savedUser.getId());
            assertEquals(sectors[i], savedUser.getSector());

            // Vérifier qu'on peut le récupérer
            UserEntity foundUser = userDao.findByEmail(emails[i]);
            assertNotNull(foundUser);
            assertEquals(companyNames[i], foundUser.getCompanyName());
        }
    }
}