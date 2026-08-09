package com.dwellora.repository;

import com.dwellora.entity.Rsvp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repository interface for executing database operations on Rsvp entities. */
@Repository
public interface RsvpRepository extends JpaRepository<Rsvp, Long> {

    /** Checks whether an RSVP exists for a specific event and resident. */
    boolean existsByEventIdAndResidentId(Long eventId, Long residentId);

    /** Finds an RSVP record by event ID and resident ID. */
    Optional<Rsvp> findByEventIdAndResidentId(Long eventId, Long residentId);

    /** Retrieves all RSVPs submitted by a specific resident. */
    List<Rsvp> findByResidentId(Long residentId);
}