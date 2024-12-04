package com.getrosoft.trackingservice.tracking_service.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TrackingNumberGeneratorUtilTest {

    @Test
    void testGenerateTrackingNumber_ValidInputs() {
        // Valid inputs
        String origin = "IN";
        String destination = "US";
        BigDecimal weight = new BigDecimal("2.5");
        UUID customerId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        String customerSlug = "example-customer";

        // Generate tracking number
        String trackingNumber = TrackingNumberGeneratorUtil.generateTrackingNumber(origin, destination, weight, customerId, customerSlug);

        // Validate tracking number
        assertNotNull(trackingNumber, "Tracking number should not be null");
        assertEquals(16, trackingNumber.length(), "Tracking number should be exactly 16 characters");
        assertTrue(trackingNumber.matches("^[A-Z0-9]{16}$"), "Tracking number should match the regex pattern");
    }

    @Test
    void testGenerateTrackingNumber_EmptyOriginDestination() {
        // Empty origin and destination inputs
        String origin = "I";
        String destination = "U";
        BigDecimal weight = new BigDecimal("1.5");
        UUID customerId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        String customerSlug = "example-customer";

        // Generate tracking number
        String trackingNumber = TrackingNumberGeneratorUtil.generateTrackingNumber(origin, destination, weight, customerId, customerSlug);

        // Validate tracking number
        assertNotNull(trackingNumber, "Tracking number should not be null");
        assertEquals(16, trackingNumber.length(), "Tracking number should be exactly 16 characters");
    }

    @Test
    void testGenerateTrackingNumber_LongOriginDestination() {
        // Long origin and destination inputs
        String origin = "INDIA";
        String destination = "UNITEDSTATES";
        BigDecimal weight = new BigDecimal("3.0");
        UUID customerId = UUID.randomUUID();
        String customerSlug = "logistics-company";

        // Generate tracking number
        String trackingNumber = TrackingNumberGeneratorUtil.generateTrackingNumber(origin, destination, weight, customerId, customerSlug);

        // Validate tracking number
        assertNotNull(trackingNumber, "Tracking number should not be null");
        assertEquals(16, trackingNumber.length(), "Tracking number should be exactly 16 characters");
    }

    @Test
    void testGenerateTrackingNumber_ZeroWeight() {
        // Test with zero weight
        String origin = "JP";
        String destination = "CN";
        BigDecimal weight = BigDecimal.ZERO;
        UUID customerId = UUID.randomUUID();
        String customerSlug = "zero-weight-test";

        // Generate tracking number
        String trackingNumber = TrackingNumberGeneratorUtil.generateTrackingNumber(origin, destination, weight, customerId, customerSlug);

        // Validate tracking number
        assertNotNull(trackingNumber, "Tracking number should not be null");
        assertEquals(16, trackingNumber.length(), "Tracking number should be exactly 16 characters");
    }

    @Test
    void testGenerateTrackingNumber_HighWeight() {
        // Test with very high weight
        String origin = "UK";
        String destination = "DE";
        BigDecimal weight = new BigDecimal("999999.99");
        UUID customerId = UUID.randomUUID();
        String customerSlug = "heavy-weight-test";

        // Generate tracking number
        String trackingNumber = TrackingNumberGeneratorUtil.generateTrackingNumber(origin, destination, weight, customerId, customerSlug);

        // Validate tracking number
        assertNotNull(trackingNumber, "Tracking number should not be null");
        assertEquals(16, trackingNumber.length(), "Tracking number should be exactly 16 characters");
    }

    @Test
    void testGenerateTrackingNumber_InvalidRegex() {
        // Test invalid regex validation
        String origin = "IN";
        String destination = "US";
        BigDecimal weight = new BigDecimal("1.5");
        UUID customerId = UUID.randomUUID();
        String customerSlug = "regex-test";

        // Generate tracking number
        String trackingNumber = TrackingNumberGeneratorUtil.generateTrackingNumber(origin, destination, weight, customerId, customerSlug);

        // Modify tracking number to make it invalid
        String invalidTrackingNumber = trackingNumber.toLowerCase();

        // Validate against regex
        assertFalse(invalidTrackingNumber.matches("^[A-Z0-9]{16}$"), "Tracking number should not match the regex pattern if altered");
    }

    @Test
    void testGenerateTrackingNumber_NullInputs() {
        // Test with null inputs
        Exception exception = assertThrows(NullPointerException.class, () -> {
            TrackingNumberGeneratorUtil.generateTrackingNumber(null, null, null, null, null);
        });

        assertEquals("Cannot invoke \"String.toUpperCase()\" because \"originCountryId\" is null", exception.getMessage());
    }
}
