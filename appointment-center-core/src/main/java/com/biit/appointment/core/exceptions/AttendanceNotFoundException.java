package com.biit.appointment.core.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AttendanceNotFoundException extends NotFoundException {

    private static final long serialVersionUID = -8319496008771191852L;

    public AttendanceNotFoundException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public AttendanceNotFoundException(Class<?> clazz, String message) {
        super(clazz, message);
    }

    public AttendanceNotFoundException(Class<?> clazz) {
        this(clazz, "MyEntity not found");
    }

    public AttendanceNotFoundException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
