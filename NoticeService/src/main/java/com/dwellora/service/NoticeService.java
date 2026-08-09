package com.dwellora.service;

import com.dwellora.dto.NoticeRequestDTO;
import com.dwellora.dto.NoticeResponseDTO;
import java.util.List;

/** Service interface defining operations for notice management. */
public interface NoticeService {

    /** Publishes a new notice for an apartment. */
    NoticeResponseDTO publishNotice(Long apartmentId, NoticeRequestDTO request);

    /** Retrieves all currently active notices for an apartment. */
    List<NoticeResponseDTO> getActiveNotices(Long apartmentId);

    /** Retrieves details of a specific notice by ID for an apartment. */
    NoticeResponseDTO getNoticeById(Long noticeId, Long apartmentId);

    /** Deletes a specific notice by ID for an apartment. */
    void deleteNotice(Long noticeId, Long apartmentId);
}