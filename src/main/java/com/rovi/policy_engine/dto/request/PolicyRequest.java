package com.rovi.policy_engine.dto.request;

import com.rovi.policy_engine.model.FeatureName;
import com.rovi.policy_engine.model.PlanType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PolicyRequest {
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Plan cannot be null")
    private PlanType plan;

    @NotNull(message = "Feature cannot be null")
    private FeatureName featureName;

    @NotBlank(message = "Region cannot be blank")
    private String region;
}