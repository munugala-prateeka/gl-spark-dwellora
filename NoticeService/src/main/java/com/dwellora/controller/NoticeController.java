package com.dwellora.controller;

import com.dwellora.dto.NoticeRequestDTO;
import com.dwellora.dto.NoticeResponseDTO;
import com.dwellora.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping
    public ResponseEntity<NoticeResponseDTO> publishNotice(@Valid @RequestBody NoticeRequestDTO request) {
        NoticeResponseDTO created = noticeService.publishNotice(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/apartment/{apartmentId}")
    public ResponseEntity<List<NoticeResponseDTO>> getActiveNotices(@PathVariable Integer apartmentId) {
        return ResponseEntity.ok(noticeService.getActiveNotices(apartmentId));
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<NoticeResponseDTO> getNoticeById(@PathVariable Integer noticeId) {
        return ResponseEntity.ok(noticeService.getNoticeById(noticeId));
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Integer noticeId) {
        noticeService.deleteNotice(noticeId);
        return ResponseEntity.noContent().build();
    }
}