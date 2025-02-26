package com.biit.appointment.core.converters.models;

import com.biit.appointment.persistence.entities.Availability;
import com.biit.server.converters.models.ConverterRequest;

public class AvailabilityConverterRequest extends ConverterRequest<Availability> {
    public AvailabilityConverterRequest(Availability availability) {
        super(availability);
    }
}
