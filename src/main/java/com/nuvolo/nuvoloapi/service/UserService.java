package com.nuvolo.nuvoloapi.service;

import com.nuvolo.nuvoloapi.exceptions.ForgottenPasswordException;
import com.nuvolo.nuvoloapi.exceptions.InvalidPasswordException;
import com.nuvolo.nuvoloapi.exceptions.UserVerificationException;
import com.nuvolo.nuvoloapi.exceptions.UserWithEmailAlreadyExists;
import com.nuvolo.nuvoloapi.model.dto.request.UserRequestDto;
import com.nuvolo.nuvoloapi.model.entity.ForgottenPassReset;
import com.nuvolo.nuvoloapi.model.entity.NuvoloUser;
import com.nuvolo.nuvoloapi.model.entity.Verification;
import com.nuvolo.nuvoloapi.model.enums.RoleName;
import com.nuvolo.nuvoloapi.mq.RabbitMqSender;
import com.nuvolo.nuvoloapi.mq.message.PasswordResetMessage;
import com.nuvolo.nuvoloapi.mq.message.VerificationMessage;
import com.nuvolo.nuvoloapi.repository.ForgottenPassResetRepository;
import com.nuvolo.nuvoloapi.repository.NuvoloUserRepository;
import com.nuvolo.nuvoloapi.repository.RoleRepository;
import com.nuvolo.nuvoloapi.repository.VerificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final NuvoloUserRepository userRepository;

    private final RoleRepository roleRepository;

    private final VerificationRepository verificationRepository;

    private final ForgottenPassResetRepository forgottenPassResetRepository;

    private final RabbitMqSender rabbitMqSender;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(UserRequestDto userDto) throws Exception {
        this.validateUserData(userDto);
        NuvoloUser user = NuvoloUser.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .lastName(userDto.getLastName())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .email(userDto.getEmail().toLowerCase())
                .isEnabled(false)
                .roles(List.of(roleRepository.findByName(RoleName.USER)
                        .orElseThrow(() -> new Exception("Role with name USER does not exist in database.")))
                )
                .build();
        log.debug("Saving user entity to database.");
        NuvoloUser savedUser = userRepository.save(user);
        log.debug("User with ID:{} saved to database.", savedUser.getId());

        Verification verification = Verification.builder()
                .token(UUID.randomUUID().toString())
                .isVerified(false)
                .user(savedUser)
                .build();
        log.debug("Saving user verification to database");
        Verification savedVerification = verificationRepository.save(verification);
        log.debug("Verification with ID:{} saved to database.", savedVerification.getId());

        log.debug("Sending user email verification");
        if (Boolean.FALSE.equals(rabbitMqSender.sendEmailVerificationMessage(VerificationMessage.mapVerificationEntityToMessage(savedVerification))))
            throw new AmqpException("Error occurred while sending user verification message.");
        log.debug("User email verification successfully sent");
    }

    @Transactional
    public NuvoloUser findUserByEmail(String email) {
        log.debug("Finding user for email {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with %s email not found.", email)));
    }

    public void verifyUserByToken(String token) {
        log.debug("Verifying user by token {}", token);
        Verification verification = verificationRepository.findByTokenAndIsVerified(token, Boolean.FALSE)
                .orElseThrow(() -> new UserVerificationException("Error while trying to validate user. User might already be validated or token is invalid."));
        NuvoloUser user = verification.getUser();
        log.debug("Saving verified user to database.");
        user.setIsEnabled(Boolean.TRUE);
        userRepository.save(user);
        log.debug("Verified user with ID:{} saved to database.", user.getId());
    }

    @Transactional
    public void requestForgottenPasswordReset(String email) {
        log.debug("Received password reset request from email {}", email);
        NuvoloUser user = findUserByEmail(email);
        if (Boolean.FALSE.equals(user.getIsEnabled())) {
            throw new UserVerificationException("User is not verified. Can't change password before verification.");
        }
        ForgottenPassReset forgottenPassReset = ForgottenPassReset.builder()
                .token(UUID.randomUUID().toString())
                .utilised(false)
                .user(user)
                .build();
        log.debug("Saving user forgotten password reset entity to database");
        ForgottenPassReset savedPassReset = forgottenPassResetRepository.save(forgottenPassReset);
        log.debug("Saved user forgotten password reset entity with ID: {} to database", savedPassReset.getId());

        log.debug("Sending password reset message to email service.");
        if (!rabbitMqSender.sendPasswordResetMessage(PasswordResetMessage.mapForgottenPassResetEntityToMessage(savedPassReset))) {
            throw new AmqpException("Error occurred while sending password reset request.");
        }
        log.debug("Password reset message sent successfully.");
    }

    @Transactional
    public void resetForgottenPassword(UserRequestDto userRequestDto) {
        log.debug("Resetting user password by token.");
        NuvoloUser user = userRepository.findByEmail(userRequestDto.getEmail()).
                orElseThrow(() -> new UsernameNotFoundException(String.format("User with %s email not found.", userRequestDto.getEmail())));
        ForgottenPassReset forgottenPassReset = forgottenPassResetRepository.findFirstByUserAndTokenAndUtilisedAndCreatedAtAfter
                        (user, userRequestDto.getToken(), Boolean.FALSE, LocalDateTime.now().minusDays(5))
                .orElseThrow(() -> new ForgottenPasswordException("No valid password reset requests found in 5 days. Send new request!"));
        log.debug("Found valid password reset request.");
        if (!userRequestDto.getPassword().equals(userRequestDto.getConfirmPassword())) {
            log.debug("Passwords are not matching.");
            throw new InvalidPasswordException("Passwords are not matching");
        }
        log.debug("Passwords validated. Changing user password!");

        forgottenPassReset.setUtilised(true);
        log.debug("Saving updated forgotten password request to database.");
        forgottenPassResetRepository.save(forgottenPassReset);
        log.debug("Utilised forgotten password request saved to database.");

        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        log.debug("Saving updated user entity to database.");
        NuvoloUser savedUser = userRepository.save(user);
        log.debug("Updated user with ID:{} saved to database.", savedUser.getId());
    }

    private void validateUserData(UserRequestDto userRequestDto) {
        log.debug("Validating user email and password data.");
        if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new UserWithEmailAlreadyExists(String.format("User with %s email already exist.", userRequestDto.getEmail()));
        }
        if (!userRequestDto.getPassword().equals(userRequestDto.getConfirmPassword())) {
            throw new InvalidPasswordException("Passwords are not matching.");
        }
        log.debug("User data is validated!");
    }

}
