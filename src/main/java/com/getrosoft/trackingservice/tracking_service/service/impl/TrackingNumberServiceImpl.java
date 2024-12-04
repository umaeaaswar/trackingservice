package com.getrosoft.trackingservice.tracking_service.service.impl;

import com.getrosoft.trackingservice.tracking_service.dto.TrackingNumberDto;
import com.getrosoft.trackingservice.tracking_service.exceptions.DuplicateTrackingNumberException;
import com.getrosoft.trackingservice.tracking_service.exceptions.InvalidInputException;
import com.getrosoft.trackingservice.tracking_service.exceptions.TrackingIdNotFoundException;
import com.getrosoft.trackingservice.tracking_service.exceptions.TrackingNumberGenerationException;
import com.getrosoft.trackingservice.tracking_service.model.TrackingNumberEntity;
import com.getrosoft.trackingservice.tracking_service.repository.TrackingNumberRepository;
import com.getrosoft.trackingservice.tracking_service.service.TrackingNumberService;
import com.getrosoft.trackingservice.tracking_service.utils.TrackingNumberGeneratorUtil;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class TrackingNumberServiceImpl implements TrackingNumberService {

    private static final Logger logger = LoggerFactory.getLogger(TrackingNumberServiceImpl.class);

    private final TrackingNumberRepository repository;
    private final ModelMapper modelMapper;
    private final Validator validator;

    public TrackingNumberServiceImpl(TrackingNumberRepository repository, ModelMapper modelMapper, Validator validator) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.validator = validator;
    }

    public TrackingNumberDto createTrackingNumber(final TrackingNumberDto requestDto) {
        logger.debug("Creating tracking number for {}", requestDto);

        try {
            this.validateTrackingNumberEntity(requestDto);
            final String trackingNumber = this.generateUniqueTrackingNumber(requestDto);

            final TrackingNumberEntity record = new TrackingNumberEntity(
                    trackingNumber,
                    Instant.now(),
                    requestDto.getOriginCountryId(),
                    requestDto.getDestinationCountryId(),
                    requestDto.getWeight(),
                    requestDto.getCustomerId(),
                    requestDto.getCustomerSlug()
            );
            final TrackingNumberEntity savedRecord = this.repository.save(record);
            logger.info("Successfully created tracking number: {}", trackingNumber);
            return this.modelMapper.map(savedRecord, TrackingNumberDto.class);
        } catch (ConstraintViolationException e) {
            logger.error("Validation error while persisting tracking number: {}", e.getMessage(), e);
            throw new InvalidInputException("Validation failed: " + e.getMessage(), e); // Custom exception
        } catch (IllegalArgumentException e) {
            logger.error("Invalid input detected: {}", e.getMessage(), e);
            throw new TrackingNumberGenerationException("Invalid input for tracking number generation", e);
        } catch (DuplicateTrackingNumberException e) {
            logger.error("Duplicate tracking number detected: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage(), e);
            throw new TrackingNumberGenerationException("Error generating tracking number", e);
        }
    }

    public TrackingNumberDto getTrackingDetails(String trackingId) {
        logger.debug("Fetching tracking details for trackingId={}", trackingId);

        return repository.findById(trackingId)
                .map(trackingEntity -> modelMapper.map(trackingEntity, TrackingNumberDto.class))
                .orElseThrow(() -> {
                    logger.error("Tracking details not found for trackingId={}", trackingId);
                    return new TrackingIdNotFoundException("Tracking details not found for ID: " + trackingId);
                });
    }

    private String generateUniqueTrackingNumber(TrackingNumberDto requestDto) {
        logger.debug("Generating unique tracking number...");
        try {
            // Step 1: Generate an initial tracking number
            String trackingNumber = TrackingNumberGeneratorUtil.generateTrackingNumber(
                    requestDto.getOriginCountryId(),
                    requestDto.getDestinationCountryId(),
                    requestDto.getWeight(),
                    requestDto.getCustomerId(),
                    requestDto.getCustomerSlug()
            );

            // Step 2: Check for collisions and regenerate if needed
            while (repository.existsById(trackingNumber)) {
                logger.warn("Collision detected for tracking number: {}. Regenerating...", trackingNumber);

                // Use origin and destination codes as the first 4 letters
                final String originCode = requestDto.getOriginCountryId().toUpperCase().substring(0, Math.min(requestDto.getOriginCountryId().length(), 2));
                final String destinationCode = requestDto.getDestinationCountryId().toUpperCase().substring(0, Math.min(requestDto.getDestinationCountryId().length(), 2));

                // Generate the rest using UUID and append
                String randomPart = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12).toUpperCase();
                trackingNumber = originCode + destinationCode + randomPart;
            }

            return trackingNumber;
        } catch (Exception e) {
            logger.error("Error generating unique tracking number: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Unable to generate unique tracking number", e);
        }
    }

    private void validateTrackingNumberEntity(TrackingNumberDto requestDto) {
        var violations = validator.validate(requestDto);
        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed:");
            violations.forEach(violation ->
                    errorMessage.append(" ").append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append(";")
            );
            throw new ConstraintViolationException(errorMessage.toString(), violations);
        }
    }
}
