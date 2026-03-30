package com.rovi.policy_engine.controller;

import com.rovi.policy_engine.dto.request.BatchPolicyRequest;
import com.rovi.policy_engine.dto.request.SimulateRequest;
import com.rovi.policy_engine.dto.response.BatchPolicyResponse;
import com.rovi.policy_engine.dto.response.SimulateResponse;
import com.rovi.policy_engine.exception.ResourceNotFoundException;
import com.rovi.policy_engine.model.DecisionAudit;
import com.rovi.policy_engine.model.ExtendedUser;
import com.rovi.policy_engine.model.Feature;
import com.rovi.policy_engine.model.FeatureName;
import com.rovi.policy_engine.repository.FeatureRepository;
import com.rovi.policy_engine.service.AuditService;
import com.rovi.policy_engine.service.RuleEvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/policy")
@RequiredArgsConstructor
@Tag(name = "Policy Evaluation", description = "Evaluate feature access policies")
public class DecisionController {

    private final RuleEvaluationService ruleEvaluationService;
    private final AuditService auditService;
    private final FeatureRepository featureRepository;

    @PostMapping("/simulate")
    @Operation(summary = "Simulate policy decision", description = "What-if analysis with detailed rule evaluation")
    public ResponseEntity<SimulateResponse> simulate(
            @Valid @RequestBody SimulateRequest request,
            Authentication authentication) {
        long startTime = System.currentTimeMillis();

        Feature feature = featureRepository.findByName(request.getFeatureName());

        RuleEvaluationService.EvaluationResult result = ruleEvaluationService.evaluate(
                request.getUser(),
                feature,
                request.getContext()
        );

        long latencyMs = System.currentTimeMillis() - startTime;

        List<SimulateResponse.RuleEvaluation> ruleEvals = result.getRuleEvaluations().stream()
                .map(re -> SimulateResponse.RuleEvaluation.builder()
                        .ruleId(re.getRuleId())
                        .ruleName(re.getRuleName())
                        .matched(re.isMatched())
                        .reason(re.getReason())
                        .build())
                .toList();

        SimulateResponse response = SimulateResponse.builder()
                .allowed(result.isAllowed())
                .reason(result.getReason())
                .ruleEvaluations(ruleEvals)
                .nextSteps(result.getNextSteps())
                .evaluatedAt(Instant.now())
                .latencyMs(latencyMs)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/batch")
    @Operation(summary = "Batch policy evaluation", description = "Evaluate multiple features at once")
    public ResponseEntity<BatchPolicyResponse> batchEvaluate(
            @Valid @RequestBody BatchPolicyRequest request,
            Authentication authentication) {
        long startTime = System.currentTimeMillis();
        List<BatchPolicyResponse.FeatureDecision> results = new ArrayList<>();

        for (FeatureName featureName : request.getFeatures()) {
            try {
                Feature feature = featureRepository.findByName(featureName);

                RuleEvaluationService.EvaluationResult result = ruleEvaluationService.evaluate(
                        request.getUser(),
                        feature,
                        request.getContext()
                );

                results.add(BatchPolicyResponse.FeatureDecision.builder()
                        .featureName(featureName.name())
                        .allowed(result.isAllowed())
                        .reason(result.getReason())
                        .matchedRules(result.getMatchedRules())
                        .nextSteps(result.getNextSteps())
                        .build());

                auditService.logDecision(DecisionAudit.builder()
                        .userId(request.getUser().getId())
                        .userName(request.getUser().getUsername())
                        .featureName(featureName.name())
                        .input(Map.of(
                                "user", request.getUser(),
                                "context", request.getContext() != null ? request.getContext() : Map.of()
                        ))
                        .decision(result.isAllowed())
                        .reason(result.getReason())
                        .latencyMs(System.currentTimeMillis() - startTime)
                        .build());

            } catch (ResourceNotFoundException e) {
                results.add(BatchPolicyResponse.FeatureDecision.builder()
                        .featureName(featureName.name())
                        .allowed(false)
                        .reason("Feature not found: " + featureName)
                        .matchedRules(List.of())
                        .nextSteps(List.of())
                        .build());
            }
        }

        long totalLatencyMs = System.currentTimeMillis() - startTime;

        BatchPolicyResponse response = BatchPolicyResponse.builder()
                .results(results)
                .evaluatedAt(Instant.now())
                .totalLatencyMs(totalLatencyMs)
                .build();

        return ResponseEntity.ok(response);
    }
}
