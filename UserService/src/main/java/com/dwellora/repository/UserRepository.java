package com.dwellora.repository;

import com.dwellora.entity.User;
import com.dwellora.enums.AccountStatus;
import com.dwellora.enums.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndAccountStatusNot(String email, AccountStatus accountStatus);

    boolean existsByApartmentIdAndRole(Integer apartmentId, Role role);

    List<User> findByApartmentId(Integer apartmentId);

    List<User> findByApartmentIdAndRole(Integer apartmentId, Role role);

    List<User> findByRole(Role role);

    Optional<User> findByActivationToken(String activationToken);
}