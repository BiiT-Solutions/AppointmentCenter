package com.biit.appointment.core.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.logger.LoggedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidParameterException extends LoggedException {

    @Serial
    private static final long serialVersionUID = 2889863036556030421L;

    public InvalidParameterException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type, HttpStatus.BAD_REQUEST);
    }

    public InvalidParameterException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.SEVERE, HttpStatus.BAD_REQUEST);
    }

    public InvalidParameterException(Class<?> clazz) {
        this(clazz, "MyEntity not found");
    }

    public InvalidParameterException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
