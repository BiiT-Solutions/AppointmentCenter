package com.biit.appointment.core.converters.models;

import com.biit.appointment.persistence.entities.Recurrence;
import com.biit.server.converters.models.ConverterRequest;

public class RecurrenceConverterRequest extends ConverterRequest<Recurrence> {
    public RecurrenceConverterRequest(Recurrence recurrence) {
        super(recurrence);
    }
}
