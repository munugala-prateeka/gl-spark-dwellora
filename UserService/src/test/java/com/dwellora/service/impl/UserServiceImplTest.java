package com.dwellora.service.impl;

import com.dwellora.client.ApartmentClient;
import com.dwellora.dto.ActivateAccountDTO;
import com.dwellora.dto.LoginRequestDTO;
import com.dwellora.dto.LoginResponseDTO;
import com.dwellora.dto.ResidentRequestDTO;
import com.dwellora.dto.UserResponseDTO;
import com.dwellora.dto.UserUpdateRequestDTO;
import com.dwellora.entity.User;
import com.dwellora.enums.AccountStatus;
import com.dwellora.enums.Role;
import com.dwellora.exception.UserException;
import com.dwellora.kafka.ResidentProducer;
import com.dwellora.repository.UserRepository;
import com.dwellora.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserServiceImpl}.
 *
 * <p>Covers:
 * <ul>
 *   <li>US-001 - User Authentication &amp; Role-Based Login</li>
 *   <li>US-004 - Manager Account Activation</li>
 *   <li>US-005 - Manager Adds a Resident</li>
 *   <li>US-006 - Resident Account Activation</li>
 *   <li>US-014 - Get All Users</li>
 *
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApartmentClient apartmentClient;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ResidentProducer residentProducer;

    @InjectMocks
    private UserServiceImpl userService;

    private User activeManager;

    @BeforeEach
    void setUp() {
        activeManager = new User();
        activeManager.setUserId(1);
        activeManager.setApartmentId(10);
        activeManager.setFullName("Jane Manager");
        activeManager.setEmail("jane@dwellora.com");
        activeManager.setPassword("encoded-pass");
        activeManager.setPhone("9876543210");
        activeManager.setFlatNumber("Office");
        activeManager.setRole(Role.MANAGER);
        activeManager.setAccountStatus(AccountStatus.ACTIVE);
    }

    // ==========================================
    // US-001: LOGIN
    // ==========================================

    @Test
    @DisplayName("US-001: Given valid credentials, when I log in, then I receive a token and role")
    void login_ValidActiveCredentials_ReturnsTokenAndRole() {
        // Given
        LoginRequestDTO request = new LoginRequestDTO("jane@dwellora.com", "raw-password");
        when(userRepository.findByEmail("jane@dwellora.com")).thenReturn(Optional.of(activeManager));
        when(passwordEncoder.matches("raw-password", "encoded-pass")).thenReturn(true);
        when(jwtUtil.generateToken(1, "jane@dwellora.com", "MANAGER")).thenReturn("jwt-token");

        // When
        LoginResponseDTO response = userService.login(request);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("MANAGER", response.getRole());
        assertEquals(10, response.getApartmentId());
    }

    @Test
    @DisplayName("US-001: Given invalid password, when I log in, then a UserException is thrown")
    void login_InvalidPassword_ThrowsException() {
        // Given
        LoginRequestDTO request = new LoginRequestDTO("jane@dwellora.com", "wrong-password");
        when(userRepository.findByEmail("jane@dwellora.com")).thenReturn(Optional.of(activeManager));
        when(passwordEncoder.matches("wrong-password", "encoded-pass")).thenReturn(false);

        // When & Then
        UserException ex = assertThrows(UserException.class, () -> userService.login(request));
        assertEquals("Invalid email or password.", ex.getMessage());
        verify(jwtUtil, never()).generateToken(any(), anyString(), anyString());
    }

    @Test
    @DisplayName("US-001: Given an unactivated (PENDING_ACTIVATION) account, when I log in, then it is rejected")
    void login_InactiveAccount_ThrowsException() {
        // Given
        activeManager.setAccountStatus(AccountStatus.PENDING_ACTIVATION);
        LoginRequestDTO request = new LoginRequestDTO("jane@dwellora.com", "raw-password");
        when(userRepository.findByEmail("jane@dwellora.com")).thenReturn(Optional.of(activeManager));

        // When & Then
        UserException ex = assertThrows(UserException.class, () -> userService.login(request));
        assertEquals("Account is inactive.", ex.getMessage());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("US-001: Given an unknown email, when I log in, then a UserException is thrown")
    void login_UnknownEmail_ThrowsException() {
        // Given
        when(userRepository.findByEmail("ghost@dwellora.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserException.class,
                () -> userService.login(new LoginRequestDTO("ghost@dwellora.com", "x")));
    }

    // ==========================================
    // US-006 / US-004: ACCOUNT ACTIVATION
    // ==========================================

    @Test
    @DisplayName("US-006: Given a valid, unexpired token, when I activate, then status becomes ACTIVE")
    void activateAccount_ValidToken_ActivatesAccount() {
        // Given
        User pending = new User();
        pending.setUserId(2);
        pending.setApartmentId(10);
        pending.setFullName("New Resident");
        pending.setEmail("resident@dwellora.com");
        pending.setRole(Role.RESIDENT);
        pending.setAccountStatus(AccountStatus.PENDING_ACTIVATION);
        pending.setActivationToken("valid-token");
        pending.setActivationTokenExpiry(LocalDateTime.now().plusHours(1));

        ActivateAccountDTO dto = new ActivateAccountDTO();
        dto.setToken("valid-token");
        dto.setNewPassword("newPass123");

        when(userRepository.findByActivationToken("valid-token")).thenReturn(Optional.of(pending));
        when(passwordEncoder.encode("newPass123")).thenReturn("encoded-new-pass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // When
        LoginResponseDTO response = userService.activateAccount(dto);

        // Then
        assertEquals(AccountStatus.ACTIVE, pending.getAccountStatus());
        assertEquals("encoded-new-pass", pending.getPassword());
        assertEquals("RESIDENT", response.getRole());
        assertEquals("resident@dwellora.com", response.getEmail());
    }

    @Test
    @DisplayName("US-006: Given an expired token, when I activate, then the attempt is rejected")
    void activateAccount_ExpiredToken_ThrowsException() {
        // Given
        User pending = new User();
        pending.setAccountStatus(AccountStatus.PENDING_ACTIVATION);
        pending.setActivationToken("expired-token");
        pending.setActivationTokenExpiry(LocalDateTime.now().minusMinutes(5));

        ActivateAccountDTO dto = new ActivateAccountDTO();
        dto.setToken("expired-token");
        dto.setNewPassword("newPass123");

        when(userRepository.findByActivationToken("expired-token")).thenReturn(Optional.of(pending));

        // When & Then
        UserException ex = assertThrows(UserException.class, () -> userService.activateAccount(dto));
        assertEquals("Activation token has expired.", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("US-006: Given an already-used token, when I activate, then the attempt is rejected")
    void activateAccount_AlreadyActivated_ThrowsException() {
        // Given
        User already = new User();
        already.setAccountStatus(AccountStatus.ACTIVE);
        already.setActivationToken("used-token");

        ActivateAccountDTO dto = new ActivateAccountDTO();
        dto.setToken("used-token");
        dto.setNewPassword("newPass123");

        when(userRepository.findByActivationToken("used-token")).thenReturn(Optional.of(already));

        // When & Then
        assertThrows(UserException.class, () -> userService.activateAccount(dto));
    }

    @Test
    @DisplayName("US-006: Given an invalid token, when I activate, then a UserException is thrown")
    void activateAccount_InvalidToken_ThrowsException() {
        // Given
        when(userRepository.findByActivationToken("bogus")).thenReturn(Optional.empty());
        ActivateAccountDTO dto = new ActivateAccountDTO();
        dto.setToken("bogus");
        dto.setNewPassword("pw");

        // When & Then
        assertThrows(UserException.class, () -> userService.activateAccount(dto));
    }

    // ==========================================
    // US-005: MANAGER ADDS A RESIDENT
    // ==========================================

    @Test
    @DisplayName("US-005: Given valid resident details, when added, then a PENDING_ACTIVATION resident is created and an activation event is published")
    void createResident_ValidRequest_CreatesPendingResidentAndPublishesEvent() {
        // Given
        ResidentRequestDTO request = new ResidentRequestDTO();
        request.setApartmentId(10);
        request.setFullName("New Resident");
        request.setEmail("resident2@dwellora.com");
        request.setPhone("9998887776");
        request.setFlatNumber("A-101");

        when(apartmentClient.getApartmentById(10)).thenReturn(new Object());
        when(userRepository.findByEmailAndAccountStatusNot("resident2@dwellora.com", AccountStatus.INACTIVE))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setUserId(5);
            return u;
        });

        // When
        UserResponseDTO response = userService.createResident(request);

        // Then
        assertEquals(AccountStatus.PENDING_ACTIVATION, response.getAccountStatus());
        assertEquals(Role.RESIDENT, response.getRole());
        assertEquals("A-101", response.getFlatNumber());
        verify(residentProducer, times(1)).publish(any());
    }

    @Test
    @DisplayName("US-005: Given a duplicate email, when adding a resident, then a UserException is thrown")
    void createResident_DuplicateEmail_ThrowsException() {
        // Given
        ResidentRequestDTO request = new ResidentRequestDTO();
        request.setApartmentId(10);
        request.setEmail("dup@dwellora.com");
        request.setFullName("Dup Resident");
        request.setPhone("1234567890");
        request.setFlatNumber("B-202");

        when(apartmentClient.getApartmentById(10)).thenReturn(new Object());
        when(userRepository.findByEmailAndAccountStatusNot("dup@dwellora.com", AccountStatus.INACTIVE))
                .thenReturn(Optional.of(activeManager));

        // When & Then
        assertThrows(UserException.class, () -> userService.createResident(request));
        verify(userRepository, never()).save(any());
        verify(residentProducer, never()).publish(any());
    }

    @Test
    @DisplayName("US-005: Given an update to a resident's flat number, when saved, then the change is persisted")
    void updateResident_ValidRequest_UpdatesAndReturnsResident() {
        // Given
        User existing = new User();
        existing.setUserId(5);
        existing.setApartmentId(10);
        existing.setRole(Role.RESIDENT);
        existing.setAccountStatus(AccountStatus.ACTIVE);
        existing.setFullName("Old Name");
        existing.setEmail("old@dwellora.com");
        existing.setPhone("1112223333");
        existing.setFlatNumber("A-101");

        UserUpdateRequestDTO update = new UserUpdateRequestDTO();
        update.setApartmentId(10);
        update.setFullName("Updated Name");
        update.setEmail("old@dwellora.com");
        update.setPhone("1112223333");
        update.setFlatNumber("A-102");

        when(userRepository.findById(5)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // When
        UserResponseDTO response = userService.updateResident(5, update);

        // Then
        assertEquals("A-102", response.getFlatNumber());
        assertEquals("Updated Name", response.getFullName());
    }

    @Test
    @DisplayName("US-005: Given a resident I no longer want, when removed, then their account is deactivated (soft delete)")
    void deleteResident_ExistingResident_SoftDeletes() {
        // Given
        User existing = new User();
        existing.setUserId(5);
        existing.setRole(Role.RESIDENT);
        existing.setAccountStatus(AccountStatus.ACTIVE);

        when(userRepository.findById(5)).thenReturn(Optional.of(existing));

        // When
        userService.deleteResident(5);

        // Then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals(AccountStatus.INACTIVE, captor.getValue().getAccountStatus());
    }

    @Test
    @DisplayName("US-005: Given a manager account, when deletion is attempted, then it is rejected")
    void deleteResident_ManagerAccount_ThrowsException() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.of(activeManager));

        // When & Then
        UserException ex = assertThrows(UserException.class, () -> userService.deleteResident(1));
        assertEquals("Manager cannot be deleted.", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("US-005: Given a missing user id, when updated, then a UserException is thrown")
    void updateResident_NotFound_ThrowsException() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());
        UserUpdateRequestDTO update = new UserUpdateRequestDTO();
        assertThrows(UserException.class, () -> userService.updateResident(999, update));
    }

    // ==========================================
    // US-014: GET ALL USERS
    // ==========================================

    @Test
    @DisplayName("US-014: Should return all users across the platform")
    void getAllUsers_ReturnsFullList() {
        // Given
        when(userRepository.findAll()).thenReturn(List.of(activeManager));

        // When
        List<UserResponseDTO> results = userService.getAllUsers();

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(Role.MANAGER, results.get(0).getRole());
    }
}
