package com.biit.appointment.core.converters.models;

import com.biit.appointment.persistence.entities.ExternalCalendarCredentials;
import com.biit.server.converters.models.ConverterRequest;

public class ExternalCalendarCredentialsConverterRequest extends ConverterRequest<ExternalCalendarCredentials> {
    public ExternalCalendarCredentialsConverterRequest(ExternalCalendarCredentials externalCalendarCredentials) {
        super(externalCalendarCredentials);
    }
}
