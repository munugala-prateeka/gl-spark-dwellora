package com.dwellora.repository;

import com.dwellora.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {

    List<Notice> findByApartmentIdAndExpiresAtAfterOrApartmentIdAndExpiresAtIsNullOrderByPublishedAtDesc(
            Integer apartmentId1, LocalDateTime now, Integer apartmentId2);
}