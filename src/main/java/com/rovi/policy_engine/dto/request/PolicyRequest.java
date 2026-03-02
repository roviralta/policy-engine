package com.rovi.policy_engine.dto.request;

import com.rovi.policy_engine.model.FeatureName;
import com.rovi.policy_engine.model.PlanType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PolicyRequest {
    private Long userId;
    private PlanType plan;
    private FeatureName featureName;
    private String region; // optional
}