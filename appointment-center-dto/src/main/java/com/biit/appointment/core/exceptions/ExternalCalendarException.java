package com.biit.appointment.core.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.logger.LoggedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExternalCalendarException extends LoggedException {

    @Serial
    private static final long serialVersionUID = 358531421874652480L;

    public ExternalCalendarException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public ExternalCalendarException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.SEVERE);
    }

    public ExternalCalendarException(Class<?> clazz) {
        this(clazz, "Unknown error");
    }

    public ExternalCalendarException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
