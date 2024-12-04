package com.getrosoft.trackingservice.tracking_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackingNumberDto {

    private String trackingNumber;

    private Instant createdAt;

    @Schema(description = "Origin country ID", example = "US")
    @NotBlank(message = "Origin country ID cannot be blank")
    @Size(max = 3, message = "Origin country ID must be at most 3 characters long")
    private String originCountryId;

    @Schema(description = "Destination country ID", example = "IN")
    @NotBlank(message = "Destination country ID cannot be blank")
    @Size(max = 3, message = "Destination country ID must be at most 3 characters long")
    private String destinationCountryId;

    @Schema(description = "Weight in kilograms", example = "1.5")
    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.1", message = "Weight must be at least 0.1")
    @Positive(message = "Weight must be a positive number")
    private BigDecimal weight;

    @Schema(description = "Customer ID in UUID format", example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull(message = "Customer ID is required")
    private UUID customerId;

    @Schema(description = "Customer slug", example = "example-customer")
    @NotBlank(message = "Customer slug cannot be blank")
    @Size(max = 50, message = "Customer slug must be at most 50 characters long")
    private String customerSlug;
}

