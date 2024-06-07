package com.nuvolo.nuvoloapi.mq.message;


import com.nuvolo.nuvoloapi.model.entity.ForgottenPassReset;
import com.nuvolo.nuvoloapi.model.entity.Verification;
import lombok.Data;

@Data
public class VerificationAndResetMessage {

    private String email;

    private String firstName;

    private String lastName;

    private String token;

    private Boolean success;

    public static VerificationAndResetMessage mapVerificationEntityToMessage(Verification verification) {
        VerificationAndResetMessage verificationMessage = new VerificationAndResetMessage();
        verificationMessage.setEmail(verification.getUser().getEmail());
        verificationMessage.setFirstName(verification.getUser().getFirstName());
        verificationMessage.setLastName(verification.getUser().getLastName());
        verificationMessage.setToken(verification.getToken());
        return verificationMessage;
    }

    public static VerificationAndResetMessage mapForgottenPassResetEntityToMessage(ForgottenPassReset forgottenPassReset) {
        VerificationAndResetMessage passResetMessage = new VerificationAndResetMessage();
        passResetMessage.setEmail(forgottenPassReset.getUser().getEmail());
        passResetMessage.setFirstName(forgottenPassReset.getUser().getFirstName());
        passResetMessage.setLastName(forgottenPassReset.getUser().getLastName());
        passResetMessage.setToken(forgottenPassReset.getToken());
        return passResetMessage;
    }

}
