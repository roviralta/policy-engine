package com.rovi.policy_engine.policy;

import com.rovi.policy_engine.model.Feature;
import com.rovi.policy_engine.model.PolicyDecision;
import com.rovi.policy_engine.model.User;

import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class RegionPolicy implements AccessPolicy {

    @Override
    public PolicyDecision evaluate(User user, Feature feature) {
        boolean allowed = feature.getAllowedRegions().contains(user.getRegion());

        return PolicyDecision.builder()
                .allowed(allowed)
                .explanation(List.of(
                        this.getClass().getSimpleName() + ": " +
                        (allowed
                                ? "Region allowed"
                                : "Feature not available in your region")
                ))
                .nextSteps(
                        allowed
                                ? List.of("You can use the feature now")
                                : List.of("Change region or contact support")
                )
                .build();
    }
}