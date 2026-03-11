package com.rovi.policy_engine.service;

import com.rovi.policy_engine.model.Feature;
import com.rovi.policy_engine.model.PolicyDecision;
import com.rovi.policy_engine.model.User;
import com.rovi.policy_engine.policy.AccessPolicy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PolicyEngineService {

    private final List<AccessPolicy> policies;

    public PolicyEngineService(List<AccessPolicy> policies) {
        this.policies = policies;
    }

    public PolicyDecision evaluate(User user, Feature feature) {
        List<String> explanations = new ArrayList<>();
        List<String> nextSteps = new ArrayList<>();
        boolean allowed = true;

        for (AccessPolicy policy : policies) {
            PolicyDecision decision = policy.evaluate(user, feature);

            explanations.addAll(decision.getExplanation());
            nextSteps.addAll(decision.getNextSteps());

            if (!decision.isAllowed()) {
                allowed = false;
                explanations.addAll(decision.getExplanation());
                nextSteps.clear(); // reset previous suggestions
                nextSteps.addAll(decision.getNextSteps());
            } else {
                explanations.addAll(decision.getExplanation());
}
        }

        return PolicyDecision.builder()
                .allowed(allowed)
                .explanation(explanations)
                .nextSteps(nextSteps)
                .build();
    }
}