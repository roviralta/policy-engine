package com.rovi.policy_engine.dto.response;

import com.rovi.policy_engine.model.DecisionAudit;
import com.rovi.policy_engine.model.Rule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchPolicyResponse {
    private List<FeatureDecision> results;
    private Instant evaluatedAt;
    private long totalLatencyMs;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FeatureDecision {
        private String featureName;
        private boolean allowed;
        private String reason;
        private List<String> matchedRules;
        private List<String> nextSteps;
    }
}
