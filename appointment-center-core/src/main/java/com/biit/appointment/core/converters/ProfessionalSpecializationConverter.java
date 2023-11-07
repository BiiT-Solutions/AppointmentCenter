package com.biit.appointment.core.converters;

import com.biit.appointment.core.converters.models.AppointmentTypeConverterRequest;
import com.biit.appointment.core.converters.models.ProfessionalSpecializationConverterRequest;
import com.biit.appointment.core.models.ProfessionalSpecializationDTO;
import com.biit.appointment.persistence.entities.ProfessionalSpecialization;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ProfessionalSpecializationConverter extends
        ElementConverter<ProfessionalSpecialization, ProfessionalSpecializationDTO, ProfessionalSpecializationConverterRequest> {

    private final AppointmentTypeConverter appointmentTypeConverter;

    public ProfessionalSpecializationConverter(AppointmentTypeConverter appointmentTypeConverter) {
        this.appointmentTypeConverter = appointmentTypeConverter;
    }

    @Override
    protected ProfessionalSpecializationDTO convertElement(ProfessionalSpecializationConverterRequest from) {
        final ProfessionalSpecializationDTO professionalSpecializationDTO = new ProfessionalSpecializationDTO();
        if (from.getEntity() != null) {
            BeanUtils.copyProperties(from.getEntity(), professionalSpecializationDTO);
            professionalSpecializationDTO.setAppointmentType(
                    appointmentTypeConverter.convert(new AppointmentTypeConverterRequest(from.getEntity().getAppointmentType())));
        }
        return professionalSpecializationDTO;
    }

    @Override
    public ProfessionalSpecialization reverse(ProfessionalSpecializationDTO to) {
        if (to == null) {
            return null;
        }
        final ProfessionalSpecialization professionalSpecialization = new ProfessionalSpecialization();
        BeanUtils.copyProperties(to, professionalSpecialization);
        professionalSpecialization.setAppointmentType(appointmentTypeConverter.reverse(to.getAppointmentType()));
        return professionalSpecialization;
    }
}
