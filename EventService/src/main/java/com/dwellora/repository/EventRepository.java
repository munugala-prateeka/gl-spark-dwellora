package com.dwellora.repository;

import com.dwellora.entity.Event;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository interface for executing database operations on Event entities. */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /** Retrieves upcoming events for a specific apartment after the given timestamp. */
    List<Event> findByApartmentIdAndEventDateAfterOrderByEventDateAsc(
            Long apartmentId, LocalDateTime now);
}