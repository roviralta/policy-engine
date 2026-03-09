package com.rovi.policy_engine.repository;

import com.rovi.policy_engine.model.Feature;
import com.rovi.policy_engine.model.FeatureName;
import com.rovi.policy_engine.model.PlanType;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryFeatureRepository implements FeatureRepository {

    private final Map<FeatureName, Feature> features = new HashMap<>();

    public InMemoryFeatureRepository() {
        // Preload features
        features.put(
            FeatureName.BASIC_DASHBOARD,
            new Feature(FeatureName.BASIC_DASHBOARD, PlanType.FREE, true)
        );
        features.put(
            FeatureName.ADVANCED_EXPORT,
            new Feature(FeatureName.ADVANCED_EXPORT, PlanType.PRO, true)
        );
        features.put(
            FeatureName.PRIORITY_SUPPORT,
            new Feature(FeatureName.PRIORITY_SUPPORT, PlanType.PRO, false)
        );
    }

    @Override
    public Feature findByName(FeatureName featureName) {
        Feature f = features.get(featureName);
        if (f == null) {
            throw new IllegalArgumentException("Feature not found: " + featureName);
        }
        return f;
    }
}