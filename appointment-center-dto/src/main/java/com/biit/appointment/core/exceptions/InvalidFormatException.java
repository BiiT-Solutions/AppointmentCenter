package com.biit.appointment.core.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.logger.LoggedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidFormatException extends LoggedException {
    private static final long serialVersionUID = 7130024111678831271L;

    public InvalidFormatException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public InvalidFormatException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.SEVERE);
    }

    public InvalidFormatException(Class<?> clazz) {
        this(clazz, "MyEntity not found");
    }

    public InvalidFormatException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
