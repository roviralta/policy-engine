package com.rovi.policy_engine.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PolicyDecision {
    private final boolean allowed;
    private final List<String> explanation;  
    private final List<String> nextSteps;    
}