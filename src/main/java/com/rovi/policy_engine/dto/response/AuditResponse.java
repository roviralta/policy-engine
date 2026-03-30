package com.rovi.policy_engine.dto.response;

import com.rovi.policy_engine.model.DecisionAudit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditResponse {
    private String id;
    private Instant timestamp;
    private Long userId;
    private String userName;
    private String featureName;
    private String ruleId;
    private String ruleName;
    private Map<String, Object> input;
    private boolean decision;
    private String reason;
    private long latencyMs;

    public static AuditResponse from(DecisionAudit audit) {
        return AuditResponse.builder()
                .id(audit.getId())
                .timestamp(audit.getTimestamp())
                .userId(audit.getUserId())
                .userName(audit.getUserName())
                .featureName(audit.getFeatureName())
                .ruleId(audit.getRuleId())
                .ruleName(audit.getRuleName())
                .input(audit.getInput())
                .decision(audit.isDecision())
                .reason(audit.getReason())
                .latencyMs(audit.getLatencyMs())
                .build();
    }
}
