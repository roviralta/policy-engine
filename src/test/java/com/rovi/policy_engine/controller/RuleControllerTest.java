package com.rovi.policy_engine.controller;

import com.rovi.policy_engine.dto.response.RuleResponse;
import com.rovi.policy_engine.model.RuleAction;
import com.rovi.policy_engine.service.RuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RuleControllerTest {

    @Mock
    private RuleService ruleService;

    private RuleController ruleController;

    @BeforeEach
    void setUp() {
        ruleController = new RuleController(ruleService);
    }

    @Test
    void getAllRules_ShouldReturnRules() {
        when(ruleService.getAllRules()).thenReturn(List.of());

        ResponseEntity<List<RuleResponse>> response = ruleController.getAllRules();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void getRuleById_ShouldReturnRule() {
        RuleResponse ruleResponse = RuleResponse.builder()
                .id("test-id")
                .name("Test Rule")
                .action(RuleAction.ALLOW)
                .priority(1)
                .enabled(true)
                .build();

        when(ruleService.getRuleById("test-id")).thenReturn(ruleResponse);

        ResponseEntity<RuleResponse> response = ruleController.getRuleById("test-id");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("test-id", response.getBody().getId());
        assertEquals("Test Rule", response.getBody().getName());
    }

    @Test
    void createRule_ShouldReturnCreatedRule() {
        com.rovi.policy_engine.dto.request.RuleRequest request = 
            com.rovi.policy_engine.dto.request.RuleRequest.builder()
                .name("New Rule")
                .description("Description")
                .action(RuleAction.ALLOW)
                .priority(1)
                .enabled(true)
                .conditions(List.of())
                .build();

        RuleResponse createdResponse = RuleResponse.builder()
                .id("new-id")
                .name("New Rule")
                .action(RuleAction.ALLOW)
                .priority(1)
                .enabled(true)
                .build();

        when(ruleService.createRule(any())).thenReturn(createdResponse);

        ResponseEntity<RuleResponse> response = ruleController.createRule(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("new-id", response.getBody().getId());
    }

    @Test
    void deleteRule_ShouldReturnNoContent() {
        doNothing().when(ruleService).deleteRule("test-id");

        ResponseEntity<Void> response = ruleController.deleteRule("test-id");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(ruleService, times(1)).deleteRule("test-id");
    }

    @Test
    void toggleRule_ShouldReturnToggledRule() {
        RuleResponse toggledResponse = RuleResponse.builder()
                .id("test-id")
                .name("Test Rule")
                .action(RuleAction.ALLOW)
                .priority(1)
                .enabled(false)
                .build();

        when(ruleService.toggleRule("test-id")).thenReturn(toggledResponse);

        ResponseEntity<RuleResponse> response = ruleController.toggleRule("test-id");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEnabled());
    }
}
