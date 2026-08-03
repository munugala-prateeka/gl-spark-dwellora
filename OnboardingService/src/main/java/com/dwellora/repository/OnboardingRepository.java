package com.dwellora.repository;

import com.dwellora.entity.OnboardingRequest;
import com.dwellora.enums.OnboardingStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OnboardingRepository extends JpaRepository<OnboardingRequest, Integer> {

    List<OnboardingRequest> findByStatus(OnboardingStatus status);
}