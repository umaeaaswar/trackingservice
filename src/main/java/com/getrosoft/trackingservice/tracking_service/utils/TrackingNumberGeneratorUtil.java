package com.getrosoft.trackingservice.tracking_service.utils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Random;
import java.util.UUID;

public final class TrackingNumberGeneratorUtil {

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new Random();

/*    public static String generateTrackingNumber(String originCountryId, String destinationCountryId, BigDecimal weight,
                                         UUID customerId, String customerSlug) {
        // Validate input parameters (example validations shown earlier)

        // Step 1: Fixed components
        String origin = originCountryId.toUpperCase();
        String destination = destinationCountryId.toUpperCase();

        // Step 2: Hash customer ID
        String customerHash = customerId.toString().replaceAll("-", "").substring(0, 6).toUpperCase();

        // Step 3: Add weight information (Base36 encoding of weight)
        String weightBase36 = Long.toString(weight.multiply(BigDecimal.valueOf(1000)).longValue(), 36).toUpperCase();

        // Step 4: Random alphanumeric suffix
//        String randomSuffix = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 4).toUpperCase();
        String randomSuffix = generateRandomAlphanumeric(4).toUpperCase();

        // Combine all components
        String trackingNumber = origin + destination + customerHash + weightBase36 + randomSuffix;

        // Ensure it matches the regex pattern and is unique
        if (!trackingNumber.matches("^[A-Z0-9]{1,16}$")) {
            throw new IllegalArgumentException("Generated tracking number is invalid");
        }

        return trackingNumber;
    }*/

    public static String generateTrackingNumber(final String originCountryId, final String destinationCountryId, final BigDecimal weight,
                                                final UUID customerId, final String customerSlug) {
        // Validate input parameters (add necessary validations as needed)

        // Step 1: Fixed components (truncate to ensure length fits within 16 characters)
        String origin = originCountryId.toUpperCase().substring(0, Math.min(originCountryId.length(), 2));
        String destination = destinationCountryId.toUpperCase().substring(0, Math.min(destinationCountryId.length(), 2));

        // Step 2: Hash customer ID (use the first 6 characters of the UUID)
        String customerHash = customerId.toString().replaceAll("-", "").substring(0, 6).toUpperCase();

        // Step 3: Add weight information (Base36 encoding of weight)
        String weightBase36 = Long.toString(weight.multiply(BigDecimal.valueOf(1000)).longValue(), 36).toUpperCase();

        // Step 4: Random alphanumeric suffix to adjust length
        int fixedLength = origin.length() + destination.length() + customerHash.length() + weightBase36.length();
        int remainingLength = 16 - fixedLength; // Adjust remaining length to ensure total is 16 characters

        if (remainingLength < 0) {
            throw new IllegalArgumentException("Generated components exceed 16 characters. Adjust logic.");
        }

        String randomSuffix = generateRandomAlphanumeric(remainingLength).toUpperCase();

        // Combine all components
        String trackingNumber = origin + destination + customerHash + weightBase36 + randomSuffix;

        // Final validation to ensure it matches the regex pattern
        if (!trackingNumber.matches("^[A-Z0-9]{16}$")) {
            throw new IllegalArgumentException("Generated tracking number is invalid");
        }

        return trackingNumber;
    }

    private static String generateRandomAlphanumeric(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        TrackingNumberGeneratorUtil generator = new TrackingNumberGeneratorUtil();
        String trackingNumber = generator.generateTrackingNumber("MY", "ID", new BigDecimal("1.234"),
                UUID.fromString("de619854-b59b-425e-9db4-943979e1bd49"), "redbox-logistics");
        System.out.println("Generated Tracking Number: " + trackingNumber);
    }
}
