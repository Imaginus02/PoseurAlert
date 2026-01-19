package com.reynaud.poseuralert.controller;

import com.reynaud.poseuralert.dao.UserDao;
import com.reynaud.poseuralert.model.Sector;
import com.reynaud.poseuralert.model.UserEntity;
import com.reynaud.poseuralert.util.logging.Loggers;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@CrossOrigin
@Controller
@RequestMapping("/inscription")
public class RegisterController {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;


    public RegisterController(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    @Transactional
    public String registerNewUser(@RequestParam String email,
                                       @RequestParam String password,
                                       @RequestParam String passwordConfirm,
                                       @RequestParam String companyName,
                                       @RequestParam String sector,
                                       @RequestParam(required = false) String address,
                                       @RequestParam(required = false) String phoneNumber,
                                       @RequestParam(required = false) String siret) {

        Loggers.business().info("REGISTRATION ATTEMPT email={} company={} sector={} phone={} siret={} ", email, companyName, sector, phoneNumber, (siret != null ? "[PRESENT]" : "[NULL]"));

        // Verify if the user entered the same password twice
        if (password == null || passwordConfirm == null || !password.equals(passwordConfirm)) {
            Loggers.access().warn("REGISTRATION FAILED: password mismatch for {}", email);
            return "redirect:/inscription?error=true";
        }

        // Verify required fields
        if (companyName == null || companyName.trim().isEmpty() || sector == null || sector.trim().isEmpty()) {
            Loggers.business().warn("REGISTRATION FAILED: missing fields email={}", email);
            return "redirect:/inscription?error=true";
        }

        // Convert string to Sector enum
        Sector sectorEnum;
        try {
            sectorEnum = Sector.valueOf(sector);
            Loggers.technical().debug("Sector parsed: {}", sectorEnum);
        } catch (IllegalArgumentException e) {
            Loggers.business().warn("REGISTRATION FAILED: invalid sector value {} for email {}", sector, email);
            return "redirect:/inscription?error=true";
        }

        // Validate SIRET if provided
        if (siret != null && !siret.trim().isEmpty()) {
            // SIRET should be exactly 14 digits for French companies
            if (!siret.matches("\\d{14}")) {
                return "redirect:/inscription?error=true";
            }
        }

        // Hash the password before saving
        String hashedPassword = passwordEncoder.encode(password);
        Loggers.diagnostic().debug("Password hashed with {}", passwordEncoder.getClass().getSimpleName());

        // Save the new user into the database
        UserEntity user = new UserEntity(email, hashedPassword, companyName, sectorEnum);
        user.setAddress(address);
        user.setPhoneNumber(phoneNumber);
        user.setSiret(siret);

        try {
            UserEntity savedUser = userDao.save(user);
            // Forcer l'écriture immédiate en base
            entityManager.flush();
            Loggers.business().info("USER REGISTERED email={} id={}", savedUser.getEmail(), savedUser.getId());

            // Vérification supplémentaire : récupérer l'utilisateur depuis la base
            UserEntity retrievedUser = userDao.findByEmail(email);
            if (retrievedUser != null) {
                Loggers.technical().info("VERIFIED USER PERSISTENCE email={} sector={} ", retrievedUser.getEmail(), retrievedUser.getSector());
            } else {
                Loggers.technical().error("User not found after save email={}", email);
                return "redirect:/inscription?error=true";
            }
        } catch (Exception e) {
            Loggers.technical().error("Error saving user email={} cause={}", email, e.getMessage());
            return "redirect:/inscription?error=true";
        }
        Loggers.business().info("REGISTRATION COMPLETED email={}", email);
        return "login";
    }
}
