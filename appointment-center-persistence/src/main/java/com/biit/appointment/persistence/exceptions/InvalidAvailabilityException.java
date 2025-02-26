package com.biit.appointment.persistence.exceptions;

import com.biit.appointment.logger.AppointmentCenterLogger;

import java.io.Serial;

public class InvalidAvailabilityException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 4546925557940053858L;

    public InvalidAvailabilityException(Class<?> clazz, String message) {
        super(message);
        AppointmentCenterLogger.severe(clazz, message);
    }

    public InvalidAvailabilityException(Class<?> clazz, Throwable e) {
        super(e);
        AppointmentCenterLogger.errorMessage(clazz, e);
    }
}
