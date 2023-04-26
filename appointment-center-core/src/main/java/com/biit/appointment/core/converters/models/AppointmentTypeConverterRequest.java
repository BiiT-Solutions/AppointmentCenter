package com.biit.appointment.core.converters.models;

import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.server.converters.models.ConverterRequest;

public class AppointmentTypeConverterRequest extends ConverterRequest<AppointmentType> {
    public AppointmentTypeConverterRequest(AppointmentType appointmentType) {
        super(appointmentType);
    }
}
