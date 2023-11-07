package com.biit.appointment.core.converters.models;

import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.CustomProperty;
import com.biit.server.converters.models.ConverterRequest;

import java.util.Collection;

public class AppointmentConverterRequest extends ConverterRequest<Appointment> {
    private final Collection<CustomProperty> customProperties;

    public AppointmentConverterRequest(Appointment appointment) {
        super(appointment);
        this.customProperties = null;
    }

    public AppointmentConverterRequest(Appointment appointment, Collection<CustomProperty> customProperties) {
        super(appointment);
        this.customProperties = customProperties;
    }

    public Collection<CustomProperty> getCustomProperties() {
        return customProperties;
    }
}
