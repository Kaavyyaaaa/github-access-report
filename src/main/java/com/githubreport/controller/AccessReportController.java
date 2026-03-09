package com.githubreport.controller;

import com.githubreport.model.AccessReport;
import com.githubreport.service.AccessReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AccessReportController {

    private static final Logger log = LoggerFactory.getLogger(AccessReportController.class);

    private final AccessReportService accessReportService;

    public AccessReportController(AccessReportService accessReportService) {
        this.accessReportService = accessReportService;
    }

    @GetMapping("/access-report")
    public ResponseEntity<AccessReport> getAccessReport(@RequestParam String org) {

        if (org == null || org.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        log.info("Received request for access report: org={}", org);

        try {
            AccessReport report = accessReportService.generateReport(org);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            log.error("Failed to generate access report for org: {}", org, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/access-report/user")
    public ResponseEntity<?> getUserAccess(
            @RequestParam String org,
            @RequestParam String username) {

        if (org == null || org.isBlank() || username == null || username.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Both 'org' and 'username' parameters are required"));
        }

        log.info("Received request for user access: org={}, user={}", org, username);

        try {
            AccessReport report = accessReportService.generateReport(org);

            var userAccess = report.getUserAccess().get(username);

            if (userAccess == null) {
                return ResponseEntity.ok(Map.of(
                        "organization", org,
                        "username", username,
                        "message", "User not found or has no repository access",
                        "repositories", java.util.List.of()
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "organization", org,
                    "username", username,
                    "totalRepos", userAccess.size(),
                    "repositories", userAccess
            ));

        } catch (Exception e) {
            log.error("Failed to get access for user {} in org {}", username, org, e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to generate report: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}