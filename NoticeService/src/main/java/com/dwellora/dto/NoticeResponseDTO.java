package com.dwellora.dto;

import java.time.LocalDateTime;

/** Data transfer object containing notice details in response payloads. */
public class NoticeResponseDTO {

    private Long noticeId;
    private Long apartmentId;
    private String title;
    private String body;
    private Boolean isUrgent;
    private LocalDateTime publishedAt;
    private LocalDateTime expiresAt;

    public NoticeResponseDTO() {}

    public NoticeResponseDTO(
            Long noticeId,
            Long apartmentId,
            String title,
            String body,
            Boolean isUrgent,
            LocalDateTime publishedAt,
            LocalDateTime expiresAt) {
        this.noticeId = noticeId;
        this.apartmentId = apartmentId;
        this.title = title;
        this.body = body;
        this.isUrgent = isUrgent;
        this.publishedAt = publishedAt;
        this.expiresAt = expiresAt;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public Long getApartmentId() {
        return apartmentId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Boolean getIsUrgent() {
        return isUrgent;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}