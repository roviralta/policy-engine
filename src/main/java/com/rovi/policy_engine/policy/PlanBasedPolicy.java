package com.rovi.policy_engine.policy;

import com.rovi.policy_engine.model.Feature;
import com.rovi.policy_engine.model.PolicyDecision;
import com.rovi.policy_engine.model.User;
import com.rovi.policy_engine.repository.FeatureRepository;

import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class PlanBasedPolicy implements AccessPolicy {

    private final FeatureRepository featureRepo;

    public PlanBasedPolicy(FeatureRepository featureRepo) {
        this.featureRepo = featureRepo;
    }

    @Override
    public PolicyDecision evaluate(User user, Feature feature) {
        Feature dbFeature = featureRepo.findByName(feature.getName());

        boolean allowed = user.getPlan().canAccess(dbFeature.getRequiredPlan());

        return PolicyDecision.builder()
                .allowed(allowed)
                .explanation(List.of(
                        this.getClass().getSimpleName() + ": " +
                        (allowed
                                ? "Access granted based on plan"
                                : "Access denied: Upgrade your plan to access this feature")
                ))
                .nextSteps(allowed
                        ? List.of("You can use the feature now")
                        : List.of("Upgrade your subscription")
                )
                .build();
    }
}