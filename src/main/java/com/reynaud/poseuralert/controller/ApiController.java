package com.reynaud.poseuralert.controller;

import com.reynaud.poseuralert.dao.AppointmentDao;
import com.reynaud.poseuralert.dao.ReportDao;
import com.reynaud.poseuralert.dao.UserDao;
import com.reynaud.poseuralert.model.*;
import com.reynaud.poseuralert.util.logging.Loggers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
public class ApiController {

    private final AppointmentDao appointmentDao;
    private final UserDao userDao;
    private final ReportDao reportDao;

    public ApiController(AppointmentDao appointmentDao, UserDao userDao, ReportDao reportDao) {
        this.appointmentDao = appointmentDao;
        this.userDao = userDao;
        this.reportDao = reportDao;
    }

    // ===== DTOs pour les réponses API =====

    public static class AppointmentDTO {
        private Long id;
        private String clientName;
        private String clientPhone;
        private String appointmentDate;
        private String status;
        private String displayName;
        private String notes;

        public AppointmentDTO(AppointmentEntity appointment) {
            this.id = appointment.getId();
            this.clientName = appointment.getClientName();
            this.clientPhone = appointment.getClientPhone();
            this.appointmentDate = appointment.getAppointmentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            this.status = appointment.getStatus().name();
            this.displayName = appointment.getStatus().getDisplayName();
            this.notes = appointment.getNotes();
        }

        // Getters
        public Long getId() { return id; }
        public String getClientName() { return clientName; }
        public String getClientPhone() { return clientPhone; }
        public String getAppointmentDate() { return appointmentDate; }
        public String getStatus() { return status; }
        public String getDisplayName() { return displayName; }
        public String getNotes() { return notes; }
    }

    public static class ReportDTO {
        private Long id;
        private String reportedPhone;
        private String reason;
        private String displayName;
        private String additionalNotes;
        private String appointmentDate;
        private String clientName;

        public ReportDTO(ReportEntity report) {
            this.id = report.getId();
            this.reportedPhone = report.getReportedPhone();
            this.reason = report.getReason().name();
            this.displayName = report.getReason().getDisplayName();
            this.additionalNotes = report.getAdditionalNotes();

            // Informations sur le rendez-vous associé
            if (report.getAppointment() != null) {
                this.appointmentDate = report.getAppointment().getAppointmentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                this.clientName = report.getAppointment().getClientName();
            }
        }

        // Getters
        public Long getId() { return id; }
        public String getReportedPhone() { return reportedPhone; }
        public String getReason() { return reason; }
        public String getDisplayName() { return displayName; }
        public String getAdditionalNotes() { return additionalNotes; }
        public String getAppointmentDate() { return appointmentDate; }
        public String getClientName() { return clientName; }
    }

    // ===== API ENDPOINTS =====

    /**
     * API: Récupère tous les rendez-vous de l'utilisateur connecté
     * GET /api/appointments
     */
    @GetMapping("/appointments")
    @ResponseBody
    public ResponseEntity<List<AppointmentDTO>> getUserAppointments(@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        try {
            // Récupérer l'UserEntity depuis la base de données en utilisant l'email
            UserEntity user = userDao.findByEmail(userDetails.getUsername());

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            List<AppointmentEntity> appointments = appointmentDao.findByProfessional(user);
            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(appointmentDTOs);
        } catch (Exception e) {
            Loggers.technical().error("ERROR in getUserAppointments cause={}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * API: Récupère un rendez-vous spécifique par ID (seulement si c'est le sien)
     * GET /api/appointments/{id}
     */
    @GetMapping("/appointments/{id}")
    @ResponseBody
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable Long id,
                                                           @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        try {
            // Récupérer l'UserEntity depuis la base de données en utilisant l'email
            UserEntity user = userDao.findByEmail(userDetails.getUsername());

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Optional<AppointmentEntity> appointmentOpt = appointmentDao.findById(id);
            if (appointmentOpt.isPresent()) {
                AppointmentEntity appointment = appointmentOpt.get();

                // Vérifier que l'utilisateur est bien le propriétaire du rendez-vous
                if (!appointment.getProfessional().getId().equals(user.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }

                AppointmentDTO appointmentDTO = new AppointmentDTO(appointment);
                return ResponseEntity.ok(appointmentDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Loggers.technical().error("ERROR in getAppointmentById id={} cause={}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * API: Récupère tous les signalements de l'utilisateur connecté
     * GET /api/reports
     */
    @GetMapping("/reports")
    @ResponseBody
    public ResponseEntity<List<ReportDTO>> getUserReports(@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        try {
            // Récupérer l'UserEntity depuis la base de données en utilisant l'email
            UserEntity user = userDao.findByEmail(userDetails.getUsername());

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Récupérer tous les signalements où l'utilisateur est le rapporteur
            List<ReportEntity> reports = reportDao.findByProfessional(user);
            List<ReportDTO> reportDTOs = reports.stream()
                    .map(ReportDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(reportDTOs);
        } catch (Exception e) {
            Loggers.technical().error("ERROR in getUserReports cause={}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}