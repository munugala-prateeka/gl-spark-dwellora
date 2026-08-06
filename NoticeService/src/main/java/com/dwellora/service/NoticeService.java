package com.dwellora.service;

import com.dwellora.dto.NoticeRequestDTO;
import com.dwellora.dto.NoticeResponseDTO;

import java.util.List;

public interface NoticeService {

    NoticeResponseDTO publishNotice(NoticeRequestDTO request);

    List<NoticeResponseDTO> getActiveNotices(Integer apartmentId);

    NoticeResponseDTO getNoticeById(Integer noticeId);

    void deleteNotice(Integer noticeId);
}