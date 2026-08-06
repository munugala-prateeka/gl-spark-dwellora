package com.dwellora.service.impl;

import com.dwellora.dto.OnboardingRequestDTO;
import com.dwellora.dto.OnboardingResponseDTO;
import com.dwellora.entity.OnboardingRequest;
import com.dwellora.enums.OnboardingStatus;
import com.dwellora.event.CommunityApprovedEvent;
import com.dwellora.exception.OnboardingException;
import com.dwellora.kafka.CommunityProducer;
import com.dwellora.repository.OnboardingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OnboardingServiceImplTest {

    @Mock
    private OnboardingRepository repository;

    @Mock
    private CommunityProducer producer;

    @Mock
    private Environment env;

    @InjectMocks
    private OnboardingServiceImpl onboardingService;

    private OnboardingRequestDTO requestDTO;
    private OnboardingRequest pendingRequest;

    @BeforeEach
    void setUp() {
        // Sample DTO for creating request
        requestDTO = new OnboardingRequestDTO();
        requestDTO.setApartmentName("Green Heights");
        requestDTO.setAddress("123 Main St");
        requestDTO.setCity("Hyderabad");
        requestDTO.setState("Telangana");
        requestDTO.setPincode("500081");
        requestDTO.setTotalBlocks(4);
        requestDTO.setTotalUnits(100);
        requestDTO.setManagerName("John Doe");
        requestDTO.setManagerEmail("john@example.com");
        requestDTO.setManagerPhone("9876543210");

        // Sample Entity representing saved DB row
        pendingRequest = new OnboardingRequest();
        pendingRequest.setRequestId(1);
        pendingRequest.setApartmentName("Green Heights");
        pendingRequest.setAddress("123 Main St");
        pendingRequest.setCity("Hyderabad");
        pendingRequest.setState("Telangana");
        pendingRequest.setPincode("500081");
        pendingRequest.setTotalBlocks(4);
        pendingRequest.setTotalUnits(100);
        pendingRequest.setManagerName("John Doe");
        pendingRequest.setManagerEmail("john@example.com");
        pendingRequest.setManagerPhone("9876543210");
        pendingRequest.setStatus(OnboardingStatus.PENDING);
        pendingRequest.setCreatedAt(LocalDateTime.now());
    }

    // ==========================================
    // 1. CREATE REQUEST TESTS
    // ==========================================

    @Test
    @DisplayName("US-001: Should create onboarding request with PENDING status")
    void createRequest_Success() {
        // Given
        when(repository.save(any(OnboardingRequest.class))).thenReturn(pendingRequest);

        // When
        OnboardingResponseDTO response = onboardingService.createRequest(requestDTO);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getRequestId());
        assertEquals("Green Heights", response.getApartmentName());
        assertEquals(OnboardingStatus.PENDING, response.getStatus());

        ArgumentCaptor<OnboardingRequest> captor = ArgumentCaptor.forClass(OnboardingRequest.class);
        verify(repository, times(1)).save(captor.capture());

        OnboardingRequest savedEntity = captor.getValue();
        assertEquals(OnboardingStatus.PENDING, savedEntity.getStatus());
        assertNotNull(savedEntity.getCreatedAt());
    }

    // ==========================================
    // 2. GET PENDING REQUESTS TESTS
    // ==========================================

    @Test
    @DisplayName("US-002: Should return list of pending onboarding requests")
    void getPendingRequests_ReturnsList() {
        // Given
        when(repository.findByStatus(OnboardingStatus.PENDING))
                .thenReturn(List.of(pendingRequest));

        // When
        List<OnboardingResponseDTO> results = onboardingService.getPendingRequests();

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Green Heights", results.get(0).getApartmentName());
        assertEquals(OnboardingStatus.PENDING, results.get(0).getStatus());

        verify(repository, times(1)).findByStatus(OnboardingStatus.PENDING);
    }

    @Test
    @DisplayName("US-002: Should return empty list when no pending requests exist")
    void getPendingRequests_ReturnsEmptyList() {
        // Given
        when(repository.findByStatus(OnboardingStatus.PENDING))
                .thenReturn(Collections.emptyList());

        // When
        List<OnboardingResponseDTO> results = onboardingService.getPendingRequests();

        // Then
        assertNotNull(results);
        assertTrue(results.isEmpty());
        verify(repository, times(1)).findByStatus(OnboardingStatus.PENDING);
    }

    // ==========================================
    // 3. APPROVE REQUEST TESTS
    // ==========================================

    @Test
    @DisplayName("US-003: Should approve request and publish Kafka event")
    void approveRequest_Success() {
        // Given
        when(repository.findById(1)).thenReturn(Optional.of(pendingRequest));
        when(repository.save(any(OnboardingRequest.class))).thenAnswer(i -> i.getArgument(0));

        // When
        OnboardingResponseDTO response = onboardingService.approveRequest(1);

        // Then
        assertNotNull(response);
        assertEquals(OnboardingStatus.APPROVED, response.getStatus());
        assertNotNull(response.getApprovedAt());

        // Verify Kafka event publish call and capture the event payload
        ArgumentCaptor<CommunityApprovedEvent> eventCaptor = ArgumentCaptor.forClass(CommunityApprovedEvent.class);
        verify(producer, times(1)).publish(eventCaptor.capture());

        CommunityApprovedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(1, publishedEvent.getRequestId());
        assertEquals("Green Heights", publishedEvent.getApartmentName());
        assertEquals("John Doe", publishedEvent.getManagerName());
        assertEquals("john@example.com", publishedEvent.getManagerEmail());

        verify(repository, times(1)).save(pendingRequest);
    }

    @Test
    @DisplayName("US-003: Should throw OnboardingException when approving non-existent ID")
    void approveRequest_NotFound_ThrowsException() {
        // Given
        when(repository.findById(99)).thenReturn(Optional.empty());

        // When & Then
        OnboardingException exception = assertThrows(
                OnboardingException.class,
                () -> onboardingService.approveRequest(99)
        );

        assertTrue(exception.getMessage().contains("Onboarding request not found with id : 99"));
        verify(producer, never()).publish(any());
        verify(repository, never()).save(any());
    }

    // ==========================================
    // 4. REJECT REQUEST TESTS
    // ==========================================

    @Test
    @DisplayName("US-004: Should reject onboarding request successfully")
    void rejectRequest_Success() {
        // Given
        when(repository.findById(1)).thenReturn(Optional.of(pendingRequest));
        when(repository.save(any(OnboardingRequest.class))).thenAnswer(i -> i.getArgument(0));

        // When
        OnboardingResponseDTO response = onboardingService.rejectRequest(1);

        // Then
        assertNotNull(response);
        assertEquals(OnboardingStatus.REJECTED, response.getStatus());
        verify(repository, times(1)).save(pendingRequest);
        verify(producer, never()).publish(any());
    }

    @Test
    @DisplayName("US-004: Should throw OnboardingException when rejecting non-existent ID")
    void rejectRequest_NotFound_ThrowsException() {
        // Given
        when(repository.findById(99)).thenReturn(Optional.empty());

        // When & Then
        OnboardingException exception = assertThrows(
                OnboardingException.class,
                () -> onboardingService.rejectRequest(99)
        );

        assertTrue(exception.getMessage().contains("Onboarding request not found with id : 99"));
        verify(repository, never()).save(any());


    }

    // ==========================================
// 5. GET ALL REQUESTS TESTS (US-013 / US-014)
// ==========================================

    @Test
    @DisplayName("US-013/US-014: Should return all onboarding requests regardless of status")
    void getAllRequests_ReturnsFullHistory() {
        // Given
        OnboardingRequest approved = new OnboardingRequest();
        approved.setRequestId(2);
        approved.setApartmentName("Blue Ridge");
        approved.setStatus(OnboardingStatus.APPROVED);

        when(repository.findAll()).thenReturn(List.of(pendingRequest, approved));

        // When
        List<OnboardingResponseDTO> results = onboardingService.getAllRequests();

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("US-014: Should return empty list when no onboarding requests exist")
    void getAllRequests_ReturnsEmptyList() {
        // Given
        when(repository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<OnboardingResponseDTO> results = onboardingService.getAllRequests();

        // Then
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }
}