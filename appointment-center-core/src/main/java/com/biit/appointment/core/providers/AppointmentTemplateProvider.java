package com.biit.appointment.core.providers;

import com.biit.appointment.core.exceptions.InvalidParameterException;
import com.biit.appointment.persistence.entities.AppointmentTemplate;
import com.biit.appointment.persistence.repositories.AppointmentTemplateRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentTemplateProvider extends ElementProvider<AppointmentTemplate, Long, AppointmentTemplateRepository> {


    public AppointmentTemplateProvider(AppointmentTemplateRepository repository) {
        super(repository);
    }


    /**
     * Finds all appointments from an organization.
     *
     * @param organizationId the organization of the appointment.
     * @return a list of appointments.
     */
    public List<AppointmentTemplate> findByOrganizationId(Long organizationId) {
        if (organizationId == null) {
            throw new InvalidParameterException(this.getClass(), "You must select an organization!");
        }
        return getRepository().findByOrganizationId(organizationId);
    }

}
