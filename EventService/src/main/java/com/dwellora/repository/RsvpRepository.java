package com.dwellora.repository;

import com.dwellora.entity.Rsvp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RsvpRepository extends JpaRepository<Rsvp, Integer> {
    boolean existsByEventIdAndResidentId(Integer eventId, Integer residentId);
    Optional<Rsvp> findByEventIdAndResidentId(Integer eventId, Integer residentId);
    List<Rsvp> findByResidentId(Integer residentId);
}