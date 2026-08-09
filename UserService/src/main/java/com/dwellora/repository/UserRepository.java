package com.dwellora.repository;

import com.dwellora.entity.User;
import com.dwellora.enums.AccountStatus;
import com.dwellora.enums.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link User} entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Checks if a user exists with the given email address.
     */
    boolean existsByEmail(String email);

    /**
     * Finds a user by email address.
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by email address excluding a specific account status.
     */
    Optional<User> findByEmailAndAccountStatusNot(String email, AccountStatus accountStatus);

    /**
     * Checks if a user exists for a given apartment ID and role.
     */
    boolean existsByApartmentIdAndRole(Long apartmentId, Role role);

    /**
     * Finds all users associated with a specific apartment ID.
     */
    List<User> findByApartmentId(Long apartmentId);

    /**
     * Finds all users associated with a specific apartment ID and role.
     */
    List<User> findByApartmentIdAndRole(Long apartmentId, Role role);

    /**
     * Finds all users with a specific role.
     */
    List<User> findByRole(Role role);

    /**
     * Finds a user by activation token.
     */
    Optional<User> findByActivationToken(String activationToken);
}