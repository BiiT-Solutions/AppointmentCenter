package com.biit.appointment.core.converters.models;

import com.biit.appointment.persistence.entities.Appointment;
import com.biit.server.converters.models.ConverterRequest;

public class AppointmentConverterRequest extends ConverterRequest<Appointment> {
    public AppointmentConverterRequest(Appointment appointment) {
        super(appointment);
    }
}
