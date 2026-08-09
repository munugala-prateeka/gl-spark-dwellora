package com.dwellora.repository;

import com.dwellora.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Apartment} entities.
 */
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {}