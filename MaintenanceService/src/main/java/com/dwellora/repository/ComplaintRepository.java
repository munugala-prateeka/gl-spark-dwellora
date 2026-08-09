package com.dwellora.repository;

import com.dwellora.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Repository interface for executing database operations on Complaint entities. */
@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    /** Retrieves all complaints submitted by a specific user. */
    List<Complaint> findByUserId(Long userId);

    /** Retrieves all complaints for an apartment ordered by creation time in descending order. */
    List<Complaint> findByApartmentIdOrderByRaisedAtDesc(Long apartmentId);
}