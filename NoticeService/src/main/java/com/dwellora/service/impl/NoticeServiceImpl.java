package com.dwellora.service.impl;

import com.dwellora.dto.NoticeRequestDTO;
import com.dwellora.dto.NoticeResponseDTO;
import com.dwellora.entity.Notice;
import com.dwellora.event.NoticePublishedEvent;
import com.dwellora.exception.NoticeException;
import com.dwellora.repository.NoticeRepository;
import com.dwellora.service.NoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {

    private static final Logger logger = LoggerFactory.getLogger(NoticeServiceImpl.class);

    private final NoticeRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public NoticeServiceImpl(NoticeRepository repository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public NoticeResponseDTO publishNotice(NoticeRequestDTO request) {
        Notice notice = new Notice();
        notice.setApartmentId(request.getApartmentId());
        notice.setTitle(request.getTitle());
        notice.setBody(request.getBody());
        notice.setIsUrgent(request.getIsUrgent() != null ? request.getIsUrgent() : false);
        notice.setPublishedAt(LocalDateTime.now());
        notice.setExpiresAt(request.getExpiresAt());

        Notice saved = repository.save(notice);

        try {
            NoticePublishedEvent event = new NoticePublishedEvent(
                    saved.getApartmentId(),
                    saved.getTitle(),
                    saved.getBody(),
                    saved.getIsUrgent()
            );
            kafkaTemplate.send("notice-published", event);
            logger.info("Published notice-published event for noticeId: {}", saved.getNoticeId());
        } catch (Exception e) {
            logger.error("Failed to publish notice event to Kafka: {}", e.getMessage());
        }

        return mapToResponse(saved);
    }

    @Override
    public List<NoticeResponseDTO> getActiveNotices(Integer apartmentId) {
        LocalDateTime now = LocalDateTime.now();
        return repository.findByApartmentIdAndExpiresAtAfterOrApartmentIdAndExpiresAtIsNullOrderByPublishedAtDesc(
                        apartmentId, now, apartmentId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public NoticeResponseDTO getNoticeById(Integer noticeId) {
        Notice notice = repository.findById(noticeId)
                .orElseThrow(() -> new NoticeException("Notice not found with ID: " + noticeId));
        return mapToResponse(notice);
    }

    @Override
    public void deleteNotice(Integer noticeId) {
        if (!repository.existsById(noticeId)) {
            throw new NoticeException("Notice not found with ID: " + noticeId);
        }
        repository.deleteById(noticeId);
    }

    private NoticeResponseDTO mapToResponse(Notice n) {
        return new NoticeResponseDTO(
                n.getNoticeId(), n.getApartmentId(), n.getTitle(), n.getBody(),
                n.getIsUrgent(), n.getPublishedAt(), n.getExpiresAt());
    }
}