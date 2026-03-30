package com.rovi.policy_engine.service;

import com.rovi.policy_engine.model.Condition;
import com.rovi.policy_engine.model.ExtendedUser;
import com.rovi.policy_engine.model.Feature;
import com.rovi.policy_engine.model.Operator;
import com.rovi.policy_engine.model.Rule;
import com.rovi.policy_engine.model.RuleAction;
import com.rovi.policy_engine.repository.FeatureRepository;
import com.rovi.policy_engine.repository.InMemoryRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleEvaluationService {

    private final InMemoryRuleRepository ruleRepository;
    private final FeatureRepository featureRepository;

    public EvaluationResult evaluate(ExtendedUser user, Feature feature, Map<String, Object> context) {
        List<RuleEvaluation> ruleEvaluations = new ArrayList<>();
        boolean allowed = true;
        String reason = "Default allow";
        List<String> matchedRules = new ArrayList<>();
        List<String> nextSteps = new ArrayList<>();

        List<Rule> rules = ruleRepository.findAllEnabled();

        for (Rule rule : rules) {
            boolean matches = evaluateConditions(rule, user, feature, context);
            
            ruleEvaluations.add(RuleEvaluation.builder()
                    .ruleId(rule.getId())
                    .ruleName(rule.getName())
                    .matched(matches)
                    .reason(matches ? "Conditions matched" : "Conditions not matched")
                    .build());

            if (matches) {
                matchedRules.add(rule.getName());

                if (rule.getAction() == RuleAction.DENY) {
                    allowed = false;
                    reason = "Denied by rule: " + rule.getName();
                    nextSteps.clear();
                    nextSteps.addAll(getNextStepsForRule(rule));
                    break;
                } else if (rule.getAction() == RuleAction.ALLOW) {
                    allowed = true;
                    reason = "Allowed by rule: " + rule.getName();
                    nextSteps.addAll(getNextStepsForRule(rule));
                }
            }
        }

        if (matchedRules.isEmpty()) {
            reason = "No matching rules found, defaulting to denied for safety";
            allowed = false;
        }

        return EvaluationResult.builder()
                .allowed(allowed)
                .reason(reason)
                .ruleEvaluations(ruleEvaluations)
                .matchedRules(matchedRules)
                .nextSteps(nextSteps)
                .build();
    }

    private boolean evaluateConditions(Rule rule, ExtendedUser user, Feature feature, Map<String, Object> context) {
        List<Condition> conditions = rule.getConditions();
        if (conditions == null || conditions.isEmpty()) {
            return false;
        }

        List<Boolean> results = new ArrayList<>();
        for (Condition condition : conditions) {
            results.add(evaluateCondition(condition, user, feature, context));
        }

        if (rule.getLogicalOperator() == Rule.LogicalOperator.AND) {
            return results.stream().allMatch(Boolean::booleanValue);
        } else {
            return results.stream().anyMatch(Boolean::booleanValue);
        }
    }

    private boolean evaluateCondition(Condition condition, ExtendedUser user, Feature feature, Map<String, Object> context) {
        Object fieldValue = getFieldValue(condition.getField(), user, feature, context);
        if (fieldValue == null) {
            return false;
        }

        return evaluateOperator(condition.getOperator(), fieldValue, condition.getValue(), condition.getSecondValue());
    }

    private Object getFieldValue(String field, ExtendedUser user, Feature feature, Map<String, Object> context) {
        return switch (field) {
            case "user.id" -> user.getId();
            case "user.plan" -> user.getPlan() != null ? user.getPlan().name() : null;
            case "user.region" -> user.getRegion();
            case "user.country" -> user.getCountry();
            case "user.email" -> user.getEmail();
            case "user.verified" -> user.isVerified();
            case "user.age" -> user.getAge();
            case "user.roles" -> user.getRoles();
            case "feature.name" -> feature != null ? feature.getName().name() : null;
            case "feature.requiredPlan" -> feature != null && feature.getRequiredPlan() != null ? feature.getRequiredPlan().name() : null;
            case "feature.enabled" -> feature != null ? feature.isEnabled() : null;
            default -> context != null ? context.get(field) : null;
        };
    }

    private boolean evaluateOperator(Operator operator, Object fieldValue, Object conditionValue, Object secondValue) {
        if (fieldValue == null) {
            return false;
        }

        String fieldStr = String.valueOf(fieldValue);
        String condStr = conditionValue != null ? String.valueOf(conditionValue) : null;

        return switch (operator) {
            case EQ -> fieldStr.equals(condStr);
            case NEQ -> !fieldStr.equals(condStr);
            case GT -> compareNumeric(fieldValue, conditionValue) > 0;
            case GTE -> compareNumeric(fieldValue, conditionValue) >= 0;
            case LT -> compareNumeric(fieldValue, conditionValue) < 0;
            case LTE -> compareNumeric(fieldValue, conditionValue) <= 0;
            case IN -> isInList(fieldValue, conditionValue);
            case NOT_IN -> !isInList(fieldValue, conditionValue);
            case CONTAINS -> fieldStr.contains(condStr);
            case NOT_CONTAINS -> !fieldStr.contains(condStr);
            case BETWEEN -> isBetween(fieldValue, conditionValue, secondValue);
            case STARTS_WITH -> fieldStr.startsWith(condStr);
            case ENDS_WITH -> fieldStr.endsWith(condStr);
        };
    }

    private int compareNumeric(Object a, Object b) {
        try {
            double numA = toDouble(a);
            double numB = toDouble(b);
            return Double.compare(numA, numB);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double toDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(String.valueOf(value));
    }

    private boolean isInList(Object fieldValue, Object conditionValue) {
        if (conditionValue instanceof List<?> list) {
            return list.stream()
                    .anyMatch(item -> String.valueOf(fieldValue).equals(String.valueOf(item)));
        }
        return false;
    }

    private boolean isBetween(Object value, Object min, Object max) {
        try {
            double numValue = toDouble(value);
            double numMin = toDouble(min);
            double numMax = toDouble(max);
            return numValue >= numMin && numValue <= numMax;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private List<String> getNextStepsForRule(Rule rule) {
        List<String> steps = new ArrayList<>();
        if (rule.getAction() == RuleAction.ALLOW) {
            steps.add("You can use this feature");
        } else if (rule.getAction() == RuleAction.DENY) {
            steps.add("Contact support to upgrade your access");
            steps.add("Review available plans at the pricing page");
        }
        return steps;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class EvaluationResult {
        private boolean allowed;
        private String reason;
        private List<RuleEvaluation> ruleEvaluations;
        private List<String> matchedRules;
        private List<String> nextSteps;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class RuleEvaluation {
        private String ruleId;
        private String ruleName;
        private boolean matched;
        private String reason;
    }
}
