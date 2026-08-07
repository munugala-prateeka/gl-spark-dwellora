package com.dwellora.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Validates the Bean Validation ({@code jakarta.validation}) constraints declared
 * directly on the request DTOs — {@code @NotBlank}, {@code @Email}, {@code @Pattern},
 * etc.
 */
class UserDtoValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    // ==========================================
    // LoginRequestDTO
    // ==========================================

    @Test
    @DisplayName("LoginRequestDTO: a blank email fails validation")
    void loginRequest_BlankEmail_FailsValidation() {
        LoginRequestDTO dto = new LoginRequestDTO("", "SomePass1@");
        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("LoginRequestDTO: a malformed email fails validation")
    void loginRequest_MalformedEmail_FailsValidation() {
        LoginRequestDTO dto = new LoginRequestDTO("not-an-email", "SomePass1@");
        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("LoginRequestDTO: a blank password fails validation")
    void loginRequest_BlankPassword_FailsValidation() {
        LoginRequestDTO dto = new LoginRequestDTO("jane@dwellora.com", "");
        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("LoginRequestDTO: login intentionally has no password-strength rule, so a short/simple password still passes")
    void loginRequest_SimplePassword_PassesValidation() {
        // Login must accept whatever password a user set at activation time,
        // which may predate any strength rule tightened here later.
        LoginRequestDTO dto = new LoginRequestDTO("jane@dwellora.com", "x");
        assertTrue(validator.validate(dto).isEmpty());
    }

    // ==========================================
    // UserRequestDTO (manager creation)
    // ==========================================

    @Test
    @DisplayName("UserRequestDTO: a fully valid manager request passes validation")
    void userRequest_AllFieldsValid_PassesValidation() {
        UserRequestDTO dto = validUserRequest();
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("UserRequestDTO: a null apartmentId fails validation")
    void userRequest_NullApartmentId_FailsValidation() {
        UserRequestDTO dto = validUserRequest();
        dto.setApartmentId(null);
        assertFieldViolation(dto, "apartmentId");
    }

    @Test
    @DisplayName("UserRequestDTO: a blank full name fails validation")
    void userRequest_BlankFullName_FailsValidation() {
        UserRequestDTO dto = validUserRequest();
        dto.setFullName("  ");
        assertFieldViolation(dto, "fullName");
    }

    @Test
    @DisplayName("UserRequestDTO: a malformed email fails validation")
    void userRequest_MalformedEmail_FailsValidation() {
        UserRequestDTO dto = validUserRequest();
        dto.setEmail("jane-at-dwellora");
        assertFieldViolation(dto, "email");
    }

    @Test
    @DisplayName("UserRequestDTO: a blank phone fails validation")
    void userRequest_BlankPhone_FailsValidation() {
        UserRequestDTO dto = validUserRequest();
        dto.setPhone("");
        assertFieldViolation(dto, "phone");
    }

    @Test
    @DisplayName("UserRequestDTO: a password with no special character fails validation")
    void userRequest_PasswordMissingSpecialChar_FailsValidation() {
        UserRequestDTO dto = validUserRequest();
        dto.setPassword("NewPass123");
        assertFieldViolation(dto, "password");
    }

    @Test
    @DisplayName("UserRequestDTO: a password with no digit fails validation")
    void userRequest_PasswordMissingDigit_FailsValidation() {
        UserRequestDTO dto = validUserRequest();
        dto.setPassword("NewPass@@");
        assertFieldViolation(dto, "password");
    }

    @Test
    @DisplayName("UserRequestDTO: a password under 8 characters fails validation")
    void userRequest_PasswordTooShort_FailsValidation() {
        UserRequestDTO dto = validUserRequest();
        dto.setPassword("Nw1@");
        assertFieldViolation(dto, "password");
    }

    @Test
    @DisplayName("UserRequestDTO: a password with a letter, digit, and special character passes validation")
    void userRequest_StrongPassword_PassesValidation() {
        UserRequestDTO dto = validUserRequest();
        dto.setPassword("NewPass1@");
        assertTrue(validator.validate(dto).isEmpty());
    }

    // ==========================================
    // ActivateAccountDTO
    // ==========================================

    @Test
    @DisplayName("ActivateAccountDTO: a blank token fails validation")
    void activateAccount_BlankToken_FailsValidation() {
        ActivateAccountDTO dto = new ActivateAccountDTO();
        dto.setToken("");
        dto.setNewPassword("NewPass1@");
        assertFieldViolation(dto, "token");
    }

    @Test
    @DisplayName("ActivateAccountDTO: a password missing a special character fails validation")
    void activateAccount_PasswordMissingSpecialChar_FailsValidation() {
        ActivateAccountDTO dto = new ActivateAccountDTO();
        dto.setToken("some-token");
        dto.setNewPassword("NewPass123");
        assertFieldViolation(dto, "newPassword");
    }

    @Test
    @DisplayName("ActivateAccountDTO: a password meeting every rule passes validation")
    void activateAccount_StrongPassword_PassesValidation() {
        ActivateAccountDTO dto = new ActivateAccountDTO();
        dto.setToken("some-token");
        dto.setNewPassword("NewPass1@");
        assertTrue(validator.validate(dto).isEmpty());
    }

    // ==========================================
    // ResidentRequestDTO (manager adds a resident — no password field, US-005)
    // ==========================================

    @Test
    @DisplayName("ResidentRequestDTO: a fully valid resident request passes validation")
    void residentRequest_AllFieldsValid_PassesValidation() {
        ResidentRequestDTO dto = validResidentRequest();
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("ResidentRequestDTO: a null apartmentId fails validation")
    void residentRequest_NullApartmentId_FailsValidation() {
        ResidentRequestDTO dto = validResidentRequest();
        dto.setApartmentId(null);
        assertFieldViolation(dto, "apartmentId");
    }

    @Test
    @DisplayName("ResidentRequestDTO: a malformed email fails validation")
    void residentRequest_MalformedEmail_FailsValidation() {
        ResidentRequestDTO dto = validResidentRequest();
        dto.setEmail("not-an-email");
        assertFieldViolation(dto, "email");
    }

    @Test
    @DisplayName("ResidentRequestDTO: flatNumber has no constraint, so a blank value still passes validation")
    void residentRequest_BlankFlatNumber_PassesValidation() {
        ResidentRequestDTO dto = validResidentRequest();
        dto.setFlatNumber("");
        assertTrue(validator.validate(dto).isEmpty());
    }

    // ==========================================
    // UserUpdateRequestDTO (password optional on update, US-005 AC-3)
    // ==========================================

    @Test
    @DisplayName("UserUpdateRequestDTO: an empty password is allowed (no change intended) and passes validation")
    void userUpdateRequest_EmptyPassword_PassesValidation() {
        UserUpdateRequestDTO dto = validUpdateRequest();
        dto.setPassword("");
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("UserUpdateRequestDTO: a null password is allowed and passes validation")
    void userUpdateRequest_NullPassword_PassesValidation() {
        UserUpdateRequestDTO dto = validUpdateRequest();
        dto.setPassword(null);
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("UserUpdateRequestDTO: a provided password missing a special character fails validation")
    void userUpdateRequest_WeakPasswordProvided_FailsValidation() {
        UserUpdateRequestDTO dto = validUpdateRequest();
        dto.setPassword("NewPass123");
        assertFieldViolation(dto, "password");
    }

    @Test
    @DisplayName("UserUpdateRequestDTO: a provided password meeting every rule passes validation")
    void userUpdateRequest_StrongPasswordProvided_PassesValidation() {
        UserUpdateRequestDTO dto = validUpdateRequest();
        dto.setPassword("NewPass1@");
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    @DisplayName("UserUpdateRequestDTO: a malformed email fails validation")
    void userUpdateRequest_MalformedEmail_FailsValidation() {
        UserUpdateRequestDTO dto = validUpdateRequest();
        dto.setEmail("bad-email");
        assertFieldViolation(dto, "email");
    }

    // ==========================================
    // Fixtures + shared assertion helper
    // ==========================================

    private UserRequestDTO validUserRequest() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setApartmentId(10);
        dto.setFullName("Jane Manager");
        dto.setEmail("jane@dwellora.com");
        dto.setPassword("NewPass1@");
        dto.setPhone("9876543210");
        dto.setFlatNumber("Office");
        return dto;
    }

    private ResidentRequestDTO validResidentRequest() {
        ResidentRequestDTO dto = new ResidentRequestDTO();
        dto.setApartmentId(10);
        dto.setFullName("New Resident");
        dto.setEmail("resident@dwellora.com");
        dto.setPhone("9998887776");
        dto.setFlatNumber("A-101");
        return dto;
    }

    private UserUpdateRequestDTO validUpdateRequest() {
        UserUpdateRequestDTO dto = new UserUpdateRequestDTO();
        dto.setApartmentId(10);
        dto.setFullName("Updated Name");
        dto.setEmail("old@dwellora.com");
        dto.setPhone("1112223333");
        dto.setFlatNumber("A-102");
        return dto;
    }

    private <T> void assertFieldViolation(T dto, String fieldName) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals(fieldName)),
                () -> "Expected a violation on field '" + fieldName + "' but got: " + violations);
    }
}