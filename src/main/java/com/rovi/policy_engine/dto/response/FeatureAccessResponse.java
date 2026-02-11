package com.rovi.policy_engine.dto.response;

import java.util.List;

public class FeatureAccessResponse{

    private boolean allowed;
    private List<String> explanation;

    public FeatureAccessResponse(){
    }

    public FeatureAccessResponse(boolean allowed, List<String> explanation){
        this.allowed = allowed;
        this.explanation = explanation;
    }

    public boolean isAllowed(){
        return this.allowed;
    }

    public List<String> getExplanation(){
        return this.explanation;
    }
}