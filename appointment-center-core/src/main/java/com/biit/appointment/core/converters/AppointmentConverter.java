package com.biit.appointment.core.converters;

import com.biit.appointment.core.converters.models.AppointmentConverterRequest;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;

public class AppointmentConverter extends ElementConverter<Appointment, AppointmentDTO, AppointmentConverterRequest> {
    @Override
    protected AppointmentDTO convertElement(AppointmentConverterRequest from) {
        final AppointmentDTO appointmentDTO = new AppointmentDTO();
        BeanUtils.copyProperties(from.getEntity(), appointmentDTO);
        return appointmentDTO;
    }

    @Override
    public Appointment reverse(AppointmentDTO to) {
        if (to == null) {
            return null;
        }
        final Appointment appointment = new Appointment();
        BeanUtils.copyProperties(to, appointment);
        return appointment;
    }
}
