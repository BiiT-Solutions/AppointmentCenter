package com.biit.appointment.core.controllers;

import com.biit.appointment.core.converters.ProfessionalSpecializationConverter;
import com.biit.appointment.core.converters.models.ProfessionalSpecializationConverterRequest;
import com.biit.appointment.core.models.ProfessionalSpecializationDTO;
import com.biit.appointment.core.providers.AppointmentTypeProvider;
import com.biit.appointment.core.providers.ProfessionalSpecializationProvider;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ProfessionalSpecialization;
import com.biit.appointment.persistence.repositories.ProfessionalSpecializationRepository;
import com.biit.server.controller.ElementController;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class ProfessionalSpecializationController extends ElementController<ProfessionalSpecialization, Long, ProfessionalSpecializationDTO,
        ProfessionalSpecializationRepository, ProfessionalSpecializationProvider, ProfessionalSpecializationConverterRequest,
        ProfessionalSpecializationConverter> {

    private final AppointmentTypeProvider appointmentTypeProvider;

    protected ProfessionalSpecializationController(ProfessionalSpecializationProvider provider, ProfessionalSpecializationConverter converter,
                                                   AppointmentTypeProvider appointmentTypeProvider) {
        super(provider, converter);
        this.appointmentTypeProvider = appointmentTypeProvider;
    }

    @Override
    protected ProfessionalSpecializationConverterRequest createConverterRequest(ProfessionalSpecialization professionalSpecialization) {
        return new ProfessionalSpecializationConverterRequest(professionalSpecialization);
    }

    public List<ProfessionalSpecializationDTO> findByName(String name) {
        return convertAll(getProvider().findByName(name));
    }

    public ProfessionalSpecializationDTO findByNameAndOrganizationId(String name, Long organizationId) {
        return convert(getProvider().findByNameAndOrganizationId(name, organizationId));
    }

    public List<ProfessionalSpecializationDTO> findAllByOrOrganizationIdAndAppointmentType(Long organizationId, String appointmentTypeName) {
        return findAllByOrOrganizationIdAndAppointmentType(organizationId,
                appointmentTypeProvider.findByNameAndOrganizationId(appointmentTypeName, organizationId));
    }

    public List<ProfessionalSpecializationDTO> findAllByOrOrganizationIdAndAppointmentType(
            Long organizationId, AppointmentType appointmentType) {
        return convertAll(getProvider().findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(organizationId, appointmentType));
    }

    public List<ProfessionalSpecializationDTO> findAllByOrOrganizationIdAndAppointmentTypeInUsingNames(
            Long organizationId, Collection<String> appointmentTypeNames) {
        if (appointmentTypeNames != null) {
            final Set<AppointmentType> appointmentTypes = new HashSet<>();
            for (final String appointmentTypeName : appointmentTypeNames) {
                appointmentTypes.add(appointmentTypeProvider.findByNameAndOrganizationId(appointmentTypeName, organizationId));
            }
            return findAllByOrOrganizationIdAndAppointmentTypeIn(organizationId, appointmentTypes);
        } else {
            return findAllByOrOrganizationIdAndAppointmentTypeIn(organizationId, null);
        }
    }

    public List<ProfessionalSpecializationDTO> findAllByOrOrganizationIdAndAppointmentTypeIn(
            Long organizationId, Collection<AppointmentType> appointmentTypes) {
        return convertAll(getProvider().findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(organizationId, appointmentTypes));
    }

    public List<ProfessionalSpecializationDTO> findByUserId(Long userId) {
        return convertAll(getProvider().findByUserId(userId));
    }

    public List<ProfessionalSpecializationDTO> findByUserIdAndOrganizationId(Long userId, Long organizationId) {
        return convertAll(getProvider().findByUserIdAndOrganizationId(userId, organizationId));
    }

    public List<ProfessionalSpecializationDTO> findByUserId(Collection<Long> usersIds) {
        return convertAll(getProvider().findByUserId(usersIds));
    }

    public List<ProfessionalSpecializationDTO> findByUserIdAndOrganizationId(Collection<Long> usersIds, Long organizationId) {
        return convertAll(getProvider().findByUserIdAndOrganizationId(usersIds, organizationId));
    }
}
