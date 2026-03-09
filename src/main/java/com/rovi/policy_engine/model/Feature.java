package com.rovi.policy_engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Feature {
    private final FeatureName name;
    private final PlanType requiredPlan;
    private final boolean enabled; 
}