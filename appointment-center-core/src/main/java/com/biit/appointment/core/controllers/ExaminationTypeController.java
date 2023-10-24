package com.biit.appointment.core.controllers;

import com.biit.appointment.core.converters.ExaminationTypeConverter;
import com.biit.appointment.core.converters.models.ExaminationTypeConverterRequest;
import com.biit.appointment.core.models.ExaminationTypeDTO;
import com.biit.appointment.core.providers.AppointmentTypeProvider;
import com.biit.appointment.core.providers.ExaminationTypeProvider;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.repositories.ExaminationTypeRepository;
import com.biit.server.controller.BasicElementController;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class ExaminationTypeController extends BasicElementController<ExaminationType, Long, ExaminationTypeDTO, ExaminationTypeRepository,
        ExaminationTypeProvider, ExaminationTypeConverterRequest, ExaminationTypeConverter> {

    private final AppointmentTypeProvider appointmentTypeProvider;

    protected ExaminationTypeController(ExaminationTypeProvider provider, ExaminationTypeConverter converter,
                                        AppointmentTypeProvider appointmentTypeProvider) {
        super(provider, converter);
        this.appointmentTypeProvider = appointmentTypeProvider;
    }

    @Override
    protected ExaminationTypeConverterRequest createConverterRequest(ExaminationType examinationType) {
        return new ExaminationTypeConverterRequest(examinationType);
    }

    public List<ExaminationTypeDTO> findByNameAndDeleted(String name, boolean deleted) {
        return convertAll(getProvider().findByNameAndDeleted(name, deleted));
    }

    public ExaminationTypeDTO findByNameAndOrganizationId(String name, Long organizationId, boolean deleted) {
        return convert(getProvider().findByNameAndOrganizationId(name, organizationId, deleted));
    }

    public List<ExaminationTypeDTO> findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(Long organizationId, String appointmentTypeName, boolean deleted) {
        return findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(organizationId,
                appointmentTypeProvider.findByNameAndOrganizationId(appointmentTypeName, organizationId), deleted);
    }

    public List<ExaminationTypeDTO> findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(
            Long organizationId, AppointmentType appointmentType, boolean deleted) {
        return convertAll(getProvider().findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(organizationId, appointmentType, deleted));
    }

    public List<ExaminationTypeDTO> findAllByOrOrganizationIdAndAppointmentTypeInAndDeletedUsingNames(
            Long organizationId, Collection<String> appointmentTypeNames, boolean deleted) {
        if (appointmentTypeNames != null) {
            final Set<AppointmentType> appointmentTypes = new HashSet<>();
            for (final String appointmentTypeName : appointmentTypeNames) {
                appointmentTypes.add(appointmentTypeProvider.findByNameAndOrganizationId(appointmentTypeName, organizationId));
            }
            return findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(organizationId, appointmentTypes, deleted);
        } else {
            return findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(organizationId, null, deleted);
        }
    }

    public List<ExaminationTypeDTO> findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(
            Long organizationId, Collection<AppointmentType> appointmentTypes, boolean deleted) {
        return convertAll(getProvider().findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(organizationId, appointmentTypes, deleted));
    }
}
