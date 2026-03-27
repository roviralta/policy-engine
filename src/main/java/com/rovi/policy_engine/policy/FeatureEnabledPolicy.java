package com.rovi.policy_engine.policy;

import com.rovi.policy_engine.model.Feature;
import com.rovi.policy_engine.model.PolicyDecision;
import com.rovi.policy_engine.model.User;

import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class FeatureEnabledPolicy implements AccessPolicy {

    @Override
    public PolicyDecision evaluate(User user, Feature feature) {
        if (!feature.isEnabled()) {
            return PolicyDecision.builder()
                    .allowed(false)
                    .explanation(List.of("Feature is currently disabled"))
                    .nextSteps(List.of("Contact support or wait for feature release"))
                    .build();
        }

        return PolicyDecision.builder()
                .allowed(true)
                .explanation(List.of("Feature is enabled"))
                .nextSteps(List.of("You can use the feature now"))
                .build();
    }
}