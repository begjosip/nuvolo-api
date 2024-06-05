package com.nuvolo.nuvoloapi.service;

import com.nuvolo.nuvoloapi.exceptions.*;
import com.nuvolo.nuvoloapi.model.dto.request.UserRequestDto;
import com.nuvolo.nuvoloapi.model.entity.ForgottenPassReset;
import com.nuvolo.nuvoloapi.model.entity.NuvoloUser;
import com.nuvolo.nuvoloapi.model.entity.Role;
import com.nuvolo.nuvoloapi.model.entity.Verification;
import com.nuvolo.nuvoloapi.model.enums.RoleName;
import com.nuvolo.nuvoloapi.mq.RabbitMqSender;
import com.nuvolo.nuvoloapi.repository.ForgottenPassResetRepository;
import com.nuvolo.nuvoloapi.repository.NuvoloUserRepository;
import com.nuvolo.nuvoloapi.repository.RoleRepository;
import com.nuvolo.nuvoloapi.repository.VerificationRepository;
import com.nuvolo.nuvoloapi.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String TEST_EMAIL = "nuvolo@mail.com";

    private static final String TEST_PASSWORD = "test_pass";

    private static final String TEST_NAME = "nuvolo";

    private static final String TEST_RESET_TOKEN = "UUID_TEST";

    @Mock
    private NuvoloUserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private VerificationRepository verificationRepository;

    @Mock
    private ForgottenPassResetRepository forgottenPassResetRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RabbitMqSender rabbitMqSender;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<ForgottenPassReset> forgottenPassResetCaptor;

    @Captor
    private ArgumentCaptor<NuvoloUser> nuvoloUserCaptor;

    @Captor
    private ArgumentCaptor<Verification> verificationCaptor;

    @Test
    void testRegisterUser_success() throws Exception {
        UserRequestDto userRequestDto = this.createValidUserRegistrationRequestDto();
        Role role = this.createUserRoleEntity();
        Verification verification = this.createValidVerification();
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userRequestDto.getPassword())).thenReturn("ENCODED_PASSWORD");
        when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.of(role));
        when(userRepository.save(nuvoloUserCaptor.capture())).thenAnswer(invocation -> {
            NuvoloUser savedUser = nuvoloUserCaptor.getValue();
            savedUser.setId(1L);
            return savedUser;
        });
        when(verificationRepository.save(any())).thenReturn(verification);
        when(rabbitMqSender.sendEmailVerificationMessage(any())).thenReturn(Boolean.TRUE);
        userService.registerUser(userRequestDto);
        assertEquals(TEST_EMAIL, nuvoloUserCaptor.getValue().getEmail());
        assertEquals(TEST_NAME, nuvoloUserCaptor.getValue().getFirstName());
        assertEquals(TEST_NAME, nuvoloUserCaptor.getValue().getLastName());
        assertEquals("ENCODED_PASSWORD", nuvoloUserCaptor.getValue().getPassword());
        assertEquals(Boolean.FALSE, nuvoloUserCaptor.getValue().getIsEnabled());
    }

    @Test
    void testRegisterUser_noRoleWithNameUser() {
        UserRequestDto userRequestDto = this.createValidUserRegistrationRequestDto();
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userRequestDto.getPassword())).thenReturn("ENCODED_PASSWORD");
        when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.empty());
        var exception = assertThrows(UserRoleException.class, () -> userService.registerUser(userRequestDto));
        assertEquals("Role with name USER does not exist in database.", exception.getMessage());
    }

    @Test
    void testRegisterUser_userAlreadyExists() {
        UserRequestDto userRequestDto = this.createValidUserRegistrationRequestDto();
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.ofNullable(this.createValidUser()));
        var exception = assertThrows(UserWithEmailAlreadyExists.class, () -> userService.registerUser(userRequestDto));
        assertEquals(String.format("User with %s email already exist.", TEST_EMAIL), exception.getMessage());
    }

    @Test
    void testRegisterUser_passwordsAreNotMatching() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        UserRequestDto userRequestDto = this.createInvalidConfirmPasswordRequest();
        var exception = assertThrows(InvalidPasswordException.class, () -> userService.registerUser(userRequestDto));
        assertEquals("Passwords are not matching.", exception.getMessage());
    }

    @Test
    void testRegisterUser_emailVerificationError() {
        UserRequestDto userRequestDto = this.createValidUserRegistrationRequestDto();
        Role role = this.createUserRoleEntity();
        Verification verification = this.createValidVerification();
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userRequestDto.getPassword())).thenReturn("ENCODED_PASSWORD");
        when(roleRepository.findByName(RoleName.USER)).thenReturn(Optional.of(role));
        when(userRepository.save(nuvoloUserCaptor.capture())).thenAnswer(invocation -> {
            NuvoloUser savedUser = nuvoloUserCaptor.getValue();
            savedUser.setId(1L);
            return savedUser;
        });
        when(verificationRepository.save(any())).thenReturn(verification);
        when(rabbitMqSender.sendEmailVerificationMessage(any())).thenReturn(Boolean.FALSE);
        var exception = assertThrows(AmqpException.class, () -> userService.registerUser(userRequestDto));
        assertEquals("Error occurred while sending user verification message.", exception.getMessage());
    }

    @Test
    void testVerifyUserByToken_verificationInvalid() {
        when(verificationRepository.findByTokenAndIsVerified(TEST_RESET_TOKEN, Boolean.FALSE)).thenReturn(Optional.empty());
        var exception = assertThrows(UserVerificationException.class, () -> userService.verifyUserByToken(TEST_RESET_TOKEN));
        assertEquals("Error while trying to validate user. User might already be validated or token is invalid.", exception.getMessage());
    }

    @Test
    void testVerifyUserByToken_success() {
        Verification verification = this.createValidVerification();
        when(verificationRepository.findByTokenAndIsVerified(TEST_RESET_TOKEN, Boolean.FALSE)).thenReturn(Optional.of(verification));
        when(verificationRepository.save(verificationCaptor.capture())).thenAnswer(invocation -> verificationCaptor.getValue());
        userService.verifyUserByToken(TEST_RESET_TOKEN);
        verify(userRepository, times(1)).save(any());
        verify(verificationRepository, times(1)).save(any());
        assertEquals(Boolean.TRUE, verificationCaptor.getValue().getIsVerified());
    }

    @Test
    void testRequestForgottenPasswordReset_userNotVerified() {
        NuvoloUser user = this.createValidUser();
        user.setIsEnabled(Boolean.FALSE);
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        var exception = assertThrows(UserVerificationException.class, () -> userService.requestForgottenPasswordReset(TEST_EMAIL));
        assertEquals("User is not verified. Can't change password before verification.", exception.getMessage());
    }

    @Test
    void testRequestForgottenPasswordReset_passwordResetMessagingError() {
        NuvoloUser user = this.createValidUser();
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(rabbitMqSender.sendPasswordResetMessage(any())).thenReturn(Boolean.FALSE);
        when(forgottenPassResetRepository.save(forgottenPassResetCaptor.capture())).thenAnswer(invocation -> {
            ForgottenPassReset passReset = forgottenPassResetCaptor.getValue();
            passReset.setId(1L);
            return passReset;
        });
        var exception = assertThrows(AmqpException.class, () -> userService.requestForgottenPasswordReset(TEST_EMAIL));
        assertEquals("Error occurred while sending password reset request.", exception.getMessage());
    }

    @Test
    void testRequestForgottenPasswordReset_success() {
        NuvoloUser user = this.createValidUser();
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(rabbitMqSender.sendPasswordResetMessage(any())).thenReturn(Boolean.TRUE);
        when(forgottenPassResetRepository.save(forgottenPassResetCaptor.capture())).thenAnswer(invocation -> {
            ForgottenPassReset passReset = forgottenPassResetCaptor.getValue();
            passReset.setId(1L);
            return passReset;
        });
        userService.requestForgottenPasswordReset(TEST_EMAIL);
        assertEquals(1L, forgottenPassResetCaptor.getValue().getId());
        assertEquals(Boolean.FALSE, forgottenPassResetCaptor.getValue().getUtilised());
        assertEquals(user, forgottenPassResetCaptor.getValue().getUser());
    }

    @Test
    void testResetForgottenPassword_noValidPasswordResetRequest() {
        NuvoloUser user = this.createValidUser();
        UserRequestDto userRequestDto = this.createValidPasswordResetRequest();
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.ofNullable(user));
        when(forgottenPassResetRepository
                .findFirstByUserAndTokenAndUtilisedAndCreatedAtAfter(eq(user), eq(userRequestDto.getToken()), eq(Boolean.FALSE), any(LocalDateTime.class))
        ).thenReturn(Optional.empty());
        var exception = assertThrows(ForgottenPasswordException.class, () -> userService.resetForgottenPassword(userRequestDto));
        assertEquals("No valid password reset requests found in 5 days. Send new request!", exception.getMessage());
    }

    @Test
    void testResetForgottenPassword_invalidConfirmPasswordResetRequest() {
        NuvoloUser user = this.createValidUser();
        UserRequestDto userRequestDto = this.createInvalidConfirmPasswordRequest();
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.ofNullable(user));
        when(forgottenPassResetRepository
                .findFirstByUserAndTokenAndUtilisedAndCreatedAtAfter(eq(user), eq(userRequestDto.getToken()), eq(Boolean.FALSE), any(LocalDateTime.class))
        ).thenReturn(Optional.ofNullable(this.createValidForgottenPassReset()));
        var exception = assertThrows(InvalidPasswordException.class, () -> userService.resetForgottenPassword(userRequestDto));
        assertEquals("Passwords are not matching", exception.getMessage());
    }

    @Test
    void testResetForgottenPassword_success() {
        NuvoloUser user = this.createValidUser();
        UserRequestDto userRequestDto = this.createValidPasswordResetRequest();
        ForgottenPassReset forgottenPassReset = this.createValidForgottenPassReset();
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.ofNullable(user));
        when(forgottenPassResetRepository
                .findFirstByUserAndTokenAndUtilisedAndCreatedAtAfter(eq(user), eq(userRequestDto.getToken()), eq(Boolean.FALSE), any(LocalDateTime.class))
        ).thenReturn(Optional.ofNullable(forgottenPassReset));
        when(forgottenPassResetRepository.save(forgottenPassResetCaptor.capture())).thenAnswer(invocation -> forgottenPassResetCaptor.getValue());
        when(passwordEncoder.encode(userRequestDto.getPassword())).thenReturn("ENCODED_PASSWORD");
        when(userRepository.save(nuvoloUserCaptor.capture())).thenAnswer(invocation -> nuvoloUserCaptor.getValue());

        userService.resetForgottenPassword(userRequestDto);
        assertEquals(Boolean.TRUE, forgottenPassResetCaptor.getValue().getUtilised());
        assertEquals("ENCODED_PASSWORD", nuvoloUserCaptor.getValue().getPassword());
    }

    @Test
    void testFindUserByEmail_success() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.ofNullable(this.createValidUser()));
        var user = userService.findUserByEmail(TEST_EMAIL);
        assertEquals(1L, user.getId());
        assertEquals(TEST_NAME, user.getFirstName());
        assertEquals(TEST_NAME, user.getLastName());
        assertEquals(TEST_EMAIL, user.getEmail());
        assertEquals(Boolean.TRUE, user.getIsEnabled());
    }

    @Test
    void testFindUserByEmail_userNotFound() {
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());
        var exception = assertThrows(UsernameNotFoundException.class, () -> userService.findUserByEmail(TEST_EMAIL));
        assertEquals(String.format("User with %s email not found.", TEST_EMAIL), exception.getMessage());
    }

    @Test
    void testGetAllUsers_success() {
        when(userRepository.findAll()).thenReturn(List.of(this.createValidUser()));
        var users = userService.getAllUsers();
        assertEquals(1L, users.getFirst().getId());
        assertEquals(TEST_NAME, users.getFirst().getFirstName());
        assertEquals(TEST_NAME, users.getFirst().getLastName());
        assertEquals(TEST_EMAIL, users.getFirst().getEmail());
        assertEquals(Boolean.FALSE, users.getFirst().getIsAdmin());
    }

    @Test
    void testGetAllUsers_noUsersAvailable() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        assertEquals(Collections.emptyList(), userService.getAllUsers());
    }

    @Test
    void testSignInUser_success() {
        NuvoloUser user = this.createValidUser();
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(jwtService.generateToken(TEST_EMAIL, user.getRoles())).thenReturn("JWT_TOKEN");
        var signInResponse = userService.signInUser(TEST_EMAIL);

        assertEquals(1L, signInResponse.getId());
        assertEquals("JWT_TOKEN", signInResponse.getToken());
        assertEquals(TEST_NAME, signInResponse.getFirstName());
        assertEquals(TEST_NAME, signInResponse.getLastName());
        assertEquals(TEST_EMAIL, signInResponse.getEmail());
        assertEquals(Boolean.FALSE, signInResponse.getIsAdmin());
    }

    private NuvoloUser createValidUser() {
        return NuvoloUser.builder()
                .id(1L)
                .firstName(TEST_NAME)
                .lastName(TEST_NAME)
                .email(TEST_EMAIL)
                .password(TEST_PASSWORD)
                .roles(List.of(createUserRoleEntity()))
                .isEnabled(Boolean.TRUE)
                .build();
    }

    private Role createUserRoleEntity() {
        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setName(RoleName.USER);
        return userRole;
    }

    private Verification createValidVerification() {
        return Verification.builder()
                .id(1L)
                .token(UUID.randomUUID().toString())
                .isVerified(Boolean.FALSE)
                .user(this.createValidUser())
                .build();
    }

    private UserRequestDto createValidUserRegistrationRequestDto() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstName(TEST_NAME);
        userRequestDto.setLastName(TEST_NAME);
        userRequestDto.setEmail(TEST_EMAIL);
        userRequestDto.setPassword(TEST_PASSWORD);
        userRequestDto.setConfirmPassword(TEST_PASSWORD);
        return userRequestDto;
    }

    private ForgottenPassReset createValidForgottenPassReset() {
        return ForgottenPassReset.builder()
                .id(1L)
                .token(TEST_RESET_TOKEN)
                .utilised(Boolean.FALSE)
                .user(this.createValidUser())
                .build();
    }

    private UserRequestDto createValidPasswordResetRequest() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail(TEST_EMAIL);
        userRequestDto.setPassword(TEST_PASSWORD);
        userRequestDto.setConfirmPassword(TEST_PASSWORD);
        userRequestDto.setToken(TEST_RESET_TOKEN);
        return userRequestDto;
    }

    private UserRequestDto createInvalidConfirmPasswordRequest() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setEmail(TEST_EMAIL);
        userRequestDto.setPassword(TEST_PASSWORD);
        userRequestDto.setConfirmPassword(TEST_PASSWORD + ".");
        userRequestDto.setToken(TEST_RESET_TOKEN);
        return userRequestDto;
    }
}
