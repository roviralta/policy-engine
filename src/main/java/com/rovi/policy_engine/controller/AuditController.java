package com.rovi.policy_engine.controller;

import com.rovi.policy_engine.dto.response.AuditResponse;
import com.rovi.policy_engine.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/decisions")
@RequiredArgsConstructor
@Tag(name = "Decision Audit", description = "View and export decision history")
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    @Operation(summary = "List all decision audits", description = "Paginated list of all decisions")
    public ResponseEntity<List<AuditResponse>> getAllAudits(
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditService.getAllAudits(page, size));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get audits by user ID")
    public ResponseEntity<List<AuditResponse>> getAuditsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditService.getAuditsByUserId(userId, page, size));
    }

    @GetMapping("/feature/{featureName}")
    @Operation(summary = "Get audits by feature name")
    public ResponseEntity<List<AuditResponse>> getAuditsByFeature(
            @PathVariable String featureName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditService.getAuditsByFeature(featureName, page, size));
    }

    @GetMapping("/range")
    @Operation(summary = "Get audits by date range")
    public ResponseEntity<List<AuditResponse>> getAuditsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(auditService.getAuditsByDateRange(from, to, page, size));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get decision statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(auditService.getStatistics());
    }
}
