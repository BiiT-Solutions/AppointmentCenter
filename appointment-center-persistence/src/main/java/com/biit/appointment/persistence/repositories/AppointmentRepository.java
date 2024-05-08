package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentStatus;
import com.biit.appointment.persistence.entities.AppointmentTemplate;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.server.persistence.repositories.ElementRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends ElementRepository<Appointment, Long> {

    /**
     * Finds all appointments from an organizer.
     *
     * @param organizer the organizer of the appointment.
     * @return a list of appointments.
     */
    List<Appointment> findByOrganizer(UUID organizer);

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
     * @param speakerIds a list of speakers
     * @return a list of appointments that contains any of the speakers.
     */
    List<Appointment> findDistinctBySpeakersIn(Collection<UUID> speakerIds);

    /**
     * Finds all appointments from a collection of attendees.
     *
     * @param attendeesIds a list of attendees
     * @return a list of appointments that contains any of the attendees.
     */
    List<Appointment> findDistinctByAttendeesIn(Collection<UUID> attendeesIds);

    /**
     * Finds all appointments from a collection of templates.
     *
     * @param appointmentTemplates a list of templates
     * @return a list of appointments that have been generated from a template.
     */
    List<Appointment> findByAppointmentTemplateIn(Collection<AppointmentTemplate> appointmentTemplates);

    /**
     * Find all appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId      the organization of the parameters (can be null for any organization).
     * @param organizer           who must resolve the appointment (can be null for any organizer).
     * @param examinationTypes    a collection of types of the appointment (can be null for any type).
     * @param appointmentStatuses the status of the appointment (can be null for any status or a list).
     * @param lowerTimeBoundary   the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary   the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted             the appointment is deleted or not.
     * @return a list of appointments.
     */
    @Query("""
            SELECT a FROM Appointment a WHERE
            (:organizationId IS NULL OR a.organizationId = :organizationId) AND
            (:organizer IS NULL OR a.organizer = :organizer) AND
            (:attendee IS NULL OR :attendee MEMBER OF a.attendees) AND
            (a.examinationType IN :examinationTypes OR :examinationTypes IS NULL) AND
            (a.status IN :appointmentStatuses OR :appointmentStatuses IS NULL) AND
            (((:lowerTimeBoundary IS NULL OR a.endTime >= :lowerTimeBoundary) AND
            (:upperTimeBoundary IS NULL OR a.startTime <= :upperTimeBoundary)) OR
            a.startTime IS NULL AND a.endTime IS NULL) AND
            (:deleted IS NULL OR a.deleted = :deleted)
            """)
    List<Appointment> findBy(
            @Param("organizationId") String organizationId,
            @Param("organizer") UUID organizer,
            @Param("attendee") UUID attendee,
            @Param("examinationTypes") Collection<ExaminationType> examinationTypes,
            @Param("appointmentStatuses") Collection<AppointmentStatus> appointmentStatuses,
            @Param("lowerTimeBoundary") LocalDateTime lowerTimeBoundary,
            @Param("upperTimeBoundary") LocalDateTime upperTimeBoundary,
            @Param("deleted") Boolean deleted);


    /**
     * Counts the total appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId      the organization of the parameters (can be null for any organization).
     * @param organizer           who must resolve the appointment (can be null for any organizer).
     * @param examinationTypes    the type of the appointment (can be null for any type).
     * @param appointmentStatuses the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary   the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary   the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted             the appointment is deleted or not.
     * @return the total number of appointments
     */
    @Query("""
            SELECT COUNT(a) FROM Appointment a WHERE
            (:organizationId IS NULL OR a.organizationId = :organizationId) AND
            (:organizer IS NULL OR a.organizer = :organizer) AND
            (:attendee IS NULL OR :attendee MEMBER OF a.attendees) AND
            (a.examinationType IN :examinationTypes OR :examinationTypes IS NULL) AND
            (a.status IN :appointmentStatuses OR :appointmentStatuses IS NULL) AND
            (((:lowerTimeBoundary IS NULL OR a.endTime >= :lowerTimeBoundary) AND
            (:upperTimeBoundary IS NULL OR a.startTime <= :upperTimeBoundary)) OR
            a.startTime IS NULL AND a.endTime IS NULL) AND
            (:deleted IS NULL OR a.deleted = :deleted)
            """)
    long count(
            @Param("organizationId") String organizationId,
            @Param("organizer") UUID organizer,
            @Param("attendee") UUID attendee,
            @Param("examinationTypes") Collection<ExaminationType> examinationTypes,
            @Param("appointmentStatuses") Collection<AppointmentStatus> appointmentStatuses,
            @Param("lowerTimeBoundary") LocalDateTime lowerTimeBoundary,
            @Param("upperTimeBoundary") LocalDateTime upperTimeBoundary,
            @Param("deleted") Boolean deleted);


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
