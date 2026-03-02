package com.rovi.policy_engine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Feature {
    private final FeatureName name;
    private final PlanType requiredPlan;
    private final boolean enabled; 
}