package com.biit.appointment.core.services;

/*-
 * #%L
 * AppointmentCenter (DTO)
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

import com.biit.appointment.core.exceptions.ExternalCalendarActionException;
import com.biit.appointment.core.exceptions.ExternalCalendarNotFoundException;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.models.CalendarProviderDTO;
import com.biit.appointment.core.models.ExternalCalendarCredentialsDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface IExternalProviderCalendarService {

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
