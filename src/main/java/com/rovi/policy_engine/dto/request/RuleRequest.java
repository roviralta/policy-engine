package com.rovi.policy_engine.dto.request;

import com.rovi.policy_engine.model.Condition;
import com.rovi.policy_engine.model.Rule;
import com.rovi.policy_engine.model.RuleAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class RuleRequest {
    
    @NotBlank(message = "Rule name is required")
    private String name;
    
    private String description;
    
    @NotNull(message = "Conditions are required")
    private List<Condition> conditions;
    
    private Rule.LogicalOperator logicalOperator;
    
    @NotNull(message = "Action is required")
    private RuleAction action;
    
    private int priority;
    
    private Instant validFrom;
    
    private Instant validTo;
    
    private boolean enabled;
}
