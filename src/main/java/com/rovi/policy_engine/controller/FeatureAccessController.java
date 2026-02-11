package com.rovi.policy_engine.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rovi.policy_engine.dto.request.FeatureAccessRequest;
import com.rovi.policy_engine.dto.response.FeatureAccessResponse;
import com.rovi.policy_engine.service.FeatureAccessService;

@RestController
@RequestMapping("/api/feature-access")
public class FeatureAccessController {

    private final FeatureAccessService service;

    public FeatureAccessController(FeatureAccessService service) {
        this.service = service;
    }

	@PostMapping("/check")
    public FeatureAccessResponse checkFeatureAccess(@RequestBody FeatureAccessRequest request) {
        return service.checkFeatureAccess(request); 
    }
}