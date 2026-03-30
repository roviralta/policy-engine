package com.rovi.policy_engine.service;

import com.rovi.policy_engine.dto.request.RuleRequest;
import com.rovi.policy_engine.dto.response.RuleResponse;
import com.rovi.policy_engine.exception.ResourceNotFoundException;
import com.rovi.policy_engine.model.Rule;
import com.rovi.policy_engine.repository.InMemoryRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleService {

    private final InMemoryRuleRepository ruleRepository;

    public List<RuleResponse> getAllRules() {
        return ruleRepository.findAll().stream()
                .map(RuleResponse::from)
                .collect(Collectors.toList());
    }

    public List<RuleResponse> getActiveRules() {
        return ruleRepository.findAllEnabled().stream()
                .map(RuleResponse::from)
                .collect(Collectors.toList());
    }

    public RuleResponse getRuleById(String id) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found: " + id));
        return RuleResponse.from(rule);
    }

    public RuleResponse createRule(RuleRequest request) {
        Rule rule = Rule.builder()
                .name(request.getName())
                .description(request.getDescription())
                .conditions(request.getConditions())
                .logicalOperator(request.getLogicalOperator() != null ? request.getLogicalOperator() : Rule.LogicalOperator.AND)
                .action(request.getAction())
                .priority(request.getPriority())
                .validFrom(request.getValidFrom())
                .validTo(request.getValidTo())
                .enabled(request.isEnabled())
                .build();

        Rule saved = ruleRepository.save(rule);
        log.info("Created rule: {} ({})", saved.getName(), saved.getId());
        return RuleResponse.from(saved);
    }

    public RuleResponse updateRule(String id, RuleRequest request) {
        Rule existing = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found: " + id));

        Rule updated = Rule.builder()
                .id(id)
                .name(request.getName())
                .description(request.getDescription())
                .conditions(request.getConditions())
                .logicalOperator(request.getLogicalOperator() != null ? request.getLogicalOperator() : existing.getLogicalOperator())
                .action(request.getAction())
                .priority(request.getPriority())
                .validFrom(request.getValidFrom())
                .validTo(request.getValidTo())
                .enabled(request.isEnabled())
                .build();

        Rule saved = ruleRepository.save(updated);
        log.info("Updated rule: {} ({})", saved.getName(), saved.getId());
        return RuleResponse.from(saved);
    }

    public void deleteRule(String id) {
        if (!ruleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Rule not found: " + id);
        }
        ruleRepository.deleteById(id);
        log.info("Deleted rule: {}", id);
    }

    public RuleResponse toggleRule(String id) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule not found: " + id));
        
        rule.setEnabled(!rule.isEnabled());
        Rule saved = ruleRepository.save(rule);
        log.info("Toggled rule {}: enabled={}", id, saved.isEnabled());
        return RuleResponse.from(saved);
    }
}
