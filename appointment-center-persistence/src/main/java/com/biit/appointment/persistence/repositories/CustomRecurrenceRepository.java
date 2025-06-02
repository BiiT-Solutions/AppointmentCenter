package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.entities.Recurrence;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CustomRecurrenceRepository {

    /**
     * Find all appointments that match the search parameters. If startTime and endTime are defined, will search any appointment inside this range.
     *
     * @param organizationId    the organization of the parameters (can be null for any organization).
     * @param organizer         who must resolve the appointment (can be null for any organizer).
     * @param examinationTypes  a collection of types of the appointment (can be null for any type).
     * @param lowerTimeBoundary the lower limit on time for searching an appointment (can be null for no limit).
     * @param upperTimeBoundary the upper limit on time for searching an appointment (can be null for no limit).
     * @return a list of appointments.
     */
    List<Recurrence> findBy(
            @Param("organizationId") String organizationId,
            @Param("organizer") UUID organizer,
            @Param("createdBy") String createdBy,
            @Param("examinationTypes") Collection<ExaminationType> examinationTypes,
            @Param("lowerTimeBoundary") LocalDateTime lowerTimeBoundary,
            @Param("upperTimeBoundary") LocalDateTime upperTimeBoundary);
}
