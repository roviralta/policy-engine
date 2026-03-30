package com.rovi.policy_engine.dto.request;

import com.rovi.policy_engine.model.ExtendedUser;
import com.rovi.policy_engine.model.FeatureName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class BatchPolicyRequest {
    
    @NotNull(message = "User data is required")
    private ExtendedUser user;
    
    @NotEmpty(message = "At least one feature is required")
    private List<FeatureName> features;
    
    private Map<String, Object> context;
}
