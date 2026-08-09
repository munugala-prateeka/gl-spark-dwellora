package com.dwellora.service.impl;

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

import com.dwellora.dto.NoticeRequestDTO;
import com.dwellora.dto.NoticeResponseDTO;
import com.dwellora.entity.Notice;
import com.dwellora.exception.NoticeException;
import com.dwellora.repository.NoticeRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Unit tests for {@link NoticeServiceImpl}. Covers publishing notices, fetching active board items,
 * authorization checks, and deletion logic.
 */
@ExtendWith(MockitoExtension.class)
class NoticeServiceImplTest {

    @Mock private NoticeRepository repository;

    @Mock private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks private NoticeServiceImpl noticeService;

    // ==========================================
    // US-015: MANAGER PUBLISHES A NOTICE
    // ==========================================

    /** Tests publishing a valid notice and verifying event emission to Kafka. */
    @Test
    @DisplayName(
            "US-015 (AC-1): Given a title and body, when published, then it becomes visible to the"
                    + " apartment and a notice-published event fires")
    void publishNotice_ValidRequest_PublishesAndSendsEvent() {
        NoticeRequestDTO request = new NoticeRequestDTO();
        request.setTitle("Water Shutdown");
        request.setBody("Water will be shut off from 10am-2pm for maintenance.");
        request.setIsUrgent(false);

        when(repository.save(any(Notice.class)))
                .thenAnswer(
                        i -> {
                            Notice n = i.getArgument(0);
                            n.setNoticeId(1L);
                            return n;
                        });

        NoticeResponseDTO response = noticeService.publishNotice(10L, request);

        assertNotNull(response);
        assertEquals("Water Shutdown", response.getTitle());
        assertFalse(response.getIsUrgent());
        assertNotNull(response.getPublishedAt());
        verify(kafkaTemplate, times(1)).send(eq("notice-published"), any());
    }

    /** Tests publishing an urgent notice to verify the urgency flag is accurately preserved. */
    @Test
    @DisplayName(
            "US-015 (AC-2): Given a notice marked urgent, when published, then isUrgent is preserved so"
                    + " it can be visually distinguished")
    void publishNotice_MarkedUrgent_PreservesUrgentFlag() {
        NoticeRequestDTO request = new NoticeRequestDTO();
        request.setTitle("Fire drill NOW");
        request.setBody("Evacuate immediately.");
        request.setIsUrgent(true);

        when(repository.save(any(Notice.class))).thenAnswer(i -> i.getArgument(0));

        NoticeResponseDTO response = noticeService.publishNotice(10L, request);

        assertTrue(response.getIsUrgent());
    }

    /** Tests retrieving active notices while ensuring expired records are excluded. */
    @Test
    @DisplayName(
            "US-015 (AC-3): Given active and expired notices, when the board is viewed, then only"
                    + " unexpired notices are returned")
    void getActiveNotices_ExcludesExpiredNotices() {
        Notice active = new Notice();
        active.setNoticeId(1L);
        active.setApartmentId(10L);
        active.setTitle("Still relevant");
        active.setIsUrgent(false);
        active.setPublishedAt(LocalDateTime.now().minusDays(1));
        active.setExpiresAt(LocalDateTime.now().plusDays(5));

        when(repository
                .findByApartmentIdAndExpiresAtAfterOrApartmentIdAndExpiresAtIsNullOrderByPublishedAtDesc(
                        eq(10L), any(LocalDateTime.class), eq(10L)))
                .thenReturn(List.of(active));

        List<NoticeResponseDTO> results = noticeService.getActiveNotices(10L);

        assertEquals(1, results.size());
        assertEquals("Still relevant", results.get(0).getTitle());
    }

    /** Tests successfully retrieving details for an existing notice ID. */
    @Test
    @DisplayName("Given a notice id, when fetched, then its details are returned")
    void getNoticeById_ExistingId_ReturnsNotice() {
        Notice notice = new Notice();
        notice.setNoticeId(5L);
        notice.setApartmentId(10L);
        notice.setTitle("Elevator maintenance");
        notice.setIsUrgent(false);
        notice.setPublishedAt(LocalDateTime.now());

        when(repository.findById(5L)).thenReturn(Optional.of(notice));

        NoticeResponseDTO response = noticeService.getNoticeById(5L, 10L);

        assertEquals("Elevator maintenance", response.getTitle());
    }

    /** Tests throwing NoticeException when attempting to access a non-existent notice ID. */
    @Test
    @DisplayName("Given a non-existent notice, when fetched, then a NoticeException is thrown")
    void getNoticeById_MissingId_ThrowsException() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoticeException.class, () -> noticeService.getNoticeById(999L, 10L));
    }

    /** Tests throwing NoticeException when attempting to delete a non-existent notice. */
    @Test
    @DisplayName(
            "Given a non-existent notice, when deletion is attempted, then a NoticeException is thrown")
    void deleteNotice_MissingId_ThrowsException() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoticeException.class, () -> noticeService.deleteNotice(999L, 10L));
        verify(repository, never()).delete(any());
    }
}