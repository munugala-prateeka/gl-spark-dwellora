package com.dwellora.service.impl;

import com.dwellora.dto.AmenityRequestDTO;
import com.dwellora.dto.AmenityResponseDTO;
import com.dwellora.entity.Amenity;
import com.dwellora.enums.AmenityType;
import com.dwellora.enums.BookingPolicy;
import com.dwellora.exception.AmenityException;
import com.dwellora.repository.AmenityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link AmenityServiceImpl}.
 *
 * <p>Covers US-007 - Manager Configures Amenities &amp; Booking Policy.
 */
@ExtendWith(MockitoExtension.class)
class AmenityServiceImplTest {

    @Mock
    private AmenityRepository amenityRepository;

    @InjectMocks
    private AmenityServiceImpl amenityService;

    private AmenityRequestDTO requestDTO;
    private Amenity savedAmenity;

    @BeforeEach
    void setUp() {
        requestDTO = new AmenityRequestDTO();
        requestDTO.setAmenityName("Gym");
        requestDTO.setAmenityType(AmenityType.GYM);
        requestDTO.setCapacity(15);
        requestDTO.setAvailable(true);
        requestDTO.setOpeningTime(LocalTime.of(6, 0));
        requestDTO.setClosingTime(LocalTime.of(21, 0));
        requestDTO.setBookingPolicy(BookingPolicy.PER_PERSON);
        requestDTO.setSlotDurationMinutes(60);
        requestDTO.setMaxBookingsPerDay(20);

        savedAmenity = new Amenity();
        savedAmenity.setAmenityId(1L);
        savedAmenity.setApartmentId(10L);
        savedAmenity.setAmenityName("Gym");
        savedAmenity.setAmenityType(AmenityType.GYM);
        savedAmenity.setCapacity(15);
        savedAmenity.setAvailable(true);
        savedAmenity.setOpeningTime(LocalTime.of(6, 0));
        savedAmenity.setClosingTime(LocalTime.of(21, 0));
        savedAmenity.setBookingPolicy(BookingPolicy.PER_PERSON);
        savedAmenity.setSlotDurationMinutes(60);
        savedAmenity.setMaxBookingsPerDay(20);
    }

    @Test
    @DisplayName("US-007: Given amenity name, type, capacity and hours, when saved, then it appears in the apartment's amenity list")
    void addAmenity_ValidRequest_SavesAndReturnsAmenity() {
        // Given
        when(amenityRepository.existsByApartmentIdAndAmenityName(10L, "Gym")).thenReturn(false);
        when(amenityRepository.save(any(Amenity.class))).thenReturn(savedAmenity);

        // When
        AmenityResponseDTO response = amenityService.addAmenity(10L, requestDTO);

        // Then
        assertNotNull(response);
        assertEquals("Gym", response.getAmenityName());
        assertEquals(BookingPolicy.PER_PERSON, response.getBookingPolicy());
    }

    @Test
    @DisplayName("US-007: Given a duplicate amenity name in the same apartment, when saved, then it is rejected")
    void addAmenity_DuplicateName_ThrowsException() {
        // Given
        when(amenityRepository.existsByApartmentIdAndAmenityName(10L, "Gym")).thenReturn(true);

        // When & Then
        assertThrows(AmenityException.class, () -> amenityService.addAmenity(10L, requestDTO));
        verify(amenityRepository, never()).save(any());
    }

    @Test
    @DisplayName("US-007: Given an opening time not before closing time, when saved, then it is rejected")
    void addAmenity_InvalidTimeRange_ThrowsException() {
        // Given
        requestDTO.setOpeningTime(LocalTime.of(21, 0));
        requestDTO.setClosingTime(LocalTime.of(6, 0));

        when(amenityRepository.existsByApartmentIdAndAmenityName(10L, "Gym")).thenReturn(false);

        // When & Then
        AmenityException ex = assertThrows(AmenityException.class, () -> amenityService.addAmenity(10L, requestDTO));
        assertEquals("Opening time must be strictly before closing time.", ex.getMessage());
    }

    @Test
    @DisplayName("US-007: Given an unreachable Apartment Service, when saving an amenity, then it is rejected")
    void addAmenity_ApartmentServiceUnavailable_ThrowsException() {
        // Given
        when(amenityRepository.existsByApartmentIdAndAmenityName(10L, "Gym"))
                .thenThrow(new AmenityException("Apartment service is currently unavailable"));

        // When & Then
        assertThrows(AmenityException.class, () -> amenityService.addAmenity(10L, requestDTO));
        verify(amenityRepository, never()).save(any());
    }

    @Test
    @DisplayName("Given an apartment id, when amenities are requested, then only that apartment's amenities are returned")
    void getAmenitiesByApartment_ReturnsScopedList() {
        // Given
        when(amenityRepository.findByApartmentId(10L)).thenReturn(List.of(savedAmenity));

        // When
        List<AmenityResponseDTO> results = amenityService.getAmenitiesByApartment(10L);

        // Then
        assertEquals(1, results.size());
        assertEquals("Gym", results.get(0).getAmenityName());
    }

    @Test
    @DisplayName("Given a non-existent amenity, when deletion is attempted, then an AmenityException is thrown")
    void deleteAmenity_MissingId_ThrowsException() {
        // Given
        when(amenityRepository.findById(77L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(AmenityException.class, () -> amenityService.deleteAmenity(77L, 10L));
    }
}