package com.nuvolo.nuvoloapi.exceptions;

public class ForgottenPasswordException extends RuntimeException {
    public ForgottenPasswordException(String message) {
        super(message);
    }
}
