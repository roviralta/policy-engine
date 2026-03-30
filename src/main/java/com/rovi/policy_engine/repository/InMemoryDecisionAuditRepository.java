package com.rovi.policy_engine.repository;

import com.rovi.policy_engine.model.DecisionAudit;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Repository
public class InMemoryDecisionAuditRepository {

    private static final int MAX_AUDITS = 10000;
    private final ConcurrentLinkedQueue<DecisionAudit> audits = new ConcurrentLinkedQueue<>();

    public DecisionAudit save(DecisionAudit audit) {
        if (audit.getId() == null) {
            audit.setId("audit-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000));
        }
        if (audit.getTimestamp() == null) {
            audit.setTimestamp(Instant.now());
        }
        audits.add(audit);
        
        while (audits.size() > MAX_AUDITS) {
            audits.poll();
        }
        
        return audit;
    }

    public List<DecisionAudit> findAll(int page, int size) {
        return audits.stream()
                .sorted(Comparator.comparing(DecisionAudit::getTimestamp).reversed())
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    public List<DecisionAudit> findByUserId(Long userId, int page, int size) {
        return audits.stream()
                .filter(a -> a.getUserId() != null && a.getUserId().equals(userId))
                .sorted(Comparator.comparing(DecisionAudit::getTimestamp).reversed())
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    public List<DecisionAudit> findByFeatureName(String featureName, int page, int size) {
        return audits.stream()
                .filter(a -> a.getFeatureName() != null && a.getFeatureName().equals(featureName))
                .sorted(Comparator.comparing(DecisionAudit::getTimestamp).reversed())
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    public List<DecisionAudit> findByDateRange(Instant from, Instant to, int page, int size) {
        return audits.stream()
                .filter(a -> a.getTimestamp().isAfter(from) && a.getTimestamp().isBefore(to))
                .sorted(Comparator.comparing(DecisionAudit::getTimestamp).reversed())
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    public long count() {
        return audits.size();
    }

    public long countByDecision(boolean decision) {
        return audits.stream()
                .filter(a -> a.isDecision() == decision)
                .count();
    }
}
