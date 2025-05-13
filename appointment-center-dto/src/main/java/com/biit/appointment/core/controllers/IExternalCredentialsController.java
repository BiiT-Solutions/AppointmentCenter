package com.biit.appointment.core.controllers;

import com.biit.appointment.core.models.CalendarProviderDTO;
import com.biit.appointment.core.models.ExternalCalendarCredentialsDTO;

import java.util.UUID;

public interface IExternalCredentialsController {

    ExternalCalendarCredentialsDTO create(ExternalCalendarCredentialsDTO dto, String creatorName);

    void delete(UUID userId, CalendarProviderDTO calendarProvider);
}
