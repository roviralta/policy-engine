package com.rovi.policy_engine.dto.response;

import com.rovi.policy_engine.model.Rule;
import com.rovi.policy_engine.model.RuleAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleResponse {
    private String id;
    private String name;
    private String description;
    private RuleAction action;
    private int priority;
    private boolean enabled;
    private Instant createdAt;
    private Instant updatedAt;

    public static RuleResponse from(Rule rule) {
        return RuleResponse.builder()
                .id(rule.getId())
                .name(rule.getName())
                .description(rule.getDescription())
                .action(rule.getAction())
                .priority(rule.getPriority())
                .enabled(rule.isEnabled())
                .build();
    }
}
