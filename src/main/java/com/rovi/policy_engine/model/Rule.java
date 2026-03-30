package com.rovi.policy_engine.model;

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
public class Rule {
    private String id;
    private String name;
    private String description;
    private List<Condition> conditions;
    private LogicalOperator logicalOperator;
    private RuleAction action;
    private int priority;
    private Instant validFrom;
    private Instant validTo;
    private boolean enabled;

    public enum LogicalOperator {
        AND, OR
    }
}
