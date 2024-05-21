package com.nuvolo.nuvoloapi.mq.message;

import com.nuvolo.nuvoloapi.model.entity.ForgottenPassReset;
import lombok.Data;

@Data
public class PasswordResetMessage {

    private String email;

    private String firstName;

    private String lastName;

    private String token;

    private Boolean success;

    public static PasswordResetMessage mapForgottenPassResetEntityToMessage(ForgottenPassReset forgottenPassReset) {
        PasswordResetMessage passwordResetMessage = new PasswordResetMessage();
        passwordResetMessage.setEmail(forgottenPassReset.getUser().getEmail());
        passwordResetMessage.setFirstName(forgottenPassReset.getUser().getFirstName());
        passwordResetMessage.setLastName(forgottenPassReset.getUser().getLastName());
        passwordResetMessage.setToken(forgottenPassReset.getToken());
        return passwordResetMessage;
    }
}
