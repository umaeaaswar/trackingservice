package com.getrosoft.trackingservice.tracking_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Profile Controller", description = "API to retrieve the active Spring profile.")
public class ProfileController {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @GetMapping("/active-profile")
    @Operation(
            summary = "Get Active Profile",
            description = "Returns the currently active Spring Boot profile for the application.",
            tags = {"Profile"}
    )
    public String getActiveProfile() {
        return "Active Profile: " + activeProfile;
    }
}

