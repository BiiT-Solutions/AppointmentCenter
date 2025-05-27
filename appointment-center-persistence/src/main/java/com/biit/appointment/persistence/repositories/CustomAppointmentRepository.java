package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentStatus;
import com.biit.appointment.persistence.entities.ExaminationType;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CustomAppointmentRepository {

    /**
     * Find all appointments that match the search parameters. If startTime and endTime are defined, will search any appointment inside this range.
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
     * Counts the total appointments that match the search parameters. If startTime and endTime are defined, will search any appointment inside this range.
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
    long count(
            @Param("organizationId") String organizationId,
            @Param("organizer") UUID organizer,
            @Param("attendee") UUID attendee,
            @Param("examinationTypes") Collection<ExaminationType> examinationTypes,
            @Param("appointmentStatuses") Collection<AppointmentStatus> appointmentStatuses,
            @Param("lowerTimeBoundary") LocalDateTime lowerTimeBoundary,
            @Param("upperTimeBoundary") LocalDateTime upperTimeBoundary,
            @Param("deleted") Boolean deleted);
}
