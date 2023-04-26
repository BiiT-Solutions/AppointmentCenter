package com.biit.appointment.core.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AppointmentTypeNotFoundException extends NotFoundException {

    @Serial
    private static final long serialVersionUID = 134636358862810300L;

    public AppointmentTypeNotFoundException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public AppointmentTypeNotFoundException(Class<?> clazz, String message) {
        super(clazz, message);
    }

    public AppointmentTypeNotFoundException(Class<?> clazz) {
        this(clazz, "MyEntity not found");
    }

    public AppointmentTypeNotFoundException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
