package com.dwellora.controller;

import com.dwellora.dto.NoticeRequestDTO;
import com.dwellora.dto.NoticeResponseDTO;
import com.dwellora.service.NoticeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST controller for managing notice publishing, retrieval, and deletion endpoints. */
@RestController
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    /** Publishes a new notice for an apartment complex. Restricted to managers. */
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping
    public ResponseEntity<NoticeResponseDTO> publishNotice(
            @RequestHeader("X-Apartment-Id") Long apartmentId,
            @Valid @RequestBody NoticeRequestDTO request) {
        NoticeResponseDTO created = noticeService.publishNotice(apartmentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** Retrieves active notices for an apartment complex. */
    @PreAuthorize("hasAnyRole('RESIDENT', 'MANAGER')")
    @GetMapping
    public ResponseEntity<List<NoticeResponseDTO>> getActiveNotices(
            @RequestHeader("X-Apartment-Id") Long apartmentId) {
        return ResponseEntity.ok(noticeService.getActiveNotices(apartmentId));
    }

    /** Retrieves details for a specific notice by ID and apartment ID. */
    @PreAuthorize("hasAnyRole('RESIDENT', 'MANAGER')")
    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDTO> getNoticeById(
            @RequestHeader("X-Apartment-Id") Long apartmentId, @PathVariable Long noticeId) {
        return ResponseEntity.ok(noticeService.getNoticeById(noticeId, apartmentId));
    }

    /** Deletes a notice by ID for an apartment complex. Restricted to managers. */
    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(
            @RequestHeader("X-Apartment-Id") Long apartmentId, @PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId, apartmentId);
        return ResponseEntity.noContent().build();
    }
}