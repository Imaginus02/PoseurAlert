package com.reynaud.poseuralert.controller;

import com.reynaud.poseuralert.dao.CommercialContactDao;
import com.reynaud.poseuralert.dao.SmsDao;
import com.reynaud.poseuralert.dao.UserDao;
import com.reynaud.poseuralert.model.SMSCategory;
import com.reynaud.poseuralert.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/facturation")
public class BillingController {

    @Autowired
    private SmsDao smsDao;

    @Autowired
    private CommercialContactDao commercialContactDao;

    @Autowired
    private UserDao userDao;

    // Prix par SMS en euros (à adapter selon votre tarification)
    private static final double PRICE_PER_SMS = 0.10;

    @GetMapping
    public String showBillingPage(@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
                                  Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Récupérer l'utilisateur courant par email
        String email = userDetails.getUsername();
        UserEntity user = userDao.findByEmail(email);

        if (user == null) {
            model.addAttribute("error", "Utilisateur non trouvé");
            return "billing";
        }

        // Récupérer les statistiques SMS
        long totalSms = smsDao.countByProfessional(user);
        long reminderSms = smsDao.countByProfessionalAndCategory(user, SMSCategory.REMINDER);
        long promotionalSms = smsDao.countByProfessionalAndCategory(user, SMSCategory.PROMOTIONAL);
        long activeContacts = commercialContactDao.countActiveByProfessional(user);
        double amountDue = totalSms * PRICE_PER_SMS;

        model.addAttribute("user", user);
        model.addAttribute("totalSms", totalSms);
        model.addAttribute("reminderSms", reminderSms);
        model.addAttribute("promotionalSms", promotionalSms);
        model.addAttribute("activeContacts", activeContacts);
        model.addAttribute("amountDue", String.format("%.2f", amountDue));
        model.addAttribute("pricePerSms", String.format("%.2f", PRICE_PER_SMS));

        return "billing";
    }
}
