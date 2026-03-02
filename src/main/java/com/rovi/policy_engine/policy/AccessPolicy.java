package com.rovi.policy_engine.policy;

import com.rovi.policy_engine.model.Feature;
import com.rovi.policy_engine.model.User;
import com.rovi.policy_engine.model.PolicyDecision;

public interface AccessPolicy {
    /**
     * Evaluates if a user can access a feature.
     *
     * @param user    the user trying to access
     * @param feature the feature to access
     * @return PolicyDecision containing allowed flag, explanation, next steps
     */
    PolicyDecision evaluate(User user, Feature feature);
}