package com.biit.appointment.rest.api.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.logger.LoggedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidGoogleCredentialsException extends LoggedException {

    @Serial
    private static final long serialVersionUID = -4620336136825213128L;

    public InvalidGoogleCredentialsException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public InvalidGoogleCredentialsException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.SEVERE, HttpStatus.FORBIDDEN);
    }
}
