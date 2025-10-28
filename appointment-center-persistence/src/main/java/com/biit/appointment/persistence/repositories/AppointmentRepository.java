package com.biit.appointment.persistence.repositories;

/*-
 * #%L
 * AppointmentCenter (Persistence)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentTemplate;
import com.biit.server.persistence.repositories.ElementRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


@Transactional
public interface AppointmentRepository extends ElementRepository<Appointment, Long>, CustomAppointmentRepository {

    /**
     * Finds all appointments from an organizer.
     *
     * @param organizer the organizer of the appointment.
     * @return a list of appointments.
     */
    List<Appointment> findByOrganizer(UUID organizer);


    /**
     * Finds all appointments that will be celebrated today
     *
     * @param organizer the organizer of the appointment.
     * @return a list of appointments that contains any of the attendees.
     */
    List<Appointment> findDistinctByOrganizerAndStartTimeGreaterThanEqualAndStartTimeLessThanEqual(
            UUID organizer, LocalDateTime lowerBound, LocalDateTime upperBound);

    /**
     * Finds all appointments from an organization.
     *
     * @param organizationId the organization.
     * @return a list of appointments.
     */
    List<Appointment> findByOrganizationId(String organizationId);

    /**
     * Finds all appointments from a group of speakers.
     *
     * @param speakerIds a list of speakers.
     * @return a list of appointments that contains any of the speakers.
     */
    List<Appointment> findDistinctBySpeakersIn(Collection<UUID> speakerIds);

    /**
     * Finds all appointments from a collection of attendees.
     *
     * @param attendeesIds a list of attendees.
     * @return a list of appointments that contains any of the attendees.
     */
    List<Appointment> findDistinctByAttendeesIn(Collection<UUID> attendeesIds);


    /**
     * Finds all appointments that will be celebrated today
     *
     * @param organizationId the organization to search.
     * @return a list of appointments that contains any of the attendees.
     */
    List<Appointment> findDistinctByOrganizationIdAndStartTimeGreaterThanEqualAndStartTimeLessThanEqual(
            String organizationId, LocalDateTime lowerBound, LocalDateTime upperBound);


    /**
     * Finds all appointments that will be celebrated today
     *
     * @return a list of appointments that contains any of the attendees.
     */
    List<Appointment> findDistinctByStartTimeGreaterThanEqualAndStartTimeLessThanEqual(LocalDateTime lowerBound, LocalDateTime upperBound);


    /**
     * Finds all appointments that will be celebrated today
     *
     * @return a list of appointments that contains any of the attendees.
     */
    List<Appointment> findByStartTimeGreaterThanEqual(LocalDateTime lowerBound);

    /**
     * Finds all appointments that will be celebrated today
     *
     * @param attendeesIds a list of attendees.
     * @return a list of appointments that contains any of the attendees.
     */
    List<Appointment> findDistinctByAttendeesInAndStartTimeGreaterThanEqual(
            Collection<UUID> attendeesIds, LocalDateTime lowerBound);


    /**
     * Finds all appointments that will be celebrated today
     *
     * @param speakersIds a list of attendees.
     * @return a list of appointments that contains any of the attendees.
     */
    List<Appointment> findDistinctBySpeakersInAndStartTimeGreaterThanEqual(
            Collection<UUID> speakersIds, LocalDateTime lowerBound);

    /**
     * Finds all appointments that will be celebrated today
     *
     * @param attendeesIds a list of attendees.
     * @return a list of appointments that contains any of the attendees.
     */
    List<Appointment> findDistinctByAttendeesInAndStartTimeGreaterThanEqualAndStartTimeLessThanEqual(
            Collection<UUID> attendeesIds, LocalDateTime lowerBound, LocalDateTime upperBound);


    /**
     * Finds all appointments that will be celebrated today
     *
     * @param speakersIds a list of speakers.
     * @return a list of appointments that contains any of the attendees.
     */
    List<Appointment> findDistinctBySpeakersInAndStartTimeGreaterThanEqualAndStartTimeLessThanEqual(
            Collection<UUID> speakersIds, LocalDateTime lowerBound, LocalDateTime upperBound);

    /**
     * Finds all appointments from a collection of attendees.
     *
     * @param attendeesIds a list of attendees.
     * @return a list of appointments that contains any of the attendees.
     */
    List<Appointment> findDistinctByAttendeesNotIn(Collection<UUID> attendeesIds);


    /**
     * Finds all appointments from a collection of attendees and a template
     *
     * @param attendeesIds         a list of attendees.
     * @param appointmentTemplates the templates to filter.
     * @return a list of appointments that contains any of the attendees.
     */
    List<Appointment> findDistinctByAttendeesInAndAppointmentTemplateIn(Collection<UUID> attendeesIds, Collection<AppointmentTemplate> appointmentTemplates);

    /**
     * Finds all appointments from a collection of templates.
     *
     * @param appointmentTemplates a list of templates.
     * @return a list of appointments that have been generated from a template.
     */
    List<Appointment> findByAppointmentTemplateIn(Collection<AppointmentTemplate> appointmentTemplates);

    /**
     * Finds all appointments that are related to a template.
     *
     * @return a list of appointments that have been generated from a template.
     */
    List<Appointment> findByAppointmentTemplateNotNull();


    /**
     * Checks if the appointment overlaps with any different appointment.
     *
     * @param appointment the appointment to check
     * @return the count of how many appointments overlaps with the input. The input is ignored on this count.
     */
    @Query("""
            SELECT COUNT(a) FROM Appointment a WHERE
            (:#{#appointment.id} IS NULL OR a.id <> :#{#appointment.id}) AND
            (:#{#appointment.organizationId} IS NULL OR a.organizationId = :#{#appointment.organizationId}) AND
            (:#{#appointment.organizer} IS NULL OR a.organizer = :#{#appointment.organizer}) AND
            (a.status <> com.biit.appointment.persistence.entities.AppointmentStatus.CANCELLED) AND
            (:#{#appointment.examinationType.appointmentOverlapsAllowed} = false OR a.examinationType.appointmentOverlapsAllowed = false) AND
            (
                (a.startTime >= :#{#appointment.startTime} AND a.startTime < :#{#appointment.endTime}) OR
                (a.endTime > :#{#appointment.startTime} AND a.endTime < :#{#appointment.endTime}) OR
                (a.startTime <= :#{#appointment.startTime} AND a.endTime >= :#{#appointment.endTime})
            ) AND
            (a.deleted = false)
            """)
    long overlaps(@Param("appointment") Appointment appointment);
}
