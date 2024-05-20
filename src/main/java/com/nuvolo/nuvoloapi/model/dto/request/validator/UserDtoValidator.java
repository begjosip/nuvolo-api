package com.nuvolo.nuvoloapi.model.dto.request.validator;

import jakarta.validation.groups.Default;


public interface UserDtoValidator {
    interface SignIn extends Default {
    }

    interface Register extends Default {
    }

    interface PasswordChange extends Default {
    }

    interface PasswordResetRequest extends Default {
    }

    interface ForgottenPasswordChange extends Default {
    }
}