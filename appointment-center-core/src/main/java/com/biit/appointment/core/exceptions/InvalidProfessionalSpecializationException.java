package com.biit.appointment.core.exceptions;

import com.biit.logger.ExceptionType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidProfessionalSpecializationException extends InvalidParameterException {

    @Serial
    private static final long serialVersionUID = 4152098686888843782L;

    public InvalidProfessionalSpecializationException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public InvalidProfessionalSpecializationException(Class<?> clazz, String message) {
        super(clazz, message);
    }

    public InvalidProfessionalSpecializationException(Class<?> clazz) {
        this(clazz, "Parameter not found");
    }

    public InvalidProfessionalSpecializationException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
