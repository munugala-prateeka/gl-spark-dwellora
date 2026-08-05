package com.dwellora.repository;

import com.dwellora.entity.Amenity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity, Integer> {

    List<Amenity> findByApartmentId(Integer apartmentId);

    boolean existsByApartmentIdAndAmenityName(Integer apartmentId, String amenityName);
}