package com.rovi.policy_engine.repository;

import com.rovi.policy_engine.model.Rule;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryRuleRepository {

    private final ConcurrentHashMap<String, Rule> rules = new ConcurrentHashMap<>();

    public InMemoryRuleRepository() {
        loadSampleRules();
    }

    private void loadSampleRules() {
        rules.put("rule-1", Rule.builder()
                .id("rule-1")
                .name("Premium EU Users Access")
                .description("Allow PRO plan users in EU to access advanced features")
                .priority(1)
                .enabled(true)
                .action(com.rovi.policy_engine.model.RuleAction.ALLOW)
                .logicalOperator(Rule.LogicalOperator.AND)
                .validFrom(Instant.parse("2026-01-01T00:00:00Z"))
                .conditions(List.of(
                        com.rovi.policy_engine.model.Condition.builder()
                                .field("user.plan")
                                .operator(com.rovi.policy_engine.model.Operator.IN)
                                .value(List.of("PRO", "ENTERPRISE"))
                                .build(),
                        com.rovi.policy_engine.model.Condition.builder()
                                .field("user.region")
                                .operator(com.rovi.policy_engine.model.Operator.EQ)
                                .value("EU")
                                .build()
                ))
                .build());

        rules.put("rule-2", Rule.builder()
                .id("rule-2")
                .name("Deny Suspended Users")
                .description("Deny access to suspended users")
                .priority(1)
                .enabled(true)
                .action(com.rovi.policy_engine.model.RuleAction.DENY)
                .logicalOperator(Rule.LogicalOperator.AND)
                .conditions(List.of(
                        com.rovi.policy_engine.model.Condition.builder()
                                .field("user.suspended")
                                .operator(com.rovi.policy_engine.model.Operator.EQ)
                                .value(true)
                                .build()
                ))
                .build());

        rules.put("rule-3", Rule.builder()
                .id("rule-3")
                .name("Enterprise Always Allowed")
                .description("Enterprise users can access everything")
                .priority(2)
                .enabled(true)
                .action(com.rovi.policy_engine.model.RuleAction.ALLOW)
                .logicalOperator(Rule.LogicalOperator.AND)
                .conditions(List.of(
                        com.rovi.policy_engine.model.Condition.builder()
                                .field("user.plan")
                                .operator(com.rovi.policy_engine.model.Operator.EQ)
                                .value("ENTERPRISE")
                                .build()
                ))
                .build());
    }

    public List<Rule> findAll() {
        return rules.values().stream()
                .sorted(Comparator.comparingInt(Rule::getPriority))
                .collect(Collectors.toList());
    }

    public List<Rule> findAllEnabled() {
        Instant now = Instant.now();
        return rules.values().stream()
                .filter(Rule::isEnabled)
                .filter(rule -> rule.getValidFrom() == null || rule.getValidFrom().isBefore(now))
                .filter(rule -> rule.getValidTo() == null || rule.getValidTo().isAfter(now))
                .sorted(Comparator.comparingInt(Rule::getPriority))
                .collect(Collectors.toList());
    }

    public Optional<Rule> findById(String id) {
        return Optional.ofNullable(rules.get(id));
    }

    public Rule save(Rule rule) {
        if (rule.getId() == null) {
            rule.setId("rule-" + System.currentTimeMillis());
        }
        rules.put(rule.getId(), rule);
        return rule;
    }

    public void deleteById(String id) {
        rules.remove(id);
    }

    public boolean existsById(String id) {
        return rules.containsKey(id);
    }
}
