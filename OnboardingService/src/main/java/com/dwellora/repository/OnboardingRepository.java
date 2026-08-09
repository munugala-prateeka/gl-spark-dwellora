package com.dwellora.repository;

import com.dwellora.entity.OnboardingRequest;
import com.dwellora.enums.OnboardingStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link OnboardingRequest} entities.
 */
public interface OnboardingRepository extends JpaRepository<OnboardingRequest, Long> {
    List<OnboardingRequest> findByStatus(OnboardingStatus status);
}