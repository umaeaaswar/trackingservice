package com.getrosoft.trackingservice.tracking_service.utils;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.UUID;

public final class TrackingNumberGeneratorUtil {

    // Constants
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CUSTOMER_HASH_LENGTH = 2;
    private static final int RANDOM_UUID_PART_LENGTH = 4;
    private static final int TRACKING_NUMBER_LENGTH = 16;
    private static final int BASE36_MULTIPLIER = 1000;
    private static final String TRACKING_NUMBER_REGEX = "^[A-Z0-9]{16}$"; // Regex pattern for tracking number validation

    public static String generateTrackingNumber(final String originCountryId, final String destinationCountryId, final BigDecimal weight,
                                                final UUID customerId, final String customerSlug) {
        // Step 1: Fixed components for Origin and Destination
        String origin = originCountryId.toUpperCase().substring(0, Math.min(originCountryId.length(), 2));
        String destination = destinationCountryId.toUpperCase().substring(0, Math.min(destinationCountryId.length(), 2));

        // Step 2: Customer Hash (2 characters) + Random (4 characters from UUID)
        String customerHash = customerId.toString().replaceAll("-", "").substring(0, CUSTOMER_HASH_LENGTH).toUpperCase();
        String randomUuidPart = UUID.randomUUID().toString().replaceAll("-", "").substring(0, RANDOM_UUID_PART_LENGTH).toUpperCase();
        String customerComponent = customerHash + randomUuidPart;

        // Step 3: Weight Information in Base36
        String weightBase36 = Long.toString(weight.multiply(BigDecimal.valueOf(BASE36_MULTIPLIER)).longValue(), 36).toUpperCase();

        // Step 4: Combine All Components
        int fixedLength = origin.length() + destination.length() + customerComponent.length() + weightBase36.length();
        int remainingLength = TRACKING_NUMBER_LENGTH - fixedLength;

        if (remainingLength < 0) {
            throw new IllegalArgumentException("Generated components exceed " + TRACKING_NUMBER_LENGTH + " characters. Adjust logic.");
        }

        // Generate Random Alphanumeric Suffix for Remaining Length
        String randomSuffix = generateSecureRandomAlphanumeric(remainingLength).toUpperCase();

        // Combine all into the tracking number
        String trackingNumber = origin + destination + customerComponent + weightBase36 + randomSuffix;

        // Ensure the final length is exactly 16 characters
        trackingNumber = trackingNumber.substring(0, Math.min(trackingNumber.length(), TRACKING_NUMBER_LENGTH));

        // Final Validation
        if (!trackingNumber.matches(TRACKING_NUMBER_REGEX)) {
            throw new IllegalArgumentException("Generated tracking number is invalid");
        }

        return trackingNumber;
    }

    // Generate a secure random alphanumeric string
    private static String generateSecureRandomAlphanumeric(int length) {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(ALPHANUMERIC.charAt(secureRandom.nextInt(ALPHANUMERIC.length())));
        }
        return builder.toString();
    }
}
