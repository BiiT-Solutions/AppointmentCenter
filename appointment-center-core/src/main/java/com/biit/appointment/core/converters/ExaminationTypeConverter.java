package com.biit.appointment.core.converters;

import com.biit.appointment.core.converters.models.AppointmentTypeConverterRequest;
import com.biit.appointment.core.converters.models.ExaminationTypeConverterRequest;
import com.biit.appointment.core.models.ExaminationTypeDTO;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ExaminationTypeConverter extends ElementConverter<ExaminationType, ExaminationTypeDTO, ExaminationTypeConverterRequest> {

    private final AppointmentTypeConverter appointmentTypeConverter;

    public ExaminationTypeConverter(AppointmentTypeConverter appointmentTypeConverter) {
        this.appointmentTypeConverter = appointmentTypeConverter;
    }

    @Override
    protected ExaminationTypeDTO convertElement(ExaminationTypeConverterRequest from) {
        final ExaminationTypeDTO examinationTypeDTO = new ExaminationTypeDTO();
        if (from.getEntity() != null) {
            BeanUtils.copyProperties(from.getEntity(), examinationTypeDTO);
            examinationTypeDTO.setAppointmentType(appointmentTypeConverter.convert(new AppointmentTypeConverterRequest(from.getEntity().getAppointmentType())));
        }
        return examinationTypeDTO;
    }

    @Override
    public ExaminationType reverse(ExaminationTypeDTO to) {
        if (to == null) {
            return null;
        }
        final ExaminationType examinationType = new ExaminationType();
        BeanUtils.copyProperties(to, examinationType);
        examinationType.setAppointmentType(appointmentTypeConverter.reverse(to.getAppointmentType()));
        return examinationType;
    }
}
