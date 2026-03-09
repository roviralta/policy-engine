package com.rovi.policy_engine.repository;

import org.springframework.stereotype.Repository;

import com.rovi.policy_engine.model.Feature;
import com.rovi.policy_engine.model.FeatureName;

@Repository
public interface FeatureRepository {
    Feature findByName(FeatureName featureName);
}