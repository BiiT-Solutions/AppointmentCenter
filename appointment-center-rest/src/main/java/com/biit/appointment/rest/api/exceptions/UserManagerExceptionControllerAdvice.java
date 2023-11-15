package com.biit.appointment.rest.api.exceptions;

import com.biit.appointment.core.exceptions.AppointmentNotFoundException;
import com.biit.appointment.core.exceptions.AppointmentTypeNotFoundException;
import com.biit.appointment.core.exceptions.ExaminationTypeNotFoundException;
import com.biit.appointment.core.exceptions.InvalidParameterException;
import com.biit.appointment.core.exceptions.InvalidRecurrenceException;
import com.biit.appointment.core.exceptions.ProfessionalSpecializationNotFoundException;
import com.biit.appointment.core.exceptions.RecurrenceNotFoundException;
import com.biit.server.exceptions.NotFoundException;
import com.biit.server.exceptions.ServerExceptionControllerAdvice;
import com.biit.server.logger.RestServerExceptionLogger;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserManagerExceptionControllerAdvice extends ServerExceptionControllerAdvice {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> notFoundException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorMessage("NOT_FOUND", ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidRecurrenceException.class)
    public ResponseEntity<Object> invalidRecurrenceException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorMessage("Recurrence is not valid!", ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecurrenceNotFoundException.class)
    public ResponseEntity<Object> recurrenceNotFoundException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorMessage("Recurrence not found!", ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExaminationTypeNotFoundException.class)
    public ResponseEntity<Object> examinationTypeNotFoundException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorMessage("Examination type not found!", ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AppointmentTypeNotFoundException.class)
    public ResponseEntity<Object> appointmentTypeNotFoundException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorMessage("Appointment type not found!", ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<Object> appointmentNotFoundException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorMessage("Appointment not found!", ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProfessionalSpecializationNotFoundException.class)
    public ResponseEntity<Object> professionalSpecializationNotFoundException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorMessage("Professional Specialization not found!", ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<Object> invalidParameterException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorMessage("Parameter invalid!", ex), HttpStatus.BAD_REQUEST);
    }
}
