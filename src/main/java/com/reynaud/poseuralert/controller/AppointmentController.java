package com.reynaud.poseuralert.controller;

import com.reynaud.poseuralert.dao.AppointmentDao;
import com.reynaud.poseuralert.dao.AuditLogDao;
import com.reynaud.poseuralert.dao.ClientPhoneDao;
import com.reynaud.poseuralert.dao.ReportDao;
import com.reynaud.poseuralert.dao.UserDao;
import com.reynaud.poseuralert.model.*;
import com.reynaud.poseuralert.util.SectorLabels;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/rendez-vous")
public class AppointmentController {

    private final AppointmentDao appointmentDao;
    private final UserDao userDao;
    private final ClientPhoneDao clientPhoneDao;
    private final ReportDao reportDao;
    private final AuditLogDao auditLogDao;

    public AppointmentController(AppointmentDao appointmentDao,
                               UserDao userDao,
                               ClientPhoneDao clientPhoneDao,
                               ReportDao reportDao,
                               AuditLogDao auditLogDao) {
        this.appointmentDao = appointmentDao;
        this.userDao = userDao;
        this.clientPhoneDao = clientPhoneDao;
        this.reportDao = reportDao;
        this.auditLogDao = auditLogDao;
    }

    private void logAuditAction(UserEntity user, String action, String resourceType, String resourceId, String details) {
        if (SectorLabels.requiresHDSCompliance(user.getSector())) {
            AuditLogEntity auditLog = new AuditLogEntity(user, action, resourceType, resourceId);
            auditLog.setDetails(details);
            auditLogDao.save(auditLog);
        }
    }

    @GetMapping
    public String listAppointments(@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails, Model model) {
        try {
            System.out.println("=== LISTING APPOINTMENTS FOR USER: " + userDetails.getUsername() + " ===");

            // Récupérer l'UserEntity depuis la base de données en utilisant l'email
            UserEntity user = userDao.findByEmail(userDetails.getUsername());

            if (user == null) {
                System.err.println("ERROR: User not found in database for email: " + userDetails.getUsername());
                return "redirect:/login";
            }

            List<AppointmentEntity> appointments = appointmentDao.findByProfessional(user);
            System.out.println("Found " + appointments.size() + " appointments");

            model.addAttribute("appointments", appointments);
            model.addAttribute("user", user);
            model.addAttribute("sectorLabels", new SectorLabels());

            // Audit pour les professionnels de santé
            logAuditAction(user, "VIEW", "APPOINTMENTS", "LIST", "Consultation de la liste des rendez-vous");

            System.out.println("Returning appointments template");
            return "appointments";
        } catch (Exception e) {
            System.err.println("ERROR in listAppointments: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/nouveau")
    public String newAppointmentForm(@AuthenticationPrincipal UserEntity user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("sectorLabels", new SectorLabels());
        return "new-appointment";
    }

    @PostMapping("/nouveau")
    public String createAppointment(@AuthenticationPrincipal UserEntity user,
                                  @RequestParam String clientName,
                                  @RequestParam String clientPhone,
                                  @RequestParam String appointmentDateTime,
                                  @RequestParam(required = false) String notes,
                                  RedirectAttributes redirectAttributes) {

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime appointmentDate = LocalDateTime.parse(appointmentDateTime, formatter);

            // Vérifier si le numéro de téléphone est déjà signalé
            Optional<ClientPhoneEntity> existingPhone = clientPhoneDao.findByPhoneNumber(clientPhone);
            boolean isFlagged = existingPhone.isPresent() && existingPhone.get().getIsFlagged();

            if (isFlagged) {
                redirectAttributes.addFlashAttribute("warning",
                    "Attention : Ce numéro de téléphone est associé à un client ayant déjà été signalé plusieurs fois pour des rendez-vous manqués.");
            }

            AppointmentEntity appointment = new AppointmentEntity(user, clientName, clientPhone, appointmentDate);
            appointment.setNotes(notes);
            AppointmentEntity savedAppointment = appointmentDao.save(appointment);

            // Audit pour les professionnels de santé
            logAuditAction(user, "CREATE", "APPOINTMENT", savedAppointment.getId().toString(),
                          "Création de rendez-vous pour " + clientName + " (" + clientPhone + ")");

            redirectAttributes.addFlashAttribute("success", "Rendez-vous créé avec succès.");
            return "redirect:/rendez-vous";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création du rendez-vous.");
            return "redirect:/rendez-vous/nouveau";
        }
    }

    @PostMapping("/{id}/statut")
    public String updateAppointmentStatus(@PathVariable Long id,
                                        @RequestParam AppointmentStatus status,
                                        @AuthenticationPrincipal UserEntity user,
                                        RedirectAttributes redirectAttributes) {

        Optional<AppointmentEntity> appointmentOpt = appointmentDao.findById(id);
        if (appointmentOpt.isPresent()) {
            AppointmentEntity appointment = appointmentOpt.get();

            // Vérifier que l'utilisateur est bien le propriétaire du rendez-vous
            if (!appointment.getProfessional().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "Accès non autorisé.");
                return "redirect:/rendez-vous";
            }

            appointment.setStatus(status);
            appointmentDao.save(appointment);

            redirectAttributes.addFlashAttribute("success", "Statut du rendez-vous mis à jour.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Rendez-vous introuvable.");
        }

        return "redirect:/rendez-vous";
    }

    @PostMapping("/{id}/signaler")
    public String reportAppointment(@PathVariable Long id,
                                  @RequestParam ReportReason reason,
                                  @RequestParam(required = false) String additionalNotes,
                                  @AuthenticationPrincipal UserEntity user,
                                  RedirectAttributes redirectAttributes) {

        Optional<AppointmentEntity> appointmentOpt = appointmentDao.findById(id);
        if (appointmentOpt.isPresent()) {
            AppointmentEntity appointment = appointmentOpt.get();

            // Vérifier que l'utilisateur est bien le propriétaire du rendez-vous
            if (!appointment.getProfessional().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "Accès non autorisé.");
                return "redirect:/rendez-vous";
            }

            // Créer le signalement
            ReportEntity report = new ReportEntity(appointment.getClientPhone(), user, appointment, reason);
            report.setAdditionalNotes(additionalNotes);
            ReportEntity savedReport = reportDao.save(report);

            // Mettre à jour ou créer l'entrée dans ClientPhoneEntity
            ClientPhoneEntity clientPhone = clientPhoneDao.findByPhoneNumber(appointment.getClientPhone())
                    .orElse(new ClientPhoneEntity(appointment.getClientPhone()));
            clientPhone.incrementReportCount();
            clientPhoneDao.save(clientPhone);

            // Audit pour les professionnels de santé
            logAuditAction(user, "REPORT", "APPOINTMENT", appointment.getId().toString(),
                          "Signalement de rendez-vous - Motif: " + reason.getDisplayName());

            // Marquer le rendez-vous comme "no-show" si c'était un signalement d'absence
            if (reason == ReportReason.NO_SHOW) {
                appointment.setStatus(AppointmentStatus.NO_SHOW);
                appointmentDao.save(appointment);
            }

            redirectAttributes.addFlashAttribute("success", "Signalement enregistré. Le numéro est maintenant surveillé.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Rendez-vous introuvable.");
        }

        return "redirect:/rendez-vous";
    }
}