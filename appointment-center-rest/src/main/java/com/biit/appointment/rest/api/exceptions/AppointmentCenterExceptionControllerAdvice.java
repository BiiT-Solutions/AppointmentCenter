package com.biit.appointment.rest.api.exceptions;

/*-
 * #%L
 * AppointmentCenter (Rest)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.appointment.core.exceptions.AppointmentNotFoundException;
import com.biit.appointment.core.exceptions.AppointmentTypeNotFoundException;
import com.biit.appointment.core.exceptions.AttendanceNotFoundException;
import com.biit.appointment.core.exceptions.ExaminationTypeNotFoundException;
import com.biit.appointment.core.exceptions.ExternalCalendarActionException;
import com.biit.appointment.core.exceptions.InvalidFormatException;
import com.biit.appointment.core.exceptions.InvalidParameterException;
import com.biit.appointment.core.exceptions.InvalidProfessionalSpecializationException;
import com.biit.appointment.core.exceptions.InvalidRecurrenceException;
import com.biit.appointment.core.exceptions.ProfessionalSpecializationNotFoundException;
import com.biit.appointment.core.exceptions.RecurrenceNotFoundException;
import com.biit.appointment.core.exceptions.YouAreAlreadyOnThisAppointmentException;
import com.biit.appointment.core.exceptions.YouAreNotOnThisAppointmentException;
import com.biit.appointment.persistence.exceptions.InvalidScheduleException;
import com.biit.kafka.exceptions.InvalidEventException;
import com.biit.server.exceptions.ErrorResponse;
import com.biit.server.exceptions.NotFoundException;
import com.biit.server.exceptions.ServerExceptionControllerAdvice;
import com.biit.server.logger.RestServerExceptionLogger;
import com.biit.usermanager.client.exceptions.ElementNotFoundException;
import com.biit.usermanager.client.exceptions.InvalidConfigurationException;
import com.biit.usermanager.client.exceptions.InvalidValueException;
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

    @ExceptionHandler(InvalidEventException.class)
    public ResponseEntity<Object> invalidEventException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "cannot_connect_to_kafka", ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ElementNotFoundException.class)
    public ResponseEntity<Object> elementNotFoundException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "not_found", ex), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidConfigurationException.class)
    public ResponseEntity<Object> invalidConfigurationException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "invalid_configuration_exception", ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InvalidValueException.class)
    public ResponseEntity<Object> invalidValueException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "invalid_parameter", ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExternalCalendarActionException.class)
    public ResponseEntity<Object> externalCalendarActionException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "cannot_access_to_external_calendar", ex), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidGoogleCredentialsException.class)
    public ResponseEntity<Object> invalidGoogleCredentialsException(Exception ex) {
        RestServerExceptionLogger.errorMessage(this.getClass().getName(), ex);
        return new ResponseEntity<>(new ErrorResponse(ex.getMessage(), "google_credentials_failure", ex), HttpStatus.FORBIDDEN);
    }


}
