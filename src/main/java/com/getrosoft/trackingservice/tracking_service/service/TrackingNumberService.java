package com.getrosoft.trackingservice.tracking_service.service;

import com.getrosoft.trackingservice.tracking_service.dto.TrackingNumberDto;

public interface TrackingNumberService {
    TrackingNumberDto createTrackingNumber(TrackingNumberDto requestDto);

    TrackingNumberDto getTrackingDetails(String trackingId);
}
