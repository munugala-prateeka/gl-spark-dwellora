package com.dwellora.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Unit tests for {@link UserServiceImpl}.
 *
 * <p>Covers:
 * <ul>
 *   <li>US-001 - User Authentication & Role-Based Login</li>
 *   <li>US-004 - Manager Account Activation</li>
 *   <li>US-005 - Manager Adds a Resident</li>
 *   <li>US-006 - Resident Account Activation</li>
 *   <li>US-014 - Get All Users</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;

    @Mock private JwtUtil jwtUtil;

    @Mock private PasswordEncoder passwordEncoder;

    @Mock private ResidentProducer residentProducer;

    @InjectMocks private UserServiceImpl userService;

    private User activeManager;

    @BeforeEach
    void setUp() {

        activeManager = new User();

        activeManager.setUserId(1L);
        activeManager.setApartmentId(10L);
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
    @DisplayName(
            "US-001: Given valid credentials, when I log in, then I receive a token and role")
    void login_ValidActiveCredentials_ReturnsTokenAndRole() {

        // Given
        LoginRequestDTO request = new LoginRequestDTO("jane@dwellora.com", "raw-password");

        when(userRepository.findByEmail("jane@dwellora.com"))
                .thenReturn(Optional.of(activeManager));

        when(passwordEncoder.matches("raw-password", "encoded-pass")).thenReturn(true);

        when(jwtUtil.generateToken(1L, "jane@dwellora.com", "MANAGER", 10L))
                .thenReturn("jwt-token");

        // When
        LoginResponseDTO response = userService.login(request);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("MANAGER", response.getRole());
        assertEquals(10L, response.getApartmentId());
        assertEquals("jane@dwellora.com", response.getEmail());
        assertEquals("Jane Manager", response.getFullName());

        verify(userRepository).findByEmail("jane@dwellora.com");
        verify(passwordEncoder).matches("raw-password", "encoded-pass");

        verify(jwtUtil).generateToken(1L, "jane@dwellora.com", "MANAGER", 10L);
    }

    @Test
    @DisplayName(
            "US-001: Given email with spaces and uppercase letters, when I log in, then email is"
                    + " normalized")
    void login_EmailIsNormalized_ReturnsToken() {

        // Given
        LoginRequestDTO request = new LoginRequestDTO("  JANE@DWELLORA.COM  ", "raw-password");

        when(userRepository.findByEmail("jane@dwellora.com"))
                .thenReturn(Optional.of(activeManager));

        when(passwordEncoder.matches("raw-password", "encoded-pass")).thenReturn(true);

        when(jwtUtil.generateToken(1L, "jane@dwellora.com", "MANAGER", 10L))
                .thenReturn("jwt-token");

        // When
        LoginResponseDTO response = userService.login(request);

        // Then
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());

        verify(userRepository).findByEmail("jane@dwellora.com");
    }

    @Test
    @DisplayName("US-001: Given invalid password, when I log in, then a UserException is thrown")
    void login_InvalidPassword_ThrowsException() {

        // Given
        LoginRequestDTO request = new LoginRequestDTO("jane@dwellora.com", "wrong-password");

        when(userRepository.findByEmail("jane@dwellora.com"))
                .thenReturn(Optional.of(activeManager));

        when(passwordEncoder.matches("wrong-password", "encoded-pass")).thenReturn(false);

        // When & Then
        UserException ex =
                assertThrows(UserException.class, () -> userService.login(request));

        assertEquals("Invalid email or password.", ex.getMessage());

        verify(jwtUtil, never()).generateToken(any(), anyString(), anyString(), any());
    }

    @Test
    @DisplayName("US-001: Given an unactivated account, when I log in, then it is rejected")
    void login_InactiveAccount_ThrowsException() {

        // Given
        activeManager.setAccountStatus(AccountStatus.PENDING_ACTIVATION);

        LoginRequestDTO request = new LoginRequestDTO("jane@dwellora.com", "raw-password");

        when(userRepository.findByEmail("jane@dwellora.com"))
                .thenReturn(Optional.of(activeManager));

        // When & Then
        UserException ex =
                assertThrows(UserException.class, () -> userService.login(request));

        assertEquals("Account is inactive.", ex.getMessage());

        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("US-001: Given an unknown email, when I log in, then a UserException is thrown")
    void login_UnknownEmail_ThrowsException() {

        // Given
        when(userRepository.findByEmail("ghost@dwellora.com")).thenReturn(Optional.empty());

        // When & Then
        UserException ex =
                assertThrows(
                        UserException.class,
                        () -> userService.login(new LoginRequestDTO("ghost@dwellora.com", "x")));

        assertEquals("Invalid email or password.", ex.getMessage());

        verify(passwordEncoder, never()).matches(anyString(), anyString());

        verify(jwtUtil, never()).generateToken(any(), anyString(), anyString(), any());
    }

    // ==========================================
    // US-006 / US-004: ACCOUNT ACTIVATION
    // ==========================================

    @Test
    @DisplayName(
            "US-006: Given a valid, unexpired token, when I activate, then status becomes ACTIVE")
    void activateAccount_ValidToken_ActivatesAccount() {

        // Given
        User pending = new User();

        pending.setUserId(2L);
        pending.setApartmentId(10L);
        pending.setFullName("New Resident");
        pending.setEmail("resident@dwellora.com");
        pending.setRole(Role.RESIDENT);
        pending.setAccountStatus(AccountStatus.PENDING_ACTIVATION);
        pending.setActivationToken("valid-token");
        pending.setActivationTokenExpiry(LocalDateTime.now().plusHours(1));

        ActivateAccountDTO dto = new ActivateAccountDTO();

        dto.setToken("valid-token");
        dto.setNewPassword("newPass123");

        when(userRepository.findByActivationToken("valid-token"))
                .thenReturn(Optional.of(pending));

        when(passwordEncoder.encode("newPass123")).thenReturn("encoded-new-pass");

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // When
        LoginResponseDTO response = userService.activateAccount(dto);

        // Then
        assertEquals(AccountStatus.ACTIVE, pending.getAccountStatus());

        assertEquals("encoded-new-pass", pending.getPassword());

        assertEquals("RESIDENT", response.getRole());

        assertEquals("resident@dwellora.com", response.getEmail());

        assertEquals(null, pending.getActivationToken());

        assertEquals(null, pending.getActivationTokenExpiry());

        verify(userRepository).save(pending);
    }

    @Test
    @DisplayName(
            "US-006: Given an expired token, when I activate, then the attempt is rejected")
    void activateAccount_ExpiredToken_ThrowsException() {

        // Given
        User pending = new User();

        pending.setAccountStatus(AccountStatus.PENDING_ACTIVATION);
        pending.setActivationToken("expired-token");
        pending.setActivationTokenExpiry(LocalDateTime.now().minusMinutes(5));

        ActivateAccountDTO dto = new ActivateAccountDTO();

        dto.setToken("expired-token");
        dto.setNewPassword("newPass123");

        when(userRepository.findByActivationToken("expired-token"))
                .thenReturn(Optional.of(pending));

        // When & Then
        UserException ex =
                assertThrows(UserException.class, () -> userService.activateAccount(dto));

        assertEquals("Activation token has expired.", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName(
            "US-006: Given an already activated account, when I activate, then the attempt is rejected")
    void activateAccount_AlreadyActivated_ThrowsException() {

        // Given
        User already = new User();

        already.setAccountStatus(AccountStatus.ACTIVE);
        already.setActivationToken("used-token");

        ActivateAccountDTO dto = new ActivateAccountDTO();

        dto.setToken("used-token");
        dto.setNewPassword("newPass123");

        when(userRepository.findByActivationToken("used-token"))
                .thenReturn(Optional.of(already));

        // When & Then
        UserException ex =
                assertThrows(UserException.class, () -> userService.activateAccount(dto));

        assertEquals("Account is already activated.", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName(
            "US-006: Given an invalid token, when I activate, then a UserException is thrown")
    void activateAccount_InvalidToken_ThrowsException() {

        // Given
        when(userRepository.findByActivationToken("bogus")).thenReturn(Optional.empty());

        ActivateAccountDTO dto = new ActivateAccountDTO();

        dto.setToken("bogus");
        dto.setNewPassword("pw");

        // When & Then
        UserException ex =
                assertThrows(UserException.class, () -> userService.activateAccount(dto));

        assertEquals("Invalid activation token.", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    // ==========================================
    // US-005: MANAGER ADDS A RESIDENT
    // ==========================================

    @Test
    @DisplayName(
            "US-005: Given valid resident details, when added, then a PENDING_ACTIVATION resident is"
                    + " created and an activation event is published")
    void createResident_ValidRequest_CreatesPendingResidentAndPublishesEvent() {

        // Given
        ResidentRequestDTO request = new ResidentRequestDTO();

        request.setFullName("New Resident");
        request.setEmail("resident2@dwellora.com");
        request.setPhone("9998887776");
        request.setFlatNumber("A-101");

        when(userRepository.findByEmailAndAccountStatusNot(
                "resident2@dwellora.com", AccountStatus.INACTIVE))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenAnswer(
                        i -> {
                            User u = i.getArgument(0);
                            u.setUserId(5L);
                            return u;
                        });

        // When
        UserResponseDTO response = userService.createResident(10L, request);

        // Then
        assertNotNull(response);

        assertEquals(10L, response.getApartmentId());

        assertEquals(AccountStatus.PENDING_ACTIVATION, response.getAccountStatus());

        assertEquals(Role.RESIDENT, response.getRole());

        assertEquals("A-101", response.getFlatNumber());

        assertEquals("resident2@dwellora.com", response.getEmail());

        verify(userRepository)
                .findByEmailAndAccountStatusNot("resident2@dwellora.com", AccountStatus.INACTIVE);

        verify(userRepository).save(any(User.class));

        verify(residentProducer, times(1)).publish(any());
    }

    @Test
    @DisplayName(
            "US-005: Given email with spaces and uppercase letters, when adding resident, then email is"
                    + " normalized")
    void createResident_EmailIsNormalized_CreatesResident() {

        // Given
        ResidentRequestDTO request = new ResidentRequestDTO();

        request.setFullName("New Resident");
        request.setEmail("  RESIDENT2@DWELLORA.COM  ");
        request.setPhone("9998887776");
        request.setFlatNumber("A-101");

        when(userRepository.findByEmailAndAccountStatusNot(
                "resident2@dwellora.com", AccountStatus.INACTIVE))
                .thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenAnswer(
                        i -> {
                            User u = i.getArgument(0);
                            u.setUserId(5L);
                            return u;
                        });

        // When
        UserResponseDTO response = userService.createResident(10L, request);

        // Then
        assertEquals("resident2@dwellora.com", response.getEmail());

        verify(userRepository)
                .findByEmailAndAccountStatusNot("resident2@dwellora.com", AccountStatus.INACTIVE);
    }

    @Test
    @DisplayName(
            "US-005: Given a duplicate email, when adding a resident, then a UserException is thrown")
    void createResident_DuplicateEmail_ThrowsException() {

        // Given
        ResidentRequestDTO request = new ResidentRequestDTO();

        request.setEmail("dup@dwellora.com");
        request.setFullName("Dup Resident");
        request.setPhone("1234567890");
        request.setFlatNumber("B-202");

        when(userRepository.findByEmailAndAccountStatusNot(
                "dup@dwellora.com", AccountStatus.INACTIVE))
                .thenReturn(Optional.of(activeManager));

        // When & Then
        UserException ex =
                assertThrows(
                        UserException.class, () -> userService.createResident(10L, request));

        assertEquals("Email already exists", ex.getMessage());

        verify(userRepository, never()).save(any());

        verify(residentProducer, never()).publish(any());
    }

    // ==========================================
    // UPDATE RESIDENT
    // ==========================================

    @Test
    @DisplayName(
            "US-005: Given an update to a resident's flat number, when saved, then the change is"
                    + " persisted")
    void updateResident_ValidRequest_UpdatesAndReturnsResident() {

        // Given
        User existing = new User();

        existing.setUserId(5L);
        existing.setApartmentId(10L);
        existing.setRole(Role.RESIDENT);
        existing.setAccountStatus(AccountStatus.ACTIVE);
        existing.setFullName("Old Name");
        existing.setEmail("old@dwellora.com");
        existing.setPhone("1112223333");
        existing.setFlatNumber("A-101");

        UserUpdateRequestDTO update = new UserUpdateRequestDTO();

        update.setFullName("Updated Name");
        update.setEmail("old@dwellora.com");
        update.setPhone("1112223333");
        update.setFlatNumber("A-102");

        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // When
        UserResponseDTO response = userService.updateResident(10L, 5L, update);

        // Then
        assertEquals("A-102", response.getFlatNumber());

        assertEquals("Updated Name", response.getFullName());

        assertEquals("old@dwellora.com", response.getEmail());

        verify(userRepository).save(existing);
    }

    @Test
    @DisplayName(
            "US-005: Given an email with spaces and uppercase letters, when updating resident, then email"
                    + " is normalized")
    void updateResident_EmailIsNormalized_UpdatesResident() {

        // Given
        User existing = new User();

        existing.setUserId(5L);
        existing.setApartmentId(10L);
        existing.setRole(Role.RESIDENT);
        existing.setAccountStatus(AccountStatus.ACTIVE);
        existing.setFullName("Old Name");
        existing.setEmail("old@dwellora.com");
        existing.setPhone("1112223333");
        existing.setFlatNumber("A-101");

        UserUpdateRequestDTO update = new UserUpdateRequestDTO();

        update.setFullName("Updated Name");
        update.setEmail("  NEW@DWELLORA.COM  ");
        update.setPhone("1112223333");
        update.setFlatNumber("A-102");

        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));

        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // When
        UserResponseDTO response = userService.updateResident(10L, 5L, update);

        // Then
        assertEquals("new@dwellora.com", response.getEmail());

        verify(userRepository).save(existing);
    }

    @Test
    @DisplayName(
            "US-005: Given a resident I no longer want, when removed, then their account is deactivated")
    void deleteResident_ExistingResident_SoftDeletes() {

        // Given
        User existing = new User();

        existing.setUserId(5L);
        existing.setApartmentId(10L);
        existing.setRole(Role.RESIDENT);
        existing.setAccountStatus(AccountStatus.ACTIVE);

        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));

        // When
        userService.deleteResident(10L, 5L);

        // Then
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        assertEquals(AccountStatus.INACTIVE, captor.getValue().getAccountStatus());
    }

    @Test
    @DisplayName("US-005: Given a manager account, when deletion is attempted, then it is rejected")
    void deleteResident_ManagerAccount_ThrowsException() {

        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeManager));

        // When & Then
        UserException ex =
                assertThrows(UserException.class, () -> userService.deleteResident(10L, 1L));

        assertEquals("Manager cannot be deleted.", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName(
            "US-005: Given a resident from another apartment, when updating, then it is rejected")
    void updateResident_WrongApartment_ThrowsException() {

        // Given
        User existing = new User();

        existing.setUserId(5L);
        existing.setApartmentId(20L);
        existing.setRole(Role.RESIDENT);
        existing.setAccountStatus(AccountStatus.ACTIVE);

        UserUpdateRequestDTO update = new UserUpdateRequestDTO();

        update.setFullName("Updated Name");
        update.setEmail("updated@dwellora.com");
        update.setPhone("1112223333");
        update.setFlatNumber("A-102");

        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));

        // When & Then
        UserException ex =
                assertThrows(
                        UserException.class, () -> userService.updateResident(10L, 5L, update));

        assertEquals("Resident not found in your apartment.", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName(
            "US-005: Given a resident from another apartment, when deleting, then it is rejected")
    void deleteResident_WrongApartment_ThrowsException() {

        // Given
        User existing = new User();

        existing.setUserId(5L);
        existing.setApartmentId(20L);
        existing.setRole(Role.RESIDENT);
        existing.setAccountStatus(AccountStatus.ACTIVE);

        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));

        // When & Then
        UserException ex =
                assertThrows(UserException.class, () -> userService.deleteResident(10L, 5L));

        assertEquals("Resident not found in your apartment.", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName(
            "US-005: Given a missing user id, when updated, then a UserException is thrown")
    void updateResident_NotFound_ThrowsException() {

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UserUpdateRequestDTO update = new UserUpdateRequestDTO();

        assertThrows(
                UserException.class, () -> userService.updateResident(10L, 999L, update));

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName(
            "US-005: Given a missing user id, when deleted, then a UserException is thrown")
    void deleteResident_NotFound_ThrowsException() {

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        UserException ex =
                assertThrows(UserException.class, () -> userService.deleteResident(10L, 999L));

        assertEquals("User not found with id: 999", ex.getMessage());

        verify(userRepository, never()).save(any());
    }

    // ==========================================
    // GET RESIDENTS BY APARTMENT
    // ==========================================

    @Test
    @DisplayName("US-005: Should return active residents for an apartment")
    void getResidentsByApartment_ReturnsActiveResidents() {

        // Given
        User resident = new User();

        resident.setUserId(5L);
        resident.setApartmentId(10L);
        resident.setFullName("Resident One");
        resident.setEmail("resident@dwellora.com");
        resident.setPhone("9998887776");
        resident.setFlatNumber("A-101");
        resident.setRole(Role.RESIDENT);
        resident.setAccountStatus(AccountStatus.ACTIVE);

        when(userRepository.findByApartmentIdAndRole(10L, Role.RESIDENT))
                .thenReturn(List.of(resident));

        // When
        List<UserResponseDTO> results = userService.getResidentsByApartment(10L);

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Resident One", results.get(0).getFullName());
        assertEquals(AccountStatus.ACTIVE, results.get(0).getAccountStatus());
    }

    @Test
    @DisplayName("US-005: Should exclude inactive residents")
    void getResidentsByApartment_ExcludesInactiveResidents() {

        // Given
        User activeResident = new User();

        activeResident.setUserId(5L);
        activeResident.setApartmentId(10L);
        activeResident.setFullName("Active Resident");
        activeResident.setEmail("active@dwellora.com");
        activeResident.setRole(Role.RESIDENT);
        activeResident.setAccountStatus(AccountStatus.ACTIVE);

        User inactiveResident = new User();

        inactiveResident.setUserId(6L);
        inactiveResident.setApartmentId(10L);
        inactiveResident.setFullName("Inactive Resident");
        inactiveResident.setEmail("inactive@dwellora.com");
        inactiveResident.setRole(Role.RESIDENT);
        inactiveResident.setAccountStatus(AccountStatus.INACTIVE);

        when(userRepository.findByApartmentIdAndRole(10L, Role.RESIDENT))
                .thenReturn(List.of(activeResident, inactiveResident));

        // When
        List<UserResponseDTO> results = userService.getResidentsByApartment(10L);

        // Then
        assertEquals(1, results.size());
        assertEquals("Active Resident", results.get(0).getFullName());
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

        assertEquals("jane@dwellora.com", results.get(0).getEmail());
    }

    // ==========================================
    // GET USER BY ID
    // ==========================================

    @Test
    @DisplayName("Should return a user by id")
    void getUserById_ExistingUser_ReturnsUser() {

        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(activeManager));

        // When
        UserResponseDTO response = userService.getUserById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals(10L, response.getApartmentId());
        assertEquals("Jane Manager", response.getFullName());
        assertEquals("jane@dwellora.com", response.getEmail());
        assertEquals(Role.MANAGER, response.getRole());
    }

    @Test
    @DisplayName("Should throw exception when user id does not exist")
    void getUserById_NotFound_ThrowsException() {

        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        UserException ex =
                assertThrows(UserException.class, () -> userService.getUserById(999L));

        assertEquals("User not found with id: 999", ex.getMessage());
    }
}