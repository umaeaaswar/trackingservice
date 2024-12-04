package com.getrosoft.trackingservice.tracking_service.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class TrackingResponse {
    private String trackingNumber;
    private Instant createdAt;
    private Status status;
    private LocalDate estimatedDelivery;
    private Priority priority;
}

