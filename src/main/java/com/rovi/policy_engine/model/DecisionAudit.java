package com.rovi.policy_engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionAudit {
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
}
