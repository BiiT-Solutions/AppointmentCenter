package com.biit.appointment.core.providers;

import com.biit.appointment.core.exceptions.AppointmentTemplateAlreadyExistsException;
import com.biit.appointment.core.exceptions.InvalidParameterException;
import com.biit.appointment.persistence.entities.AppointmentTemplate;
import com.biit.appointment.persistence.repositories.AppointmentTemplateRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.biit.database.encryption.KeyProperty.getEncryptionKey;

@Service
public class AppointmentTemplateProvider extends ElementProvider<AppointmentTemplate, Long, AppointmentTemplateRepository> {

    public AppointmentTemplateProvider(AppointmentTemplateRepository repository) {
        super(repository);
    }

    @Override
    public AppointmentTemplate save(AppointmentTemplate entity) {
        if (entity == null) {
            return null;
        }
        populateHash(entity);
        if (entity.getId() == null) {
            //Ensure it does not exist.
            findByTitleOrganizationId(entity.getTitle(), entity.getOrganizationId()).ifPresent(
                    e -> {
                        throw new AppointmentTemplateAlreadyExistsException(this.getClass(), "Already exists a workshop '" + entity.getTitle()
                                + "' in organization '" + entity.getOrganizationId() + "'.");
                    }
            );
            return getRepository().save(entity);
        } else {
            return super.save(entity);
        }
    }


    /**
     * Finds all appointments from an organization.
     *
     * @param organizationId the organization of the appointment.
     * @return a list of appointments.
     */
    public List<AppointmentTemplate> findByOrganizationId(String organizationId) {
        if (organizationId == null) {
            throw new InvalidParameterException(this.getClass(), "You must select an organization!");
        }
        return getRepository().findByOrganizationId(organizationId);
    }


    public Optional<AppointmentTemplate> findByTitleOrganizationId(String title, String organizationId) {
        if (organizationId == null) {
            throw new InvalidParameterException(this.getClass(), "You must select an organization!");
        }

        if (getEncryptionKey() != null && !getEncryptionKey().isBlank()) {
            return getRepository().findByTitleHashAndOrganizationId(title, organizationId);
        } else {
            return getRepository().findByTitleAndOrganizationId(title, organizationId);
        }
    }


    public List<AppointmentTemplate> findByTitleIn(Collection<String> titles) {
        if (getEncryptionKey() != null && !getEncryptionKey().isBlank()) {
            return getRepository().findByTitleHashIn(titles);
        } else {
            return getRepository().findByTitleIn(titles);
        }
    }


    public List<AppointmentTemplate> findByAttendeeOnAppointment(UUID attendeeUUID) {
        return getRepository().findDistinctByAttendeeIn(attendeeUUID);
    }

    public List<AppointmentTemplate> findByNonAttendeeOnAppointment(UUID attendeeUUID) {
        return getRepository().findDistinctByAttendeeNotIn(attendeeUUID);
    }

}
