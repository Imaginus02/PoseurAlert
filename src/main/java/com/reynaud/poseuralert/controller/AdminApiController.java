package com.reynaud.poseuralert.controller;

import com.reynaud.poseuralert.dao.AppointmentDao;
import com.reynaud.poseuralert.dao.ClientPhoneDao;
import com.reynaud.poseuralert.dao.ReportDao;
import com.reynaud.poseuralert.dao.UserDao;
import com.reynaud.poseuralert.model.AppointmentStatus;
import com.reynaud.poseuralert.model.ClientPhoneEntity;
import com.reynaud.poseuralert.security.SpringSecurityConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final UserDao userDao;
    private final AppointmentDao appointmentDao;
    private final ReportDao reportDao;
    private final ClientPhoneDao clientPhoneDao;

    public AdminApiController(UserDao userDao,
                              AppointmentDao appointmentDao,
                              ReportDao reportDao,
                              ClientPhoneDao clientPhoneDao) {
        this.userDao = userDao;
        this.appointmentDao = appointmentDao;
        this.reportDao = reportDao;
        this.clientPhoneDao = clientPhoneDao;
    }

    public static class MetricsDTO {
        private final long totalUsers;
        private final long totalAdmins;
        private final long totalAppointments;
        private final long totalReports;
        private final long flaggedNumbers;
        private final long upcomingAppointments;
        private final long noShowAppointments;

        public MetricsDTO(long totalUsers,
                          long totalAdmins,
                          long totalAppointments,
                          long totalReports,
                          long flaggedNumbers,
                          long upcomingAppointments,
                          long noShowAppointments) {
            this.totalUsers = totalUsers;
            this.totalAdmins = totalAdmins;
            this.totalAppointments = totalAppointments;
            this.totalReports = totalReports;
            this.flaggedNumbers = flaggedNumbers;
            this.upcomingAppointments = upcomingAppointments;
            this.noShowAppointments = noShowAppointments;
        }

        public long getTotalUsers() {
            return totalUsers;
        }

        public long getTotalAdmins() {
            return totalAdmins;
        }

        public long getTotalAppointments() {
            return totalAppointments;
        }

        public long getTotalReports() {
            return totalReports;
        }

        public long getFlaggedNumbers() {
            return flaggedNumbers;
        }

        public long getUpcomingAppointments() {
            return upcomingAppointments;
        }

        public long getNoShowAppointments() {
            return noShowAppointments;
        }
    }

    public static class FlaggedNumberDTO {
        private final String phoneNumber;
        private final Integer reportCount;
        private final String lastReportDate;

        public FlaggedNumberDTO(ClientPhoneEntity entity) {
            this.phoneNumber = entity.getPhoneNumber();
            this.reportCount = entity.getReportCount();
            this.lastReportDate = entity.getLastReportDate() != null
                    ? entity.getLastReportDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    : null;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public Integer getReportCount() {
            return reportCount;
        }

        public String getLastReportDate() {
            return lastReportDate;
        }
    }

    @GetMapping("/metrics")
    public ResponseEntity<MetricsDTO> getMetrics() {
        long totalUsers = userDao.count();
        long totalAdmins = userDao.countByRole(SpringSecurityConfig.ROLE_ADMIN);
        long totalAppointments = appointmentDao.count();
        long totalReports = reportDao.count();
        long flaggedNumbers = clientPhoneDao.countByIsFlaggedTrue();
        long upcomingAppointments = appointmentDao.countByAppointmentDateAfter(LocalDateTime.now());
        long noShowAppointments = appointmentDao.countByStatus(AppointmentStatus.NO_SHOW);

        MetricsDTO metrics = new MetricsDTO(
                totalUsers,
                totalAdmins,
                totalAppointments,
                totalReports,
                flaggedNumbers,
                upcomingAppointments,
                noShowAppointments
        );

        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/flagged-numbers")
    public ResponseEntity<List<FlaggedNumberDTO>> getFlaggedNumbers() {
        List<FlaggedNumberDTO> flaggedNumbers = clientPhoneDao.findFlaggedNumbersOrdered().stream()
                .map(FlaggedNumberDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(flaggedNumbers);
    }
}
