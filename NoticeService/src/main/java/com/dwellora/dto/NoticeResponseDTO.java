package com.dwellora.dto;

import java.time.LocalDateTime;

public class NoticeResponseDTO {

    private Integer noticeId;
    private Integer apartmentId;
    private String title;
    private String body;
    private Boolean isUrgent;
    private LocalDateTime publishedAt;
    private LocalDateTime expiresAt;

    public NoticeResponseDTO() {}

    public NoticeResponseDTO(Integer noticeId, Integer apartmentId, String title, String body,
                             Boolean isUrgent, LocalDateTime publishedAt, LocalDateTime expiresAt) {
        this.noticeId = noticeId;
        this.apartmentId = apartmentId;
        this.title = title;
        this.body = body;
        this.isUrgent = isUrgent;
        this.publishedAt = publishedAt;
        this.expiresAt = expiresAt;
    }

    public Integer getNoticeId() { return noticeId; }
    public Integer getApartmentId() { return apartmentId; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public Boolean getIsUrgent() { return isUrgent; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
}