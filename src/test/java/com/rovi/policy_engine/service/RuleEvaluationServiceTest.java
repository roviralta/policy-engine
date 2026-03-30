package com.rovi.policy_engine.service;

import com.rovi.policy_engine.model.*;
import com.rovi.policy_engine.repository.FeatureRepository;
import com.rovi.policy_engine.repository.InMemoryRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RuleEvaluationServiceTest {

    @Mock
    private InMemoryRuleRepository ruleRepository;

    @Mock
    private FeatureRepository featureRepository;

    private RuleEvaluationService service;

    @BeforeEach
    void setUp() {
        service = new RuleEvaluationService(ruleRepository, featureRepository);
    }

    @Test
    void evaluate_WithMatchingRule_ShouldReturnAllowed() {
        Rule rule = Rule.builder()
                .id("rule-1")
                .name("Pro Users Allowed")
                .action(RuleAction.ALLOW)
                .priority(1)
                .enabled(true)
                .logicalOperator(Rule.LogicalOperator.AND)
                .conditions(List.of(
                        Condition.builder()
                                .field("user.plan")
                                .operator(Operator.EQ)
                                .value("PRO")
                                .build()
                ))
                .build();

        when(ruleRepository.findAllEnabled()).thenReturn(List.of(rule));

        ExtendedUser user = ExtendedUser.builder()
                .id(1L)
                .username("testuser")
                .plan(PlanType.PRO)
                .region("EU")
                .build();

        Feature feature = Feature.builder()
                .name(FeatureName.ADVANCED_EXPORT)
                .requiredPlan(PlanType.PRO)
                .enabled(true)
                .allowedRegions(List.of("EU", "US"))
                .build();

        RuleEvaluationService.EvaluationResult result = service.evaluate(user, feature, null);

        assertTrue(result.isAllowed());
        assertEquals("Allowed by rule: Pro Users Allowed", result.getReason());
        assertTrue(result.getMatchedRules().contains("Pro Users Allowed"));
    }

    @Test
    void evaluate_WithDenyRule_ShouldReturnDenied() {
        Rule denyRule = Rule.builder()
                .id("rule-2")
                .name("Free Tier Denied")
                .action(RuleAction.DENY)
                .priority(1)
                .enabled(true)
                .logicalOperator(Rule.LogicalOperator.AND)
                .conditions(List.of(
                        Condition.builder()
                                .field("user.plan")
                                .operator(Operator.EQ)
                                .value("FREE")
                                .build()
                ))
                .build();

        when(ruleRepository.findAllEnabled()).thenReturn(List.of(denyRule));

        ExtendedUser user = ExtendedUser.builder()
                .id(1L)
                .username("testuser")
                .plan(PlanType.FREE)
                .region("EU")
                .build();

        Feature feature = Feature.builder()
                .name(FeatureName.ADVANCED_EXPORT)
                .requiredPlan(PlanType.PRO)
                .enabled(true)
                .allowedRegions(List.of("EU", "US"))
                .build();

        RuleEvaluationService.EvaluationResult result = service.evaluate(user, feature, null);

        assertFalse(result.isAllowed());
        assertTrue(result.getReason().contains("Denied by rule"));
    }

    @Test
    void evaluate_WithNoMatchingRules_ShouldReturnDenied() {
        when(ruleRepository.findAllEnabled()).thenReturn(List.of());

        ExtendedUser user = ExtendedUser.builder()
                .id(1L)
                .username("testuser")
                .plan(PlanType.FREE)
                .region("EU")
                .build();

        Feature feature = Feature.builder()
                .name(FeatureName.ADVANCED_EXPORT)
                .requiredPlan(PlanType.PRO)
                .enabled(true)
                .allowedRegions(List.of("EU", "US"))
                .build();

        RuleEvaluationService.EvaluationResult result = service.evaluate(user, feature, null);

        assertFalse(result.isAllowed());
        assertTrue(result.getReason().contains("No matching rules"));
    }

    @Test
    void evaluate_WithMultipleConditions_AndOperator_ShouldRequireAll() {
        Rule rule = Rule.builder()
                .id("rule-3")
                .name("Pro EU Users")
                .action(RuleAction.ALLOW)
                .priority(1)
                .enabled(true)
                .logicalOperator(Rule.LogicalOperator.AND)
                .conditions(List.of(
                        Condition.builder()
                                .field("user.plan")
                                .operator(Operator.EQ)
                                .value("PRO")
                                .build(),
                        Condition.builder()
                                .field("user.region")
                                .operator(Operator.EQ)
                                .value("EU")
                                .build()
                ))
                .build();

        when(ruleRepository.findAllEnabled()).thenReturn(List.of(rule));

        ExtendedUser user = ExtendedUser.builder()
                .id(1L)
                .username("testuser")
                .plan(PlanType.PRO)
                .region("US")
                .build();

        Feature feature = Feature.builder()
                .name(FeatureName.ADVANCED_EXPORT)
                .requiredPlan(PlanType.PRO)
                .enabled(true)
                .allowedRegions(List.of("EU", "US"))
                .build();

        RuleEvaluationService.EvaluationResult result = service.evaluate(user, feature, null);

        assertFalse(result.isAllowed());
        assertTrue(result.getMatchedRules().isEmpty());
    }

    @Test
    void evaluate_WithInOperator_ShouldMatchList() {
        Rule rule = Rule.builder()
                .id("rule-4")
                .name("Premium Plans")
                .action(RuleAction.ALLOW)
                .priority(1)
                .enabled(true)
                .logicalOperator(Rule.LogicalOperator.AND)
                .conditions(List.of(
                        Condition.builder()
                                .field("user.plan")
                                .operator(Operator.IN)
                                .value(List.of("PRO", "ENTERPRISE"))
                                .build()
                ))
                .build();

        when(ruleRepository.findAllEnabled()).thenReturn(List.of(rule));

        ExtendedUser user = ExtendedUser.builder()
                .id(1L)
                .username("testuser")
                .plan(PlanType.ENTERPRISE)
                .region("US")
                .build();

        Feature feature = Feature.builder()
                .name(FeatureName.BASIC_DASHBOARD)
                .requiredPlan(PlanType.FREE)
                .enabled(true)
                .allowedRegions(List.of("EU", "US"))
                .build();

        RuleEvaluationService.EvaluationResult result = service.evaluate(user, feature, null);

        assertTrue(result.isAllowed());
    }

    @Test
    void evaluate_WithHigherPriorityRule_ShouldTakePrecedence() {
        Rule lowPriorityAllow = Rule.builder()
                .id("rule-5")
                .name("Low Priority Allow")
                .action(RuleAction.ALLOW)
                .priority(10)
                .enabled(true)
                .logicalOperator(Rule.LogicalOperator.AND)
                .conditions(List.of(
                        Condition.builder()
                                .field("user.plan")
                                .operator(Operator.EQ)
                                .value("FREE")
                                .build()
                ))
                .build();

        Rule highPriorityDeny = Rule.builder()
                .id("rule-6")
                .name("High Priority Deny")
                .action(RuleAction.DENY)
                .priority(1)
                .enabled(true)
                .logicalOperator(Rule.LogicalOperator.AND)
                .conditions(List.of(
                        Condition.builder()
                                .field("user.plan")
                                .operator(Operator.IN)
                                .value(List.of("FREE", "PRO", "ENTERPRISE"))
                                .build()
                ))
                .build();

        when(ruleRepository.findAllEnabled()).thenReturn(List.of(lowPriorityAllow, highPriorityDeny));

        ExtendedUser user = ExtendedUser.builder()
                .id(1L)
                .username("testuser")
                .plan(PlanType.FREE)
                .region("US")
                .build();

        Feature feature = Feature.builder()
                .name(FeatureName.BASIC_DASHBOARD)
                .requiredPlan(PlanType.FREE)
                .enabled(true)
                .allowedRegions(List.of("EU", "US"))
                .build();

        RuleEvaluationService.EvaluationResult result = service.evaluate(user, feature, null);

        assertFalse(result.isAllowed());
        assertTrue(result.getReason().contains("High Priority Deny"));
    }
}
