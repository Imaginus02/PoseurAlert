package com.reynaud.poseuralert.controller;

import com.reynaud.poseuralert.dao.UserDao;
import com.reynaud.poseuralert.model.Sector;
import com.reynaud.poseuralert.model.UserEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@CrossOrigin
@Controller
@RequestMapping("/profil")
public class ProfileController {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping
    public String showProfilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        System.out.println("=== PROFILE PAGE REQUESTED ===");

        // Récupérer l'UserEntity depuis la base de données
        UserEntity user = userDao.findByEmail(userDetails.getUsername());
        if (user == null) {
            System.err.println("ERROR: User not found in database for email: " + userDetails.getUsername());
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("sectors", Sector.values());
        return "profile";
    }

    @PostMapping
    @Transactional
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam String email,
                               @RequestParam(required = false) String currentPassword,
                               @RequestParam(required = false) String newPassword,
                               @RequestParam(required = false) String confirmPassword,
                               @RequestParam String companyName,
                               @RequestParam String sector,
                               @RequestParam(required = false) String address,
                               @RequestParam(required = false) String phoneNumber,
                               @RequestParam(required = false) String siret,
                               @RequestParam(required = false) String description,
                               @RequestParam(required = false) String businessHours,
                               @RequestParam(required = false) Boolean isPublicProfile,
                               RedirectAttributes redirectAttributes) {

        System.out.println("=== UPDATE PROFILE REQUESTED ===");

        try {
            // Récupérer l'UserEntity depuis la base de données
            UserEntity user = userDao.findByEmail(userDetails.getUsername());
            if (user == null) {
                System.err.println("ERROR: User not found in database for email: " + userDetails.getUsername());
                return "redirect:/login";
            }
            // Vérifier si l'email est déjà utilisé par un autre utilisateur
            UserEntity existingUser = userDao.findByEmail(email);
            if (existingUser != null && !existingUser.getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "Cette adresse email est déjà utilisée.");
                return "redirect:/profil";
            }

            // Vérifier le mot de passe actuel si un nouveau mot de passe est fourni
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                if (currentPassword == null || !passwordEncoder.matches(currentPassword, user.getPassword())) {
                    redirectAttributes.addFlashAttribute("error", "Le mot de passe actuel est incorrect.");
                    return "redirect:/profil";
                }
                if (!newPassword.equals(confirmPassword)) {
                    redirectAttributes.addFlashAttribute("error", "Les nouveaux mots de passe ne correspondent pas.");
                    return "redirect:/profil";
                }
                user.setPassword(passwordEncoder.encode(newPassword));
            }

            // Mettre à jour les informations de base
            user.setEmail(email);
            user.setCompanyName(companyName);
            user.setSector(Sector.valueOf(sector));
            user.setAddress(address);
            user.setPhoneNumber(phoneNumber);
            user.setSiret(siret);

            // Mettre à jour les informations de profil public
            user.setDescription(description);
            user.setBusinessHours(businessHours);
            user.setIsPublicProfile(isPublicProfile != null ? isPublicProfile : false);

            userDao.save(user);

            redirectAttributes.addFlashAttribute("success", "Votre profil a été mis à jour avec succès.");
            return "redirect:/profil";

        } catch (Exception e) {
            System.out.println("ERROR updating profile: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Une erreur est survenue lors de la mise à jour du profil.");
            return "redirect:/profil";
        }
    }

    @GetMapping("/public/{id}")
    public String showPublicProfile(@PathVariable Long id, Model model) {
        System.out.println("=== PUBLIC PROFILE REQUESTED ===");

        Optional<UserEntity> userOptional = userDao.findById(id);
        if (!userOptional.isPresent() || !Boolean.TRUE.equals(userOptional.get().getIsPublicProfile())) {
            return "redirect:/profil/public/not-found";
        }

        UserEntity user = userOptional.get();
        model.addAttribute("company", user);
        return "public-profile";
    }
}