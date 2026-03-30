package com.rovi.policy_engine.controller;

import com.rovi.policy_engine.dto.request.RuleRequest;
import com.rovi.policy_engine.dto.response.RuleResponse;
import com.rovi.policy_engine.service.RuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
@Tag(name = "Rule Management", description = "CRUD operations for decision rules")
public class RuleController {

    private final RuleService ruleService;

    @GetMapping
    @Operation(summary = "List all rules", description = "Returns all rules including disabled ones")
    public ResponseEntity<List<RuleResponse>> getAllRules() {
        return ResponseEntity.ok(ruleService.getAllRules());
    }

    @GetMapping("/active")
    @Operation(summary = "List active rules", description = "Returns only enabled and valid rules")
    public ResponseEntity<List<RuleResponse>> getActiveRules() {
        return ResponseEntity.ok(ruleService.getActiveRules());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get rule by ID")
    public ResponseEntity<RuleResponse> getRuleById(@PathVariable String id) {
        return ResponseEntity.ok(ruleService.getRuleById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new rule")
    public ResponseEntity<RuleResponse> createRule(@Valid @RequestBody RuleRequest request) {
        RuleResponse created = ruleService.createRule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing rule")
    public ResponseEntity<RuleResponse> updateRule(@PathVariable String id, @Valid @RequestBody RuleRequest request) {
        return ResponseEntity.ok(ruleService.updateRule(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a rule")
    public ResponseEntity<Void> deleteRule(@PathVariable String id) {
        ruleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle")
    @Operation(summary = "Toggle rule enabled/disabled status")
    public ResponseEntity<RuleResponse> toggleRule(@PathVariable String id) {
        return ResponseEntity.ok(ruleService.toggleRule(id));
    }
}
