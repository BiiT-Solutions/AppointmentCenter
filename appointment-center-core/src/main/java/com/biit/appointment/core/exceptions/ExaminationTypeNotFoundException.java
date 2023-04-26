package com.biit.appointment.core.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ExaminationTypeNotFoundException extends NotFoundException {


    @Serial
    private static final long serialVersionUID = -3216682302482940102L;

    public ExaminationTypeNotFoundException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public ExaminationTypeNotFoundException(Class<?> clazz, String message) {
        super(clazz, message);
    }

    public ExaminationTypeNotFoundException(Class<?> clazz) {
        this(clazz, "MyEntity not found");
    }

    public ExaminationTypeNotFoundException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
