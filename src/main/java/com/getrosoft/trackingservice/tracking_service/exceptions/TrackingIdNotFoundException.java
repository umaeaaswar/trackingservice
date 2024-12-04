package com.getrosoft.trackingservice.tracking_service.exceptions;

public class TrackingIdNotFoundException extends RuntimeException {

    public TrackingIdNotFoundException(String message) {
        super(message);
    }

    public TrackingIdNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
