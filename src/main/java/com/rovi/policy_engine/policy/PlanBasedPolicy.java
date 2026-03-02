package com.rovi.policy_engine.policy;

import com.rovi.policy_engine.model.Feature;
import com.rovi.policy_engine.model.PolicyDecision;
import com.rovi.policy_engine.model.User;

import java.util.List;

public class PlanBasedPolicy implements AccessPolicy {

    @Override
    public PolicyDecision evaluate(User user, Feature feature) {
        boolean allowed = user.getPlan().canAccess(feature.getRequiredPlan());

        return PolicyDecision.builder()
                .allowed(allowed)
                .explanation(List.of(
                        allowed
                                ? "Access granted based on plan"
                                : "Access denied: Upgrade your plan to access this feature"
                ))
                .nextSteps(allowed
                        ? List.of("You can use the feature now")
                        : List.of("Upgrade your subscription")
                )
                .build();
    }
}