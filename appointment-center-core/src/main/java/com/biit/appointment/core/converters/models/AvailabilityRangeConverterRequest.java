package com.biit.appointment.core.converters.models;

import com.biit.appointment.persistence.entities.AvailabilityRange;
import com.biit.server.converters.models.ConverterRequest;

public class AvailabilityRangeConverterRequest extends ConverterRequest<AvailabilityRange> {
    public AvailabilityRangeConverterRequest(AvailabilityRange availabilityRange) {
        super(availabilityRange);
    }
}
