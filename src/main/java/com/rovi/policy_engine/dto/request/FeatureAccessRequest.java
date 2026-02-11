package com.rovi.policy_engine.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FeatureAccessRequest {

    @NotNull
    private int userId;
    @NotBlank
    private String userPlan;
    @NotBlank
    private String featureName;

    public FeatureAccessRequest(){
    }

    public FeatureAccessRequest(int userId, String userPlan, String featureName){
        this.userId = userId;
        this.userPlan = userPlan;
        this.featureName = featureName;
    }

    public int getUserId(){
        return this.userId;
    }

    public String getUserPlan(){
        return this.userPlan;
    }

    public String getFeatureName(){
        return this.featureName;
    }

}