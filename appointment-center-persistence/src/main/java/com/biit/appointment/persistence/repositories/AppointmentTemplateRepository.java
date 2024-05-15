package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.AppointmentTemplate;
import com.biit.server.persistence.repositories.ElementRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentTemplateRepository extends ElementRepository<AppointmentTemplate, Long> {

    /**
     * Finds all appointments from an organizer.
     *
     * @param organizationId the organizer of the appointment.
     * @return a list of appointments.
     */
    List<AppointmentTemplate> findByOrganizationId(String organizationId);

    Optional<AppointmentTemplate> findByTitleAndOrganizationId(String title, String organizationId);

    List<AppointmentTemplate> findByTitleIn(Collection<String> title);
}
