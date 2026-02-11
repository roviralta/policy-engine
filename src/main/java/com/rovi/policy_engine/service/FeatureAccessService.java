package com.rovi.policy_engine.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.rovi.policy_engine.dto.request.FeatureAccessRequest;
import com.rovi.policy_engine.dto.response.FeatureAccessResponse;

@Service
public class FeatureAccessService {

	private static final Set<String> VALID_PLANS = Set.of("FREE", "PAID");
	private static final Set<String> VALID_FEATURES = Set.of("BASIC_DASHBOARD", "ADVANCED_EXPORT", "PRIORITY_SUPPORT");

    public FeatureAccessResponse checkFeatureAccess(FeatureAccessRequest request) {

        String plan = validatePlan(request.getUserPlan());
        String featureName = validateFeature(request.getFeatureName());

        return checkAccessByFeature(featureName, plan);
    }

    // -------- Validation --------

    private String validatePlan(String plan) {
        if (plan == null || !VALID_PLANS.contains(plan.toUpperCase())) {
            throw new IllegalArgumentException("Invalid plan: " + plan);
        }
        return plan.toUpperCase();
    }

    private String validateFeature(String featureName) {
        if (featureName == null || !VALID_FEATURES.contains(featureName.toUpperCase())) {
            throw new IllegalArgumentException("Invalid feature: " + featureName);
        }
        return featureName.toUpperCase();
    }

    // -------- Feature logic --------

    private FeatureAccessResponse checkAccessByFeature(String featureName, String plan) {

        switch (featureName) {

            case "BASIC_DASHBOARD":
                return allowForAllPlans(plan, "BASIC_DASHBOARD");

            case "ADVANCED_EXPORT":
                return paidOnly(plan, "ADVANCED_EXPORT");

            case "PRIORITY_SUPPORT":
                return paidOnly(plan, "PRIORITY_SUPPORT");

            default:
                throw new IllegalArgumentException("Unsupported feature: " + featureName);
        }
    }

    // -------- Reusable rules (NO duplication) --------

    private FeatureAccessResponse allowForAllPlans(String plan, String feature) {
        return new FeatureAccessResponse(
                true,
                List.of("Feature " + feature + " available for plan " + plan)
        );
    }

    private FeatureAccessResponse paidOnly(String plan, String feature) {
        if ("FREE".equals(plan)) {
            return new FeatureAccessResponse(
                    false,
                    List.of(
                            "Feature " + feature + " requires PAID plan",
                            "Current plan is FREE"
                    )
            );
        }

        return new FeatureAccessResponse(
                true,
                List.of("Feature " + feature + " available for PAID plan")
        );
    }
}
