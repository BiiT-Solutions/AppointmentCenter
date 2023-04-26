package com.biit.appointment.core.providers;

import com.biit.appointment.core.exceptions.AppointmentTypeNotFoundException;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.repositories.AppointmentTypeRepository;
import com.biit.server.providers.CrudProvider;
import org.springframework.stereotype.Service;

@Service
public class AppointmentTypeProvider extends CrudProvider<AppointmentType, Long, AppointmentTypeRepository> {

    public AppointmentTypeProvider(AppointmentTypeRepository repository) {
        super(repository);
    }

    public AppointmentType findByNameAndOrganizationId(String name, Long organizationId) {
        return repository.findByNameAndOrganizationId(name, organizationId).orElseThrow(() ->
                new AppointmentTypeNotFoundException(this.getClass(), "No appointment type found"));
    }
}
