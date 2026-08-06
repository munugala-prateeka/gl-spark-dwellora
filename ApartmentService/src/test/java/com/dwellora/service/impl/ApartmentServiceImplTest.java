package com.dwellora.service.impl;

import com.dwellora.dto.ApartmentRequestDTO;
import com.dwellora.dto.ApartmentResponseDTO;
import com.dwellora.entity.Apartment;
import com.dwellora.enums.Status;
import com.dwellora.exception.ApartmentException;
import com.dwellora.repository.ApartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ApartmentServiceImpl}.
 *
 * <p>Backs US-003 - Platform Admin Reviews Onboarding Requests: once a request is
 * approved, the Apartment created here (via the community-approved Kafka flow) must be
 * ACTIVE and independently addressable by every other microservice.
 */
@ExtendWith(MockitoExtension.class)
class ApartmentServiceImplTest {

    @Mock
    private ApartmentRepository apartmentRepository;

    @InjectMocks
    private ApartmentServiceImpl apartmentService;

    private ApartmentRequestDTO requestDTO;
    private Apartment savedApartment;

    @BeforeEach
    void setUp() {
        requestDTO = new ApartmentRequestDTO();
        requestDTO.setApartmentName("Green Heights");
        requestDTO.setAddress("123 Main St");
        requestDTO.setCity("Hyderabad");
        requestDTO.setState("Telangana");
        requestDTO.setPincode("500081");
        requestDTO.setTotalBlocks(4);
        requestDTO.setTotalUnits(100);

        savedApartment = new Apartment();
        savedApartment.setApartmentId(1);
        savedApartment.setApartmentName("Green Heights");
        savedApartment.setAddress("123 Main St");
        savedApartment.setCity("Hyderabad");
        savedApartment.setState("Telangana");
        savedApartment.setPincode("500081");
        savedApartment.setTotalBlocks(4);
        savedApartment.setTotalUnits(100);
        savedApartment.setStatus(Status.ACTIVE);
    }

    @Test
    @DisplayName("US-003: Given valid apartment details, when provisioned, then it is created with ACTIVE status")
    void addApartment_ValidRequest_CreatesActiveApartment() {
        // Given
        when(apartmentRepository.save(any(Apartment.class))).thenReturn(savedApartment);

        // When
        ApartmentResponseDTO response = apartmentService.addApartment(requestDTO);

        // Then
        assertNotNull(response);
        assertEquals(Status.ACTIVE, response.getStatus());
        assertEquals("Green Heights", response.getApartmentName());
        verify(apartmentRepository, times(1)).save(any(Apartment.class));
    }

    @Test
    @DisplayName("Given an existing apartment id, when fetched, then its details are returned")
    void getApartmentById_ExistingId_ReturnsApartment() {
        // Given
        when(apartmentRepository.findById(1)).thenReturn(Optional.of(savedApartment));

        // When
        ApartmentResponseDTO response = apartmentService.getApartmentById(1);

        // Then
        assertEquals(1, response.getApartmentId());
        assertEquals("Green Heights", response.getApartmentName());
    }

    @Test
    @DisplayName("Given a missing apartment id, when fetched, then an ApartmentException is thrown")
    void getApartmentById_MissingId_ThrowsException() {
        // Given
        when(apartmentRepository.findById(99)).thenReturn(Optional.empty());

        // When & Then
        ApartmentException ex = assertThrows(ApartmentException.class,
                () -> apartmentService.getApartmentById(99));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    @DisplayName("Given apartments exist, when listing all, then every apartment is returned")
    void getAllApartments_ReturnsFullList() {
        // Given
        when(apartmentRepository.findAll()).thenReturn(List.of(savedApartment));

        // When
        List<ApartmentResponseDTO> results = apartmentService.getAllApartments();

        // Then
        assertEquals(1, results.size());
        assertEquals("Green Heights", results.get(0).getApartmentName());
    }

    @Test
    @DisplayName("Given a valid update, when saved, then the apartment reflects the new details")
    void updateApartment_ValidRequest_UpdatesFields() {
        // Given
        ApartmentRequestDTO update = new ApartmentRequestDTO();
        update.setApartmentName("Green Heights Phase 2");
        update.setAddress("123 Main St");
        update.setCity("Hyderabad");
        update.setState("Telangana");
        update.setPincode("500081");
        update.setTotalBlocks(6);
        update.setTotalUnits(150);

        when(apartmentRepository.findById(1)).thenReturn(Optional.of(savedApartment));
        when(apartmentRepository.save(any(Apartment.class))).thenAnswer(i -> i.getArgument(0));

        // When
        ApartmentResponseDTO response = apartmentService.updateApartment(1, update);

        // Then
        assertEquals("Green Heights Phase 2", response.getApartmentName());
        assertEquals(6, response.getTotalBlocks());
    }

    @Test
    @DisplayName("Given a non-existent apartment, when deletion is attempted, then an ApartmentException is thrown")
    void deleteApartment_MissingId_ThrowsException() {
        // Given
        when(apartmentRepository.existsById(42)).thenReturn(false);

        // When & Then
        assertThrows(ApartmentException.class, () -> apartmentService.deleteApartment(42));
        verify(apartmentRepository, never()).deleteById(any());
    }
}
