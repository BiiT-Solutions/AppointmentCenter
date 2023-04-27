package com.biit.appointment.core.controllers;

import com.biit.appointment.core.converters.ExaminationTypeConverter;
import com.biit.appointment.core.converters.models.ExaminationTypeConverterRequest;
import com.biit.appointment.core.models.ExaminationTypeDTO;
import com.biit.appointment.core.providers.ExaminationTypeProvider;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.repositories.ExaminationTypeRepository;
import com.biit.server.controller.BasicInsertableController;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;

@Controller
public class ExaminationTypeController extends BasicInsertableController<ExaminationType, ExaminationTypeDTO, ExaminationTypeRepository,
        ExaminationTypeProvider, ExaminationTypeConverterRequest, ExaminationTypeConverter> {

    protected ExaminationTypeController(ExaminationTypeProvider provider, ExaminationTypeConverter converter) {
        super(provider, converter);
    }

    @Override
    protected ExaminationTypeConverterRequest createConverterRequest(ExaminationType examinationType) {
        return new ExaminationTypeConverterRequest(examinationType);
    }

    public List<ExaminationTypeDTO> findByNameAndDeleted(String name, boolean deleted) {
        return convertAll(provider.findByNameAndDeleted(name, deleted));
    }

    public ExaminationTypeDTO findByNameAndOrganizationId(String name, Long organizationId, boolean deleted) {
        return convert(provider.findByNameAndOrganizationId(name, organizationId, deleted));
    }

    public List<ExaminationTypeDTO> findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(Long organizationId, AppointmentType appointmentType, boolean deleted) {
        return convertAll(provider.findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(organizationId, appointmentType, deleted));
    }

    public List<ExaminationTypeDTO> findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(Long organizationId, Collection<AppointmentType> appointmentTypes, boolean deleted) {
        return convertAll(provider.findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(organizationId, appointmentTypes, deleted));
    }
}
