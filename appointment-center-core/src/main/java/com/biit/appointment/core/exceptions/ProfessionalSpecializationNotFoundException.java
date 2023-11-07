package com.biit.appointment.core.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProfessionalSpecializationNotFoundException extends NotFoundException {


    @Serial
    private static final long serialVersionUID = -2782107431687274028L;

    public ProfessionalSpecializationNotFoundException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public ProfessionalSpecializationNotFoundException(Class<?> clazz, String message) {
        super(clazz, message);
    }

    public ProfessionalSpecializationNotFoundException(Class<?> clazz) {
        this(clazz, "MyEntity not found");
    }

    public ProfessionalSpecializationNotFoundException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
