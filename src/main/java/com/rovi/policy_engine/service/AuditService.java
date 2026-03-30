package com.rovi.policy_engine.service;

import com.rovi.policy_engine.dto.response.AuditResponse;
import com.rovi.policy_engine.model.DecisionAudit;
import com.rovi.policy_engine.repository.InMemoryDecisionAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final InMemoryDecisionAuditRepository auditRepository;

    public DecisionAudit logDecision(DecisionAudit audit) {
        return auditRepository.save(audit);
    }

    public List<AuditResponse> getAllAudits(int page, int size) {
        return auditRepository.findAll(page, size).stream()
                .map(AuditResponse::from)
                .collect(Collectors.toList());
    }

    public List<AuditResponse> getAuditsByUserId(Long userId, int page, int size) {
        return auditRepository.findByUserId(userId, page, size).stream()
                .map(AuditResponse::from)
                .collect(Collectors.toList());
    }

    public List<AuditResponse> getAuditsByFeature(String featureName, int page, int size) {
        return auditRepository.findByFeatureName(featureName, page, size).stream()
                .map(AuditResponse::from)
                .collect(Collectors.toList());
    }

    public List<AuditResponse> getAuditsByDateRange(Instant from, Instant to, int page, int size) {
        return auditRepository.findByDateRange(from, to, page, size).stream()
                .map(AuditResponse::from)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getStatistics() {
        return Map.of(
                "totalDecisions", auditRepository.count(),
                "allowedDecisions", auditRepository.countByDecision(true),
                "deniedDecisions", auditRepository.countByDecision(false)
        );
    }
}
