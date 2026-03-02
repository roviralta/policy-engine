package com.rovi.policy_engine.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class PolicyResponse {
    private boolean allowed;
    private List<String> explanation;
    private List<String> nextSteps;
}