package com.biit.appointment.core.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RecurrenceNotFoundException extends NotFoundException {

    @Serial
    private static final long serialVersionUID = -1304136243869289060L;

    public RecurrenceNotFoundException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public RecurrenceNotFoundException(Class<?> clazz, String message) {
        super(clazz, message);
    }

    public RecurrenceNotFoundException(Class<?> clazz) {
        this(clazz, "MyEntity not found");
    }

    public RecurrenceNotFoundException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
