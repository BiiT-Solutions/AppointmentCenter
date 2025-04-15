package com.biit.appointment.rest.api.exceptions;

import com.biit.appointment.core.exceptions.AppointmentNotFoundException;
import com.biit.appointment.core.exceptions.AppointmentTypeNotFoundException;
import com.biit.appointment.core.exceptions.AttendanceNotFoundException;
import com.biit.appointment.core.exceptions.ExaminationTypeNotFoundException;
import com.biit.appointment.core.exceptions.InvalidFormatException;
import com.biit.appointment.core.exceptions.InvalidParameterException;
import com.biit.appointment.core.exceptions.InvalidProfessionalSpecializationException;
import com.biit.appointment.core.exceptions.InvalidRecurrenceException;
import com.biit.appointment.core.exceptions.ProfessionalSpecializationNotFoundException;
import com.biit.appointment.core.exceptions.RecurrenceNotFoundException;
import com.biit.appointment.core.exceptions.YouAreAlreadyOnThisAppointmentException;
import com.biit.appointment.core.exceptions.YouAreNotOnThisAppointmentException;
import com.biit.appointment.persistence.exceptions.InvalidScheduleException;
import com.biit.server.exceptions.ErrorResponse;
import com.biit.server.exceptions.NotFoundException;
import com.biit.server.exceptions.ServerExceptionControllerAdvice;
import com.biit.server.logger.RestServerExceptionLogger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AppointmentCenterExceptionControllerAdvice extends ServerExceptionControllerAdvice {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> notFoundException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse("NOT_FOUND", "not_found", ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidRecurrenceException.class)
    public ResponseEntity<Object> invalidRecurrenceException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse("Recurrence is not valid!", "invalid_recurrence", ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecurrenceNotFoundException.class)
    public ResponseEntity<Object> recurrenceNotFoundException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse("Recurrence not found!", "recurrence_not_found", ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ExaminationTypeNotFoundException.class)
    public ResponseEntity<Object> examinationTypeNotFoundException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse("Examination type not found!", "examination_type_not_found", ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AppointmentTypeNotFoundException.class)
    public ResponseEntity<Object> appointmentTypeNotFoundException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse("Appointment type not found!", "appointment_type_not_found", ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AppointmentNotFoundException.class)
    public ResponseEntity<Object> appointmentNotFoundException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse("Appointment not found!", "appointment_not_found", ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProfessionalSpecializationNotFoundException.class)
    public ResponseEntity<Object> professionalSpecializationNotFoundException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse("Professional Specialization not found!", "professional_specialization_not_found", ex),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<Object> invalidParameterException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse("Invalid parameter!", "invalid_parameter", ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Object> invalidFormatException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "invalid_format", ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(YouAreNotOnThisAppointmentException.class)
    public ResponseEntity<Object> youAreNotOnThisAppointmentException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "user_not_in_appointment", ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(YouAreAlreadyOnThisAppointmentException.class)
    public ResponseEntity<Object> youAreAlreadyOnThisAppointmentException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "user_already_in_appointment", ex), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AttendanceNotFoundException.class)
    public ResponseEntity<Object> attendanceNotFoundException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "attendance_not_found", ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidScheduleException.class)
    public ResponseEntity<Object> invalidScheduleException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "invalid_schedule_range", ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidProfessionalSpecializationException.class)
    public ResponseEntity<Object> invalidProfessionalSpecializationException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "invalid_professional_specialization", ex), HttpStatus.BAD_REQUEST);
    }
}
