package com.rovi.policy_engine.model;

public enum PlanType {
    FREE(1),
    PRO(2),
    ENTERPRISE(3);

    private final int level;

    PlanType(int level) {
        this.level = level;
    }

    // Compare hierarchy
    public boolean canAccess(PlanType required) {
        return this.level >= required.level;
    }
}