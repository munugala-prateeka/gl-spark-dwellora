package com.dwellora.service.impl;

import com.dwellora.dto.NoticeRequestDTO;
import com.dwellora.dto.NoticeResponseDTO;
import com.dwellora.entity.Notice;
import com.dwellora.exception.NoticeException;
import com.dwellora.repository.NoticeRepository;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link NoticeServiceImpl}.
 *
 * <p>Covers US-015 - Manager Publishes a Notice.
 */
@ExtendWith(MockitoExtension.class)
class NoticeServiceImplTest {

    @Mock
    private NoticeRepository repository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private NoticeServiceImpl noticeService;

    @Test
    @DisplayName("US-015 (AC-1): Given a title and body, when published, then it becomes visible to the apartment and a notice-published event fires")
    void publishNotice_ValidRequest_PublishesAndSendsEvent() {
        // Given
        NoticeRequestDTO request = new NoticeRequestDTO();
        request.setApartmentId(10);
        request.setTitle("Water Shutdown");
        request.setBody("Water will be shut off from 10am-2pm for maintenance.");
        request.setIsUrgent(false);

        when(repository.save(any(Notice.class))).thenAnswer(i -> {
            Notice n = i.getArgument(0);
            n.setNoticeId(1);
            return n;
        });

        // When
        NoticeResponseDTO response = noticeService.publishNotice(request);

        // Then
        assertNotNull(response);
        assertEquals("Water Shutdown", response.getTitle());
        assertFalse(response.getIsUrgent());
        assertNotNull(response.getPublishedAt());
        verify(kafkaTemplate, times(1)).send(eq("notice-published"), any());
    }

    @Test
    @DisplayName("US-015 (AC-2): Given a notice marked urgent, when published, then isUrgent is preserved so it can be visually distinguished")
    void publishNotice_MarkedUrgent_PreservesUrgentFlag() {
        // Given
        NoticeRequestDTO request = new NoticeRequestDTO();
        request.setApartmentId(10);
        request.setTitle("Fire drill NOW");
        request.setBody("Evacuate immediately.");
        request.setIsUrgent(true);

        when(repository.save(any(Notice.class))).thenAnswer(i -> i.getArgument(0));

        // When
        NoticeResponseDTO response = noticeService.publishNotice(request);

        // Then
        assertTrue(response.getIsUrgent());
    }

    @Test
    @DisplayName("US-015 (AC-3): Given active and expired notices, when the board is viewed, then only unexpired notices are returned")
    void getActiveNotices_ExcludesExpiredNotices() {
        // Given
        Notice active = new Notice();
        active.setNoticeId(1);
        active.setApartmentId(10);
        active.setTitle("Still relevant");
        active.setIsUrgent(false);
        active.setPublishedAt(LocalDateTime.now().minusDays(1));
        active.setExpiresAt(LocalDateTime.now().plusDays(5));

        when(repository.findByApartmentIdAndExpiresAtAfterOrApartmentIdAndExpiresAtIsNullOrderByPublishedAtDesc(
                eq(10), any(LocalDateTime.class), eq(10)))
                .thenReturn(List.of(active));

        // When
        List<NoticeResponseDTO> results = noticeService.getActiveNotices(10);

        // Then
        assertEquals(1, results.size());
        assertEquals("Still relevant", results.get(0).getTitle());
    }

    @Test
    @DisplayName("Given a notice id, when fetched, then its details are returned")
    void getNoticeById_ExistingId_ReturnsNotice() {
        // Given
        Notice notice = new Notice();
        notice.setNoticeId(5);
        notice.setApartmentId(10);
        notice.setTitle("Elevator maintenance");
        notice.setIsUrgent(false);
        notice.setPublishedAt(LocalDateTime.now());

        when(repository.findById(5)).thenReturn(Optional.of(notice));

        // When
        NoticeResponseDTO response = noticeService.getNoticeById(5);

        // Then
        assertEquals("Elevator maintenance", response.getTitle());
    }

    @Test
    @DisplayName("Given a non-existent notice, when fetched, then a NoticeException is thrown")
    void getNoticeById_MissingId_ThrowsException() {
        // Given
        when(repository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NoticeException.class, () -> noticeService.getNoticeById(999));
    }

    @Test
    @DisplayName("Given a non-existent notice, when deletion is attempted, then a NoticeException is thrown")
    void deleteNotice_MissingId_ThrowsException() {
        // Given
        when(repository.existsById(999)).thenReturn(false);

        // When & Then
        assertThrows(NoticeException.class, () -> noticeService.deleteNotice(999));
        verify(repository, never()).deleteById(any());
    }
}
