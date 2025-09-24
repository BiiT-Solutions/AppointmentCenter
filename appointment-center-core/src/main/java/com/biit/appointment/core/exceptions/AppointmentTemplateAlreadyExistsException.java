package com.biit.appointment.core.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.logger.LoggedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AppointmentTemplateAlreadyExistsException extends LoggedException {

    @Serial
    private static final long serialVersionUID = -4871244753815473715L;

    public AppointmentTemplateAlreadyExistsException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public AppointmentTemplateAlreadyExistsException(Class<?> clazz, String message) {
        super(clazz, message, ExceptionType.INFO, HttpStatus.BAD_REQUEST);
    }

    public AppointmentTemplateAlreadyExistsException(Class<?> clazz) {
        this(clazz, "Template already exists!");
    }

    public AppointmentTemplateAlreadyExistsException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
