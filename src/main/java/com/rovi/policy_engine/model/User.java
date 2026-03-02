package com.rovi.policy_engine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class User {
    private final Long id;
    private final PlanType plan;
    private final String region; 
}