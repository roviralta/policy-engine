package com.rovi.policy_engine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtendedUser {
    private Long id;
    private String username;
    private PlanType plan;
    private String region;
    private String email;
    private String country;
    private int age;
    private boolean verified;
    private List<String> roles;
    private Map<String, Object> customAttributes;
}
