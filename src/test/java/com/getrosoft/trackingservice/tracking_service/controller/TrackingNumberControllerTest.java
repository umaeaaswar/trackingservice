package com.getrosoft.trackingservice.tracking_service.controller;

import com.getrosoft.trackingservice.tracking_service.dto.TrackingNumberDto;
import com.getrosoft.trackingservice.tracking_service.payload.Priority;
import com.getrosoft.trackingservice.tracking_service.payload.Status;
import com.getrosoft.trackingservice.tracking_service.payload.TrackingResponse;
import com.getrosoft.trackingservice.tracking_service.service.TrackingNumberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrackingNumberControllerTest {

    @Mock
    private TrackingNumberService service;

    private TrackingNumberController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new TrackingNumberController(service);
    }

    @Test
    void testGetNextTrackingNumberAsync_Success() throws Exception {
        // Arrange
        String origin = "US";
        String destination = "IN";
        BigDecimal weight = BigDecimal.valueOf(1.5);
        UUID customerId = UUID.randomUUID();
        String customerSlug = "example-customer";

        TrackingNumberDto mockDto = TrackingNumberDto.builder()
                .trackingNumber("TR123456789")
                .createdAt(Instant.now())
                .originCountryId(origin)
                .destinationCountryId(destination)
                .weight(weight)
                .customerId(customerId)
                .customerSlug(customerSlug)
                .build();

        when(service.createTrackingNumber(any(TrackingNumberDto.class))).thenReturn(mockDto);

        // Act
        CompletableFuture<ResponseEntity<TrackingResponse>> futureResponse = controller.getNextTrackingNumberAsync(origin, destination, weight, customerId, customerSlug);
        ResponseEntity<TrackingResponse> responseEntity = futureResponse.get();

        // Assert
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        TrackingResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals("TR123456789", response.getTrackingNumber());
        assertEquals(Status.SUCCESS, response.getStatus());
        assertEquals(Priority.STANDARD, response.getPriority());
        verify(service, times(1)).createTrackingNumber(any(TrackingNumberDto.class));
    }


    @Test
    void testGetTrackingDetailsAsync_Success() throws Exception {
        // Arrange
        String trackingId = "TR123456789";

        TrackingNumberDto mockDto = TrackingNumberDto.builder()
                .trackingNumber(trackingId)
                .createdAt(Instant.now())
                .originCountryId("US")
                .destinationCountryId("IN")
                .weight(BigDecimal.valueOf(1.5))
                .customerId(UUID.randomUUID())
                .customerSlug("example-customer")
                .build();

        when(service.getTrackingDetails(trackingId)).thenReturn(mockDto);

        // Act
        CompletableFuture<ResponseEntity<TrackingResponse>> futureResponse = controller.getTrackingDetailsAsync(trackingId);
        ResponseEntity<TrackingResponse> responseEntity = futureResponse.get();

        // Assert
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        TrackingResponse response = responseEntity.getBody();
        assertNotNull(response);
        assertEquals(trackingId, response.getTrackingNumber());
        assertEquals(Status.IN_TRANSIT, response.getStatus());
        verify(service, times(1)).getTrackingDetails(trackingId);
    }

    @Test
    void testGetTrackingDetailsAsync_TrackingIdNotFound() throws Exception {
        // Arrange
        String trackingId = "TR123456789";

        when(service.getTrackingDetails(trackingId)).thenThrow(new RuntimeException("Tracking ID not found"));

        // Act & Assert
        CompletableFuture<ResponseEntity<TrackingResponse>> futureResponse = controller.getTrackingDetailsAsync(trackingId);

        Exception exception = assertThrows(Exception.class, futureResponse::get);
        assertNotNull(exception);
        assertTrue(exception.getCause().getMessage().contains("Tracking ID not found"));
        verify(service, times(1)).getTrackingDetails(trackingId);
    }

    @Test
    void testGetTrackingDetailsAsync_InternalServerError() throws Exception {
        // Arrange
        String trackingId = "TR123456789";

        when(service.getTrackingDetails(trackingId)).thenThrow(new RuntimeException("Internal server error"));

        // Act & Assert
        CompletableFuture<ResponseEntity<TrackingResponse>> futureResponse = controller.getTrackingDetailsAsync(trackingId);

        Exception exception = assertThrows(Exception.class, futureResponse::get);
        assertNotNull(exception);
        assertTrue(exception.getCause().getMessage().contains("Internal server error"));
        verify(service, times(1)).getTrackingDetails(trackingId);
    }
}
