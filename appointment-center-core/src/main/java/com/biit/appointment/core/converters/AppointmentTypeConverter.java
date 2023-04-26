package com.biit.appointment.core.converters;

import com.biit.appointment.core.converters.models.AppointmentTypeConverterRequest;
import com.biit.appointment.core.models.AppointmentTypeDTO;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class AppointmentTypeConverter extends ElementConverter<AppointmentType, AppointmentTypeDTO, AppointmentTypeConverterRequest> {

    @Override
    protected AppointmentTypeDTO convertElement(AppointmentTypeConverterRequest from) {
        final AppointmentTypeDTO appointmentTypeDTO = new AppointmentTypeDTO();
        BeanUtils.copyProperties(from.getEntity(), appointmentTypeDTO);
        return appointmentTypeDTO;
    }

    @Override
    public AppointmentType reverse(AppointmentTypeDTO to) {
        if (to == null) {
            return null;
        }
        final AppointmentType appointmentType = new AppointmentType();
        BeanUtils.copyProperties(to, appointmentType);
        return appointmentType;
    }
}
