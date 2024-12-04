package com.getrosoft.trackingservice.tracking_service.repository;

import com.getrosoft.trackingservice.tracking_service.model.TrackingNumberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackingNumberRepository extends JpaRepository<TrackingNumberEntity, String> {
}
