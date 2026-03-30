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
public class SimulateResponse {
    private boolean allowed;
    private String reason;
    private List<RuleEvaluation> ruleEvaluations;
    private List<String> nextSteps;
    private Instant evaluatedAt;
    private long latencyMs;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RuleEvaluation {
        private String ruleId;
        private String ruleName;
        private boolean matched;
        private String reason;
    }
}
