package com.dwellora.service.impl;

import com.dwellora.client.UserClient;
import com.dwellora.dto.ComplaintRequestDTO;
import com.dwellora.dto.ComplaintResponseDTO;
import com.dwellora.dto.ComplaintUpdateDTO;
import com.dwellora.dto.UserDTO;
import com.dwellora.entity.Complaint;
import com.dwellora.enums.ComplaintStatus;
import com.dwellora.exception.ComplaintException;
import com.dwellora.repository.ComplaintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ComplaintServiceImpl}.
 *
 * <p>Covers US-011 - Resident Raises a Complaint, and
 * US-012 - Manager Resolves a Complaint.
 */
@ExtendWith(MockitoExtension.class)
class ComplaintServiceImplTest {

    @Mock
    private ComplaintRepository repository;

    @Mock
    private UserClient userClient;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private ComplaintServiceImpl complaintService;

    private UserDTO resident;
    private ComplaintRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        resident = new UserDTO();
        resident.setUserId(100L);
        resident.setApartmentId(10L);
        resident.setFullName("Alice Resident");
        resident.setFlatNumber("A-101");
        resident.setRole("RESIDENT");

        requestDTO = new ComplaintRequestDTO();
        requestDTO.setCategory("Plumbing");
        requestDTO.setDescription("Kitchen tap is leaking");
    }

    // ==========================================
    // US-011: RAISE A COMPLAINT
    // ==========================================

    @Test
    @DisplayName(
            "US-011: Given a category and description, when submitted, then a complaint is created "
                    + "with status OPEN linked to the resident's flat")
    void raiseComplaint_ValidRequest_CreatesOpenComplaint() {

        // Given
        when(userClient.getUserById(100L)).thenReturn(resident);

        when(repository.save(any(Complaint.class))).thenAnswer(i -> {
            Complaint c = i.getArgument(0);
            c.setComplaintId(1L);
            return c;
        });

        // When
        ComplaintResponseDTO response =
                complaintService.raiseComplaint(100L, 10L, requestDTO);

        // Then
        assertNotNull(response);
        assertEquals(ComplaintStatus.OPEN, response.getStatus());
        assertEquals("A-101", response.getFlatNumber());
        assertEquals("Plumbing", response.getCategory());

        verify(kafkaTemplate, times(1))
                .send(eq("complaint-created"), any());
    }

    @Test
    @DisplayName(
            "US-011: Given an unknown resident id, when raising a complaint, then a "
                    + "ComplaintException is thrown")
    void raiseComplaint_UnknownResident_ThrowsException() {

        // Given
        when(userClient.getUserById(999L)).thenReturn(null);

        // When & Then
        assertThrows(
                ComplaintException.class,
                () -> complaintService.raiseComplaint(999L, 100L, requestDTO));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName(
            "US-011: Given the User Service is unreachable, when raising a complaint, "
                    + "then it is rejected gracefully")
    void raiseComplaint_UserServiceUnavailable_ThrowsException() {

        // Given
        when(userClient.getUserById(100L))
                .thenThrow(new RuntimeException("feign timeout"));

        // When & Then
        assertThrows(
                ComplaintException.class,
                () -> complaintService.raiseComplaint(100L, 10L, requestDTO));
    }

    @Test
    @DisplayName(
            "US-011: Given I view my complaints, when fetched, then status and remarks "
                    + "are visible")
    void getComplaintsByUser_ReturnsUsersComplaints() {

        // Given
        Complaint c = buildComplaint(1L, ComplaintStatus.OPEN, null);

        when(repository.findByUserId(100L))
                .thenReturn(List.of(c));

        // When
        List<ComplaintResponseDTO> results =
                complaintService.getComplaintsByUser(100L);

        // Then
        assertEquals(1, results.size());
        assertEquals(
                ComplaintStatus.OPEN,
                results.get(0).getStatus());
    }

    // ==========================================
    // US-012: MANAGER RESOLVES A COMPLAINT
    // ==========================================

    @Test
    @DisplayName(
            "US-012: Given open complaints for my apartment, when viewed, then they "
                    + "are sorted by raised date")
    void getComplaintsByApartment_ReturnsSortedByRaisedDateDesc() {

        // Given
        Complaint c = buildComplaint(1L, ComplaintStatus.OPEN, null);

        when(repository.findByApartmentIdOrderByRaisedAtDesc(10L))
                .thenReturn(List.of(c));

        // When
        List<ComplaintResponseDTO> results =
                complaintService.getComplaintsByApartment(10L);

        // Then
        assertEquals(1, results.size());

        verify(repository, times(1))
                .findByApartmentIdOrderByRaisedAtDesc(10L);
    }

    @Test
    @DisplayName(
            "US-012: Given a complaint, when its status is updated to RESOLVED with "
                    + "a remark, then both are stored and visible")
    void updateComplaint_ResolveWithRemark_SetsResolvedAtAndRemark() {

        // Given
        Complaint existing =
                buildComplaint(1L, ComplaintStatus.IN_PROGRESS, null);

        ComplaintUpdateDTO update =
                new ComplaintUpdateDTO(
                        ComplaintStatus.RESOLVED,
                        "Plumber fixed the tap.");

        when(repository.findById(1L))
                .thenReturn(Optional.of(existing));

        when(repository.save(any(Complaint.class)))
                .thenAnswer(i -> i.getArgument(0));

        // When
        ComplaintResponseDTO response =
                complaintService.updateComplaint(1L, update);

        // Then
        assertEquals(
                ComplaintStatus.RESOLVED,
                response.getStatus());

        assertEquals(
                "Plumber fixed the tap.",
                response.getResolutionRemark());

        assertNotNull(response.getResolvedAt());

        verify(kafkaTemplate, times(1))
                .send(eq("complaint-updated"), any());
    }

    @Test
    @DisplayName(
            "US-012: Given a complaint update for a non-existent complaint, when "
                    + "applied, then a ComplaintException is thrown")
    void updateComplaint_MissingComplaint_ThrowsException() {

        // Given
        when(repository.findById(404L))
                .thenReturn(Optional.empty());

        ComplaintUpdateDTO update =
                new ComplaintUpdateDTO(
                        ComplaintStatus.IN_PROGRESS,
                        null);

        // When & Then
        assertThrows(
                ComplaintException.class,
                () -> complaintService.updateComplaint(404L, update));
    }

    /**
     * Helper method to construct a test Complaint instance.
     */
    private Complaint buildComplaint(
            Long id,
            ComplaintStatus status,
            String remark) {

        Complaint c = new Complaint();

        c.setComplaintId(id);
        c.setApartmentId(10L);
        c.setUserId(100L);
        c.setFlatNumber("A-101");
        c.setCategory("Plumbing");
        c.setDescription("Kitchen tap is leaking");
        c.setStatus(status);
        c.setResolutionRemark(remark);
        c.setRaisedAt(LocalDateTime.now());

        return c;
    }
}
