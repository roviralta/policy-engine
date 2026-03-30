package com.rovi.policy_engine.repository;

import com.rovi.policy_engine.exception.ResourceNotFoundException;
import com.rovi.policy_engine.model.Feature;
import com.rovi.policy_engine.model.FeatureName;
import com.rovi.policy_engine.model.PlanType;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryFeatureRepository implements FeatureRepository {

    private final Map<FeatureName, Feature> features = new HashMap<>();

    public InMemoryFeatureRepository() {
        features.put(
    FeatureName.BASIC_DASHBOARD,
    new Feature(
        FeatureName.BASIC_DASHBOARD,
        PlanType.FREE,
        true,
        List.of("EU", "US", "ASIA")
    )
);

features.put(
    FeatureName.ADVANCED_EXPORT,
    new Feature(
        FeatureName.ADVANCED_EXPORT,
        PlanType.PRO,
        true,
        List.of("EU", "US")
    )
);

features.put(
    FeatureName.PRIORITY_SUPPORT,
    new Feature(
        FeatureName.PRIORITY_SUPPORT,
        PlanType.PRO,
        false,
        List.of("US")
    )
);
    }

    @Override
    public Feature findByName(FeatureName featureName) {
        Feature f = features.get(featureName);
        if (f == null) {
            throw new ResourceNotFoundException("Feature not found: " + featureName);
        }
        return f;
    }
}