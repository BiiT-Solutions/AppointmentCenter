package com.biit.appointment.core.controllers;

import com.biit.appointment.core.models.ExternalCalendarCredentialsDTO;

public interface IExternalCredentialsController {

    ExternalCalendarCredentialsDTO create(ExternalCalendarCredentialsDTO dto, String creatorName);
}
