package com.rovi.policy_engine.dto.request;

import com.rovi.policy_engine.model.ExtendedUser;
import com.rovi.policy_engine.model.FeatureName;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulateRequest {
    
    @NotNull(message = "User data is required")
    private ExtendedUser user;
    
    @NotNull(message = "Feature name is required")
    private FeatureName featureName;
    
    private Map<String, Object> context;
}
