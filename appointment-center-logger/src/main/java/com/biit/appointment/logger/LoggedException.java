package com.biit.appointment.logger;


import org.springframework.http.HttpStatus;

public class LoggedException extends RuntimeException {
    private HttpStatus status;

    protected LoggedException(Class<?> clazz, String message, ExceptionType type, HttpStatus status) {
        super(message);
        this.status = status;
        final String className = clazz.getName();
        switch (type) {
            case INFO:
               AppointmentCenterLogger.info(className, message);
                break;
            case WARNING:
                AppointmentCenterLogger.warning(className, message);
                break;
            case SEVERE:
                AppointmentCenterLogger.severe(className, message);
                break;
            default:
                AppointmentCenterLogger.debug(className, message);
                break;
        }
    }

    protected LoggedException(Class<?> clazz,Throwable e, HttpStatus status) {
        this(clazz, e);
        this.status = status;
    }

    protected LoggedException(Class<?> clazz, Throwable e) {
        super(e);
        AppointmentCenterLogger.errorMessage(clazz, e);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
