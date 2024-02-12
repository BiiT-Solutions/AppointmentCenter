package com.biit.appointment.core.converters.models;

import com.biit.appointment.persistence.entities.AppointmentTemplate;
import com.biit.server.converters.models.ConverterRequest;

public class AppointmentTemplateConverterRequest extends ConverterRequest<AppointmentTemplate> {
    public AppointmentTemplateConverterRequest(AppointmentTemplate appointmentTemplate) {
        super(appointmentTemplate);
    }
}
