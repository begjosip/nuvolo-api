package com.nuvolo.nuvoloapi.exceptions.handler;

import com.nuvolo.nuvoloapi.exceptions.InvalidPasswordException;
import com.nuvolo.nuvoloapi.exceptions.UserVerificationException;
import com.nuvolo.nuvoloapi.exceptions.UserWithEmailAlreadyExists;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.amqp.AmqpException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String TIMESTAMP = "timestamp";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleInvalidRequestArguments(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Invalid arguments!");
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        problemDetail.setProperty(TIMESTAMP, Instant.now().toString());
        Map<String, Object> argumentErrors = new HashMap<>();
        argumentErrors.put("errors",
                ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toList());
        problemDetail.setProperties(argumentErrors);
        return problemDetail;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserWithEmailAlreadyExists.class)
    public ProblemDetail handleUserAlreadyExists(UserWithEmailAlreadyExists ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("User with email already exists!");
        problemDetail.setProperty(TIMESTAMP, Instant.now().toString());
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return problemDetail;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ProblemDetail handleUserNotVerifiedOrEnabled(InternalAuthenticationServiceException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("User not verified and enabled!");
        problemDetail.setProperty(TIMESTAMP, Instant.now().toString());
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return problemDetail;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UserVerificationException.class)
    public ProblemDetail handleUserNotVerifiedOrEnabled(UserVerificationException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("User not verified or enabled!");
        problemDetail.setProperty(TIMESTAMP, Instant.now().toString());
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return problemDetail;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("Bad credentials!");
        problemDetail.setProperty(TIMESTAMP, Instant.now().toString());
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return problemDetail;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidPasswordException.class)
    public ProblemDetail handleInvalidRequestArguments(InvalidPasswordException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Passwords are not matching!");
        problemDetail.setProperty(TIMESTAMP, Instant.now().toString());
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return problemDetail;
    }

    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ExceptionHandler(AmqpException.class)
    public ProblemDetail globalErrorHandling(AmqpException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.getMessage());
        problemDetail.setTitle("Messaging error!");
        problemDetail.setProperty(TIMESTAMP, Instant.now().toString());
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return problemDetail;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ProblemDetail globalErrorHandling(Exception ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setTitle("Internal uncaught error!");
        problemDetail.setProperty(TIMESTAMP, Instant.now().toString());
        problemDetail.setInstance(URI.create(request.getRequestURI()));
        return problemDetail;
    }

}
