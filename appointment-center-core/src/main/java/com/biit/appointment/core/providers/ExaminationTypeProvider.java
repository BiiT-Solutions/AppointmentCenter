package com.biit.appointment.core.providers;

import com.biit.appointment.core.exceptions.ExaminationTypeNotFoundException;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.repositories.ExaminationTypeRepository;
import com.biit.server.providers.CrudProvider;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class ExaminationTypeProvider extends CrudProvider<ExaminationType, Long, ExaminationTypeRepository> {

    public ExaminationTypeProvider(ExaminationTypeRepository repository) {
        super(repository);
    }

    public List<ExaminationType> findByNameAndDeleted(String name, boolean deleted) {
        return repository.findByNameAndDeleted(name, deleted);
    }

    public ExaminationType findByNameAndOrganizationId(String name, Long organizationId) {
        return repository.findByNameAndOrganizationId(name, organizationId).orElseThrow(() ->
                new ExaminationTypeNotFoundException(this.getClass(), "No examination defined with name '" + name + "' and organization '" + organizationId + "'"));
    }

    public List<ExaminationType> findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(Long organizationId, AppointmentType appointmentType, boolean deleted) {
        return repository.findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(organizationId, appointmentType, deleted);
    }

    public List<ExaminationType> findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(Long organizationId, Collection<AppointmentType> appointmentTypes, boolean deleted) {
        return repository.findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(organizationId, appointmentTypes, deleted);
    }
}
