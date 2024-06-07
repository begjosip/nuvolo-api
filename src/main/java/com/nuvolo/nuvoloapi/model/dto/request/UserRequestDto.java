package com.nuvolo.nuvoloapi.model.dto.request;

import com.nuvolo.nuvoloapi.model.dto.request.validator.UserDtoValidator;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRequestDto {

    @NotNull(groups = {UserDtoValidator.PasswordChange.class})
    private Long id;

    @NotBlank(groups = {UserDtoValidator.Register.class}, message = "Insert first name")
    private String firstName;

    @NotBlank(groups = {UserDtoValidator.Register.class}, message = "Insert last name")
    private String lastName;

    @NotBlank(groups = {UserDtoValidator.SignIn.class, UserDtoValidator.Register.class,
            UserDtoValidator.PasswordResetRequest.class, UserDtoValidator.ForgottenPasswordChange.class}, message = "Email is blank.")
    @Email(groups = {UserDtoValidator.SignIn.class, UserDtoValidator.Register.class,
            UserDtoValidator.PasswordResetRequest.class, UserDtoValidator.ForgottenPasswordChange.class},
            message = "Insert valid email")
    private String email;

    @NotBlank(groups = {UserDtoValidator.PasswordChange.class}, message = "Insert old password")
    private String oldPassword;

    @NotBlank(groups = {UserDtoValidator.SignIn.class}, message = "Insert password for sign in")
    @Size(min = 8, groups = {UserDtoValidator.Register.class, UserDtoValidator.ForgottenPasswordChange.class,
            UserDtoValidator.PasswordChange.class}, message = "Password must be 8 characters long")
    private String password;

    @NotBlank(groups = {UserDtoValidator.Register.class, UserDtoValidator.ForgottenPasswordChange.class,
            UserDtoValidator.PasswordChange.class}, message = "Repeat password")
    private String confirmPassword;

    @Size(min = 36, max = 36, message = "Invalid password reset token.")
    @NotBlank(groups = {UserDtoValidator.ForgottenPasswordChange.class}, message = "Token missing.")
    private String token;

}
