package com.biit.appointment.core.converters;

import com.biit.appointment.core.converters.models.AppointmentConverterRequest;
import com.biit.appointment.core.converters.models.ExaminationTypeConverterRequest;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class AppointmentConverter extends ElementConverter<Appointment, AppointmentDTO, AppointmentConverterRequest> {

    private final ExaminationTypeConverter examinationTypeConverter;

    public AppointmentConverter(ExaminationTypeConverter examinationTypeConverter) {
        this.examinationTypeConverter = examinationTypeConverter;
    }

    @Override
    protected AppointmentDTO convertElement(AppointmentConverterRequest from) {
        final AppointmentDTO appointmentDTO = new AppointmentDTO();
        BeanUtils.copyProperties(from.getEntity(), appointmentDTO);
        appointmentDTO.setExaminationType(examinationTypeConverter.convert(new ExaminationTypeConverterRequest(from.getEntity().getExaminationType())));
        return appointmentDTO;
    }

    @Override
    public Appointment reverse(AppointmentDTO to) {
        if (to == null) {
            return null;
        }
        final Appointment appointment = new Appointment();
        BeanUtils.copyProperties(to, appointment);
        appointment.setExaminationType(examinationTypeConverter.reverse(to.getExaminationType()));
        return appointment;
    }
}
