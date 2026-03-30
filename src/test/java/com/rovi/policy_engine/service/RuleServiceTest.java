package com.rovi.policy_engine.service;

import com.rovi.policy_engine.dto.request.RuleRequest;
import com.rovi.policy_engine.dto.response.RuleResponse;
import com.rovi.policy_engine.exception.ResourceNotFoundException;
import com.rovi.policy_engine.model.Condition;
import com.rovi.policy_engine.model.Operator;
import com.rovi.policy_engine.model.Rule;
import com.rovi.policy_engine.model.RuleAction;
import com.rovi.policy_engine.repository.InMemoryRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RuleServiceTest {

    private RuleService ruleService;
    private InMemoryRuleRepository ruleRepository;

    @BeforeEach
    void setUp() {
        ruleRepository = new InMemoryRuleRepository();
        ruleService = new RuleService(ruleRepository);
    }

    @Test
    void getAllRules_ShouldReturnAllRules() {
        List<RuleResponse> rules = ruleService.getAllRules();

        assertNotNull(rules);
        assertFalse(rules.isEmpty());
    }

    @Test
    void getActiveRules_ShouldReturnOnlyEnabledRules() {
        List<RuleResponse> rules = ruleService.getActiveRules();

        assertNotNull(rules);
        rules.forEach(rule -> assertTrue(rule.isEnabled()));
    }

    @Test
    void getRuleById_WithExistingId_ShouldReturnRule() {
        List<RuleResponse> allRules = ruleService.getAllRules();
        String existingId = allRules.get(0).getId();

        RuleResponse rule = ruleService.getRuleById(existingId);

        assertNotNull(rule);
        assertEquals(existingId, rule.getId());
    }

    @Test
    void getRuleById_WithNonExistingId_ShouldThrowException() {
        assertThrows(ResourceNotFoundException.class, () -> 
            ruleService.getRuleById("non-existent-id")
        );
    }

    @Test
    void createRule_ShouldSaveAndReturnRule() {
        RuleRequest request = RuleRequest.builder()
                .name("Test Rule")
                .description("A test rule")
                .action(RuleAction.ALLOW)
                .priority(5)
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

        RuleResponse created = ruleService.createRule(request);

        assertNotNull(created);
        assertEquals("Test Rule", created.getName());
        assertEquals(RuleAction.ALLOW, created.getAction());
        assertTrue(created.isEnabled());
    }

    @Test
    void updateRule_WithExistingId_ShouldUpdateRule() {
        List<RuleResponse> allRules = ruleService.getAllRules();
        String existingId = allRules.get(0).getId();

        RuleRequest updateRequest = RuleRequest.builder()
                .name("Updated Rule Name")
                .description("Updated description")
                .action(RuleAction.DENY)
                .priority(10)
                .enabled(false)
                .logicalOperator(Rule.LogicalOperator.OR)
                .conditions(List.of())
                .build();

        RuleResponse updated = ruleService.updateRule(existingId, updateRequest);

        assertEquals("Updated Rule Name", updated.getName());
        assertEquals(RuleAction.DENY, updated.getAction());
    }

    @Test
    void deleteRule_WithExistingId_ShouldDelete() {
        List<RuleResponse> allRules = ruleService.getAllRules();
        String existingId = allRules.get(0).getId();

        assertDoesNotThrow(() -> ruleService.deleteRule(existingId));
        assertThrows(ResourceNotFoundException.class, () -> 
            ruleService.getRuleById(existingId)
        );
    }

    @Test
    void toggleRule_ShouldFlipEnabledStatus() {
        List<RuleResponse> allRules = ruleService.getAllRules();
        String existingId = allRules.get(0).getId();
        boolean originalStatus = allRules.get(0).isEnabled();

        RuleResponse toggled = ruleService.toggleRule(existingId);

        assertEquals(!originalStatus, toggled.isEnabled());
    }
}
