package com.biit.appointment.core.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.logger.LoggedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRecurrenceException extends LoggedException {

    @Serial
    private static final long serialVersionUID = 3746101161048946447L;

    public InvalidRecurrenceException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type, HttpStatus.BAD_REQUEST);
    }

    public InvalidRecurrenceException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.SEVERE, HttpStatus.BAD_REQUEST);
    }

    public InvalidRecurrenceException(Class<?> clazz) {
        this(clazz, "MyEntity not found");
    }

    public InvalidRecurrenceException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
