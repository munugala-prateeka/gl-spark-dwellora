package com.dwellora.repository;

import com.dwellora.entity.Notice;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository interface for executing database operations on Notice entities. */
@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    /** Retrieves active notices that have not expired or have no expiration date. */
    List<Notice>
    findByApartmentIdAndExpiresAtAfterOrApartmentIdAndExpiresAtIsNullOrderByPublishedAtDesc(
            Long apartmentId1, LocalDateTime now, Long apartmentId2);
}