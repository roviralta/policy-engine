package com.rovi.policy_engine.policy;

import com.rovi.policy_engine.model.Feature;
import com.rovi.policy_engine.model.PolicyDecision;
import com.rovi.policy_engine.model.User;
import com.rovi.policy_engine.repository.FeatureRepository;

import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
public class RegionPolicy implements AccessPolicy {

    private final FeatureRepository featureRepo;

    public RegionPolicy(FeatureRepository featureRepo) {
        this.featureRepo = featureRepo;
    }

    @Override
    public PolicyDecision evaluate(User user, Feature feature) {

        Feature dbFeature = featureRepo.findByName(feature.getName());

        boolean allowed = dbFeature.getAllowedRegions()
                                   .contains(user.getRegion());

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