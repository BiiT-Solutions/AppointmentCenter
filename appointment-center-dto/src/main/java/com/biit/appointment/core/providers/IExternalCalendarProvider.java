package com.biit.appointment.core.providers;

import com.biit.appointment.core.exceptions.ExternalCalendarActionException;
import com.biit.appointment.core.exceptions.ExternalCalendarNotFoundException;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.models.CalendarProviderDTO;
import com.biit.appointment.core.models.ExternalCalendarCredentialsDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface IExternalCalendarProvider {

    CalendarProviderDTO from();

    List<AppointmentDTO> getEvents(LocalDateTime startingFrom, LocalDateTime endingTo, ExternalCalendarCredentialsDTO credentials)
            throws ExternalCalendarActionException, ExternalCalendarNotFoundException;

    List<AppointmentDTO> getEvents(int numberOfEvents, LocalDateTime startingFrom, ExternalCalendarCredentialsDTO credentials)
            throws ExternalCalendarActionException, ExternalCalendarNotFoundException;

    AppointmentDTO getEvent(String externalReference, ExternalCalendarCredentialsDTO credentials)
            throws ExternalCalendarActionException, ExternalCalendarNotFoundException;

    String addEvent(AppointmentDTO appointmentDTO, ExternalCalendarCredentialsDTO credentials)
            throws ExternalCalendarActionException, ExternalCalendarNotFoundException;

    void deleteEvent(AppointmentDTO appointmentDTO, ExternalCalendarCredentialsDTO credentials)
            throws ExternalCalendarActionException, ExternalCalendarNotFoundException;

    ExternalCalendarCredentialsDTO updateToken(ExternalCalendarCredentialsDTO externalCalendarCredentialsDTO);
}
