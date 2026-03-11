package com.rovi.policy_engine.controller;

import com.rovi.policy_engine.dto.request.PolicyRequest;
import com.rovi.policy_engine.dto.response.PolicyResponse;
import com.rovi.policy_engine.model.Feature;
import com.rovi.policy_engine.model.PolicyDecision;
import com.rovi.policy_engine.model.User;
import com.rovi.policy_engine.service.PolicyEngineService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/policy")
public class PolicyController {

    private final PolicyEngineService engineService;

    public PolicyController(PolicyEngineService engineService) {
        this.engineService = engineService;
    }

    @PostMapping("/check")
    public ResponseEntity<PolicyResponse> checkPolicy(@Valid @RequestBody PolicyRequest request) {
        // Convert request DTO → domain objects
        User user = new User(request.getUserId(), request.getPlan(), request.getRegion());
        Feature feature = new Feature(request.getFeatureName(), request.getPlan(), true, List.of(request.getRegion())); 

        PolicyDecision decision = engineService.evaluate(user, feature);

        // Convert domain PolicyDecision → response DTO
        PolicyResponse response = PolicyResponse.builder()
                .allowed(decision.isAllowed())
                .explanation(decision.getExplanation())
                .nextSteps(decision.getNextSteps())
                .build();

        return ResponseEntity.ok(response);
    }
}