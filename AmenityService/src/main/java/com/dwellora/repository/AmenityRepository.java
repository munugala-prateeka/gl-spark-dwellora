package com.dwellora.repository;

import com.dwellora.entity.Amenity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** JPA repository interface for managing Amenity persistence. */
public interface AmenityRepository extends JpaRepository<Amenity, Long> {

    /** Finds all amenities for a given apartment ID. */
    List<Amenity> findByApartmentId(Long apartmentId);

    /** Checks if an amenity with the given name exists in an apartment. */
    boolean existsByApartmentIdAndAmenityName(Long apartmentId, String amenityName);
}