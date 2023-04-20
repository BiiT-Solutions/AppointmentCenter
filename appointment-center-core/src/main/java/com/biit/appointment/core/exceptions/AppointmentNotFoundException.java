package com.biit.appointment.core.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AppointmentNotFoundException extends NotFoundException {

    private static final long serialVersionUID = -8372496008771191395L;

    public AppointmentNotFoundException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public AppointmentNotFoundException(Class<?> clazz, String message) {
        super(clazz, message);
    }

    public AppointmentNotFoundException(Class<?> clazz) {
        this(clazz, "MyEntity not found");
    }

    public AppointmentNotFoundException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
