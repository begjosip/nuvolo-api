package com.nuvolo.nuvoloapi.exceptions;

public class UserWithEmailAlreadyExists extends RuntimeException {
    public UserWithEmailAlreadyExists(String message) {
        super(message);
    }
}
