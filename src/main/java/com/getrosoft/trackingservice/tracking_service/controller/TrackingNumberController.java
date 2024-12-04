package com.getrosoft.trackingservice.tracking_service.controller;

import com.getrosoft.trackingservice.tracking_service.dto.TrackingNumberDto;
import com.getrosoft.trackingservice.tracking_service.payload.Priority;
import com.getrosoft.trackingservice.tracking_service.payload.Status;
import com.getrosoft.trackingservice.tracking_service.payload.TrackingResponse;
import com.getrosoft.trackingservice.tracking_service.service.TrackingNumberService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/v1/api")
public class TrackingNumberController {

    private static final Logger logger = LoggerFactory.getLogger(TrackingNumberController.class);

    private final TrackingNumberService service;

    public TrackingNumberController(TrackingNumberService service) {
        this.service = service;
    }

    @Operation(summary = "Generate a new tracking number", description = "Creates a new tracking number based on the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully generated tracking number",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrackingResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping("/next-tracking-number")
    public CompletableFuture<ResponseEntity<TrackingResponse>> getNextTrackingNumberAsync(
            @Valid
            @Parameter(description = "Origin country ID", example = "US", required = true)
            @RequestParam String originCountryId,
            @Valid
            @Parameter(description = "Destination country ID", example = "IN", required = true)
            @RequestParam String destinationCountryId,
            @Valid
            @Parameter(description = "Weight in kilograms (positive number)", example = "1.5", required = true)
            @RequestParam BigDecimal weight,
            @Valid
            @Parameter(description = "Customer ID in UUID format. It should have five groups separated by hyphens: 8-4-4-4-12", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
            @RequestParam UUID customerId,
            @Valid
            @Parameter(description = "Customer slug", example = "example-customer", required = true)
            @RequestParam String customerSlug) {

        logger.info("Received request to generate tracking number with originCountryId={}, destinationCountryId={}, weight={}, customerId={}, customerSlug={}",
                originCountryId, destinationCountryId, weight, customerId, customerSlug);

        return CompletableFuture.supplyAsync(() -> {
            try {
                // Map incoming request parameters to TrackingNumberDto
               final TrackingNumberDto requestDto = TrackingNumberDto.builder()
                        .originCountryId(originCountryId)
                        .destinationCountryId(destinationCountryId)
                        .weight(weight)
                        .customerId(customerId)
                        .customerSlug(customerSlug)
                        .build();

                // Call the service layer
                TrackingNumberDto trackingNumberDto = this.service.createTrackingNumber(requestDto);

                // Build response
                TrackingResponse response = TrackingResponse.builder()
                        .trackingNumber(trackingNumberDto.getTrackingNumber())
                        .createdAt(trackingNumberDto.getCreatedAt())
                        .status(Status.SUCCESS) // Set status
                        .estimatedDelivery(LocalDate.now().plusDays(3)) // Calculate estimated delivery
                        .priority(Priority.STANDARD) // Set priority
                        .build();

                logger.info("Successfully generated tracking number: {}", response.getTrackingNumber());
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                logger.error("Error generating tracking number", e);
                throw e; // Global exception handler will catch this
            }
        });
    }

    @Operation(summary = "Fetch tracking details", description = "Retrieve tracking details for a specific tracking ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tracking details fetched successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrackingResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tracking ID not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Server error", content = @Content)
    })
    @GetMapping("/tracking-details")
    public CompletableFuture<ResponseEntity<TrackingResponse>> getTrackingDetailsAsync(
            @RequestParam String trackingId) {

        logger.info("Received request to fetch tracking details for trackingId={}", trackingId);

        return CompletableFuture.supplyAsync(() -> {
            try {
                // Fetch tracking details from the service layer
                TrackingNumberDto trackingNumberDto = service.getTrackingDetails(trackingId);

                // Build response
                TrackingResponse response = TrackingResponse.builder()
                        .trackingNumber(trackingNumberDto.getTrackingNumber())
                        .createdAt(trackingNumberDto.getCreatedAt())
                        .status(Status.IN_TRANSIT) // Set status
                        .estimatedDelivery(LocalDate.now().plusDays(3)) // Placeholder: replace with actual logic if needed
                        .priority(Priority.STANDARD) // Placeholder: replace with actual logic if needed
                        .build();

                logger.info("Successfully fetched tracking details for trackingId={}", trackingId);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                logger.error("Error fetching tracking details for trackingId={}", trackingId, e);
                throw e; // Global exception handler will catch this
            }
        });
    }

}
