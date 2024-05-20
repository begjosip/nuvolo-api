package com.nuvolo.nuvoloapi.service;

import com.nuvolo.nuvoloapi.exceptions.InvalidPasswordException;
import com.nuvolo.nuvoloapi.exceptions.UserWithEmailAlreadyExists;
import com.nuvolo.nuvoloapi.model.dto.request.UserRequestDto;
import com.nuvolo.nuvoloapi.model.entity.NuvoloUser;
import com.nuvolo.nuvoloapi.model.entity.Verification;
import com.nuvolo.nuvoloapi.model.enums.RoleName;
import com.nuvolo.nuvoloapi.repository.NuvoloUserRepository;
import com.nuvolo.nuvoloapi.repository.RoleRepository;
import com.nuvolo.nuvoloapi.repository.VerificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final NuvoloUserRepository userRepository;

    private final RoleRepository roleRepository;

    private final VerificationRepository verificationRepository;

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
        Verification savedVerification  = verificationRepository.save(verification);
        log.debug("Verification with ID:{} saved to database.", savedVerification.getId());

        // TODO: Send email for account verification
    }

    @Transactional
    public NuvoloUser findUserByEmail(String email) {
        log.debug("Finding user for email {}", email);
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format("User with %s email not found.", email)));
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
