package com.nuvolo.nuvoloapi.mq.message;


import com.nuvolo.nuvoloapi.model.entity.Verification;
import lombok.Data;

@Data
public class VerificationMessage {

    private String email;

    private String firstName;

    private String lastName;

    private String token;

    private Boolean success;

    public static VerificationMessage mapVerificationEntityToMessage(Verification verification) {
        VerificationMessage verificationMessage = new VerificationMessage();
        verificationMessage.setEmail(verification.getUser().getEmail());
        verificationMessage.setFirstName(verification.getUser().getFirstName());
        verificationMessage.setLastName(verification.getUser().getLastName());
        verificationMessage.setToken(verification.getToken());
        return verificationMessage;
    }

}
