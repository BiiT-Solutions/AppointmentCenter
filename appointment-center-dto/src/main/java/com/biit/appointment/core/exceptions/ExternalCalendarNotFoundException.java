package com.biit.appointment.core.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.logger.LoggedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ExternalCalendarNotFoundException extends LoggedException {

    @Serial
    private static final long serialVersionUID = 664507411528033438L;

    public ExternalCalendarNotFoundException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public ExternalCalendarNotFoundException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.SEVERE);
    }

    public ExternalCalendarNotFoundException(Class<?> clazz) {
        this(clazz, "Failed action");
    }

    public ExternalCalendarNotFoundException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
