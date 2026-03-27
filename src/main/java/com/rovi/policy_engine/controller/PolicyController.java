package com.rovi.policy_engine.controller;

import com.rovi.policy_engine.dto.request.PolicyRequest;
import com.rovi.policy_engine.dto.response.PolicyResponse;
import com.rovi.policy_engine.model.Feature;
import com.rovi.policy_engine.model.PolicyDecision;
import com.rovi.policy_engine.model.User;
import com.rovi.policy_engine.repository.FeatureRepository;
import com.rovi.policy_engine.service.PolicyEngineService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@Slf4j
@RestController
@RequestMapping("/api/policy")
@Tag(name = "Policy Engine", description = "Feature access policy evaluation API")
public class PolicyController {

    private final PolicyEngineService engineService;
    private final FeatureRepository featureRepository;

    public PolicyController(PolicyEngineService engineService, FeatureRepository featureRepository) {
        this.engineService = engineService;
        this.featureRepository = featureRepository;
    }

    @PostMapping("/check")
    @Operation(summary = "Check feature access policy", description = "Evaluates whether a user can access a specific feature based on plan and region")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Policy evaluation completed"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PolicyResponse> checkPolicy(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Policy check request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PolicyRequest.class)))
            @Valid @RequestBody PolicyRequest request) {
        User user = new User(request.getUserId(), request.getPlan(), request.getRegion());
        Feature feature = featureRepository.findByName(request.getFeatureName());

        log.debug("Evaluating policy for user={}, feature={}, region={}",
                request.getUserId(), request.getFeatureName(), request.getRegion());

        PolicyDecision decision = engineService.evaluate(user, feature);

        log.info("Policy decision for user {}: allowed={}", request.getUserId(), decision.isAllowed());

        return ResponseEntity.ok(PolicyResponse.builder()
                .allowed(decision.isAllowed())
                .explanation(decision.getExplanation() != null ? decision.getExplanation() : Collections.emptyList())
                .nextSteps(decision.getNextSteps() != null ? decision.getNextSteps() : Collections.emptyList())
                .build());
    }
}