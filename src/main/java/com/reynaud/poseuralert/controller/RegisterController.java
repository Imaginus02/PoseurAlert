package com.reynaud.poseuralert.controller;

import com.reynaud.poseuralert.dao.UserDao;
import com.reynaud.poseuralert.model.Sector;
import com.reynaud.poseuralert.model.UserEntity;
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

        // Debug logging
        System.out.println("=== REGISTER CONTROLLER CALLED ===");
        System.out.println("Email: " + email);
        System.out.println("Password: " + (password != null ? "[PRESENT]" : "[NULL]"));
        System.out.println("PasswordConfirm: " + (passwordConfirm != null ? "[PRESENT]" : "[NULL]"));
        System.out.println("CompanyName: " + companyName);
        System.out.println("Sector: " + sector);
        System.out.println("Address: " + address);
        System.out.println("PhoneNumber: " + phoneNumber);
        System.out.println("Siret: " + siret);

        // Verify if the user entered the same password twice
        if (password == null || passwordConfirm == null || !password.equals(passwordConfirm)) {
            System.out.println("ERROR: Password mismatch or null");
            return "redirect:/inscription?error=true";
        }

        // Verify required fields
        if (companyName == null || companyName.trim().isEmpty() || sector == null || sector.trim().isEmpty()) {
            System.out.println("ERROR: Required fields missing");
            return "redirect:/inscription?error=true";
        }

        // Convert string to Sector enum
        Sector sectorEnum;
        try {
            sectorEnum = Sector.valueOf(sector);
            System.out.println("Sector enum converted successfully: " + sectorEnum);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: Invalid sector value: " + sector);
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
        System.out.println("Password hashed successfully with encoder: " + passwordEncoder.getClass().getSimpleName());
        System.out.println("Hashed password starts with: " + hashedPassword.substring(0, 10) + "...");

        // Save the new user into the database
        UserEntity user = new UserEntity(email, hashedPassword, companyName, sectorEnum);
        user.setAddress(address);
        user.setPhoneNumber(phoneNumber);
        user.setSiret(siret);

        try {
            UserEntity savedUser = userDao.save(user);
            // Forcer l'écriture immédiate en base
            entityManager.flush();
            System.out.println("User saved and flushed successfully with ID: " + savedUser.getId());

            // Vérification supplémentaire : récupérer l'utilisateur depuis la base
            UserEntity retrievedUser = userDao.findByEmail(email);
            if (retrievedUser != null) {
                System.out.println("VERIFICATION: User found in database with email: " + retrievedUser.getEmail() +
                                 ", company: " + retrievedUser.getCompanyName() +
                                 ", sector: " + retrievedUser.getSector());
            } else {
                System.out.println("ERROR: User not found in database after save!");
                return "redirect:/inscription?error=true";
            }
        } catch (Exception e) {
            System.out.println("ERROR saving user: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/inscription?error=true";
        }

        System.out.println("=== REGISTER PROCESS COMPLETED SUCCESSFULLY ===");
        return "login";
    }
}
