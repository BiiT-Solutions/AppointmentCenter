package com.biit.appointment.core.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.logger.LoggedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExternalCalendarActionException extends LoggedException {

    @Serial
    private static final long serialVersionUID = 1958950571430599259L;

    public ExternalCalendarActionException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public ExternalCalendarActionException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.SEVERE);
    }

    public ExternalCalendarActionException(Class<?> clazz) {
        this(clazz, "Failed action");
    }

    public ExternalCalendarActionException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
