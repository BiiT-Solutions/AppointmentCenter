package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.AppointmentTemplate;
import com.biit.server.persistence.repositories.ElementRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Query("""
            SELECT t FROM AppointmentTemplate t WHERE EXISTS
                (SELECT a.id FROM Appointment a WHERE a.appointmentTemplate = t AND :attendeeUUID MEMBER a.attendees)
            """)
    List<AppointmentTemplate> findDistinctByAttendeeIn(UUID attendeeUUID);

    @Query("""
            SELECT t FROM AppointmentTemplate t WHERE NOT EXISTS
                (SELECT a FROM Appointment a WHERE a.appointmentTemplate = t AND :attendeeUUID MEMBER a.attendees)
            """)
    List<AppointmentTemplate> findDistinctByAttendeeNotIn(UUID attendeeUUID);
}
