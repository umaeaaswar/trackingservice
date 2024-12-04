package com.getrosoft.trackingservice.tracking_service.service.impl;

import com.getrosoft.trackingservice.tracking_service.dto.TrackingNumberDto;
import com.getrosoft.trackingservice.tracking_service.exceptions.DuplicateTrackingNumberException;
import com.getrosoft.trackingservice.tracking_service.exceptions.InvalidInputException;
import com.getrosoft.trackingservice.tracking_service.exceptions.TrackingIdNotFoundException;
import com.getrosoft.trackingservice.tracking_service.exceptions.TrackingNumberGenerationException;
import com.getrosoft.trackingservice.tracking_service.model.TrackingNumberEntity;
import com.getrosoft.trackingservice.tracking_service.repository.TrackingNumberRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrackingNumberServiceImplTest {

    @Mock
    private TrackingNumberRepository repository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Validator validator;

    private TrackingNumberServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new TrackingNumberServiceImpl(repository, modelMapper, validator);
    }

    @Test
    void testCreateTrackingNumber_Success() {
        // Arrange
        TrackingNumberDto requestDto = new TrackingNumberDto("IN", "US", BigDecimal.valueOf(2.5), UUID.randomUUID(), "customer-slug");
        TrackingNumberEntity mockEntity = new TrackingNumberEntity(
                "INUS123456789012", Instant.now(), "IN", "US", BigDecimal.valueOf(2.5), UUID.randomUUID(), "customer-slug"
        );

        when(validator.validate(requestDto)).thenReturn(Set.of());
        when(repository.existsById(anyString())).thenReturn(false);
        when(repository.save(any(TrackingNumberEntity.class))).thenReturn(mockEntity);
        when(modelMapper.map(any(TrackingNumberEntity.class), eq(TrackingNumberDto.class))).thenReturn(requestDto);

        // Act
        TrackingNumberDto result = service.createTrackingNumber(requestDto);

        // Assert
        assertNotNull(result);
        verify(repository, times(1)).save(any(TrackingNumberEntity.class));
        verify(validator, times(1)).validate(requestDto);
    }

    @Test
    void testCreateTrackingNumber_DuplicateCollisionHandled() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        TrackingNumberDto requestDto = new TrackingNumberDto("IN", "US", BigDecimal.valueOf(2.5), customerId, "customer-slug");
        TrackingNumberEntity mockEntity = new TrackingNumberEntity(
                "INUS123456789012",
                Instant.now(),
                "IN",
                "US",
                BigDecimal.valueOf(2.5),
                customerId,
                "customer-slug"
        );
        TrackingNumberDto expectedDto = TrackingNumberDto.builder()
                .originCountryId("IN")
                .destinationCountryId("US")
                .weight(BigDecimal.valueOf(2.5))
                .customerId(customerId)
                .customerSlug("customer-slug")
                .build();

        when(validator.validate(requestDto)).thenReturn(Set.of());
        when(repository.existsById(anyString()))
                .thenReturn(true)  // Simulate collision on first attempt
                .thenReturn(false); // Succeed on second attempt
        when(repository.save(any(TrackingNumberEntity.class))).thenReturn(mockEntity);
        when(modelMapper.map(mockEntity, TrackingNumberDto.class)).thenReturn(expectedDto);

        // Act
        TrackingNumberDto result = service.createTrackingNumber(requestDto);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(expectedDto.getOriginCountryId(), result.getOriginCountryId());
        assertEquals(expectedDto.getDestinationCountryId(), result.getDestinationCountryId());
        verify(repository, times(2)).existsById(anyString());
        verify(repository, times(1)).save(any(TrackingNumberEntity.class));
        verify(modelMapper, times(1)).map(mockEntity, TrackingNumberDto.class);
    }


    @Test
    void testCreateTrackingNumber_InvalidInput() {
        // Arrange
        TrackingNumberDto requestDto = new TrackingNumberDto(null, null, null, null, null);

        // Mock a constraint violation
        @SuppressWarnings("unchecked")
        ConstraintViolation<TrackingNumberDto> mockViolation = mock(ConstraintViolation.class);

        Path mockPath = mock(Path.class);
        when(mockPath.toString()).thenReturn("originCountryId"); // Mock the Path to return the property name
        when(mockViolation.getPropertyPath()).thenReturn(mockPath); // Return the mocked Path
        when(mockViolation.getMessage()).thenReturn("must not be null"); // Mock the validation message

        // Create a set of violations
        Set<ConstraintViolation<TrackingNumberDto>> violations = Set.of(mockViolation);

        // Mock the validator to return the violations
        when(validator.validate(requestDto)).thenReturn(violations);

        // Act & Assert
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> service.createTrackingNumber(requestDto));
        assertTrue(exception.getMessage().contains("Validation failed"), "Exception message should contain validation error");
        assertTrue(exception.getMessage().contains("originCountryId"), "Exception message should contain the invalid field name");
        verify(validator, times(1)).validate(requestDto);
    }



    @Test
    void testCreateTrackingNumber_UnexpectedError() {
        // Arrange
        TrackingNumberDto requestDto = new TrackingNumberDto("IN", "US", BigDecimal.valueOf(1.0), UUID.randomUUID(), "customer-slug");

        when(validator.validate(requestDto)).thenReturn(Set.of());
        when(repository.existsById(anyString())).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        TrackingNumberGenerationException exception = assertThrows(TrackingNumberGenerationException.class, () -> service.createTrackingNumber(requestDto));
        assertTrue(exception.getMessage().contains("Invalid input for tracking number generation"));
        verify(repository, times(1)).existsById(anyString());
    }

    @Test
    void testGetTrackingDetails_Success() {
        // Arrange
        String trackingId = "INUS123456789012";
        TrackingNumberEntity mockEntity = new TrackingNumberEntity(
                trackingId, Instant.now(), "IN", "US", BigDecimal.valueOf(2.5), UUID.randomUUID(), "customer-slug"
        );
        TrackingNumberDto mockDto = new TrackingNumberDto("IN", "US", BigDecimal.valueOf(2.5), UUID.randomUUID(), "customer-slug");

        when(repository.findById(trackingId)).thenReturn(Optional.of(mockEntity));
        when(modelMapper.map(mockEntity, TrackingNumberDto.class)).thenReturn(mockDto);

        // Act
        TrackingNumberDto result = service.getTrackingDetails(trackingId);

        // Assert
        assertNotNull(result);
        verify(repository, times(1)).findById(trackingId);
    }

    @Test
    void testGetTrackingDetails_TrackingIdNotFound() {
        // Arrange
        String trackingId = "INUS123456789012";

        when(repository.findById(trackingId)).thenReturn(Optional.empty());

        // Act & Assert
        TrackingIdNotFoundException exception = assertThrows(TrackingIdNotFoundException.class, () -> service.getTrackingDetails(trackingId));
        assertTrue(exception.getMessage().contains("Tracking details not found"));
        verify(repository, times(1)).findById(trackingId);
    }
}
