package com.getrosoft.trackingservice.tracking_service.exceptions;

public class DuplicateTrackingNumberException extends RuntimeException {
    public DuplicateTrackingNumberException(String message) {
        super(message);
    }

    public DuplicateTrackingNumberException(String message, Throwable cause) {
        super(message, cause);
    }
}
