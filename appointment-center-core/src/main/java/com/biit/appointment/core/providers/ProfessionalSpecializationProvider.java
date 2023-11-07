package com.biit.appointment.core.providers;

import com.biit.appointment.core.exceptions.ProfessionalSpecializationNotFoundException;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ProfessionalSpecialization;
import com.biit.appointment.persistence.repositories.ProfessionalSpecializationRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class ProfessionalSpecializationProvider extends ElementProvider<ProfessionalSpecialization, Long, ProfessionalSpecializationRepository> {

    public ProfessionalSpecializationProvider(ProfessionalSpecializationRepository repository) {
        super(repository);
    }

    public List<ProfessionalSpecialization> findByName(String name) {
        return getRepository().findByName(name);
    }

    public List<ProfessionalSpecialization> findByNameAndDeleted(Collection<String> names) {
        return getRepository().findByNameIn(names);
    }

    public ProfessionalSpecialization findByNameAndOrganizationId(String name, Long organizationId) {
        return getRepository().findByNameAndOrganizationId(name, organizationId).orElseThrow(() ->
                new ProfessionalSpecializationNotFoundException(this.getClass(), "No specialization defined with name '" + name + "' and organization '"
                        + organizationId + "'"));
    }

    public List<ProfessionalSpecialization> findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(
            Long organizationId, AppointmentType appointmentType) {
        return getRepository().findByOrganizationIdAndAppointmentType(organizationId, appointmentType);
    }

    public List<ProfessionalSpecialization> findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(
            Long organizationId, Collection<AppointmentType> appointmentTypes) {
        return getRepository().findByOrganizationIdAndAppointmentTypeIn(organizationId, appointmentTypes);
    }

    public List<ProfessionalSpecialization> findByUserId(Long userId) {
        return getRepository().findByUserId(userId);
    }

    public List<ProfessionalSpecialization> findByUserIdAndOrganizationId(Long userId, Long organizationId) {
        return getRepository().findByUserIdAndOrganizationId(userId, organizationId);
    }

    public List<ProfessionalSpecialization> findByUserId(Collection<Long> userIds) {
        return getRepository().findByUserIdIn(userIds);
    }

    public List<ProfessionalSpecialization> findByUserIdAndOrganizationId(Collection<Long> userIds, Long organizationId) {
        return getRepository().findByUserIdInAndOrganizationId(userIds, organizationId);
    }
}
