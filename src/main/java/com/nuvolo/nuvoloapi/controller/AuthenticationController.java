package com.nuvolo.nuvoloapi.controller;


import com.nuvolo.nuvoloapi.model.dto.request.UserRequestDto;
import com.nuvolo.nuvoloapi.model.dto.request.validator.UserDtoValidator;
import com.nuvolo.nuvoloapi.model.dto.response.UserResponseDto;
import com.nuvolo.nuvoloapi.security.JwtService;
import com.nuvolo.nuvoloapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final UserService userService;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;


    @PostMapping("/sign-in")
    public ResponseEntity<Object> signIn(@Validated(UserDtoValidator.SignIn.class) @RequestBody UserRequestDto userRequestDto) {
        log.info(" > > > POST /api/v1/auth/sign-in");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userRequestDto.getEmail().toLowerCase(),
                        userRequestDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserResponseDto userResponse = userService.signInUser(userRequestDto.getEmail().toLowerCase());
        log.debug("User successfully signed in.");
        log.info(" < < < POST /api/v1/auth/sign-in");
        return ResponseEntity.ok().body(userResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@Validated(UserDtoValidator.Register.class) @RequestBody UserRequestDto userRequestDto) throws Exception {
        log.info(" > > > POST /api/v1/auth/register");
        userService.registerUser(userRequestDto);
        log.info(" < < < POST /api/v1/auth/register");
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/verify/{token}")
    public ResponseEntity<Object> verifyUserByToken(@PathVariable String token) {
        log.info(" > > > POST /api/v1/auth/verify/{}", token);
        userService.verifyUserByToken(token);
        log.info(" < < < POST /api/v1/auth/verify/{}", token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<Object> requestPasswordReset(
            @Validated(UserDtoValidator.PasswordResetRequest.class) @RequestBody UserRequestDto userRequestDto
    ) {
        log.info(" > > > POST /api/v1/auth/request-password-reset");
        userService.requestForgottenPasswordReset(userRequestDto.getEmail());
        log.info(" < < < POST /api/v1/auth/request-password-reset");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@Validated(UserDtoValidator.ForgottenPasswordChange.class)
                                                @RequestBody UserRequestDto userRequestDto) {
        log.info(" > > > POST /api/v1/auth/reset-password");
        userService.resetForgottenPassword(userRequestDto);
        log.info(" < < < POST /api/v1/auth/reset-password");
        return ResponseEntity.ok().build();
    }
}
