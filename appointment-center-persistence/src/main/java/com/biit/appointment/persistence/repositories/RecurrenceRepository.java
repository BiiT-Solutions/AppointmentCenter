package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.entities.Recurrence;
import com.biit.server.persistence.repositories.ElementRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface RecurrenceRepository extends ElementRepository<Recurrence, Long> {

    /**
     * Find all appointments that match the search parameters. If startTime and endTime are defined, will search any appointment inside this range.
     *
     * @param organizationId    the organization of the parameters (can be null for any organization).
     * @param organizer       who must resolve the appointment (can be null for any organizer).
     * @param examinationTypes  a collection of types of the appointment (can be null for any type).
     * @param lowerTimeBoundary the lower limit on time for searching an appointment (can be null for no limit).
     * @param upperTimeBoundary the upper limit on time for searching an appointment (can be null for no limit).
     * @return a list of appointments.
     */
    @Query("""
            SELECT r FROM Recurrence  r WHERE
            (:organizationId IS NULL OR r.organizationId = :organizationId) AND
            (:organizer IS NULL OR r.organizer = :organizer) AND
            (TRUE = :#{#examinationTypes == null} OR r.examinationType IN :examinationTypes) AND
            (((:lowerTimeBoundary IS NULL OR r.endsAt >= :lowerTimeBoundary) AND
            (:upperTimeBoundary IS NULL OR r.startsAt <= :upperTimeBoundary)) OR
            r.startsAt IS NULL AND r.endsAt IS NULL)
            """)
    List<Recurrence> findBy(
            @Param("organizationId") String organizationId, @Param("organizer") UUID organizer,
            @Param("examinationTypes") Collection<ExaminationType> examinationTypes,
            @Param("lowerTimeBoundary") LocalDateTime lowerTimeBoundary,
            @Param("upperTimeBoundary") LocalDateTime upperTimeBoundary);


    /**
     * Counts the total appointments that match the search parameters. If startTime and endTime are defined, will search any appointment inside this range.
     *
     * @param organizationId    the organization of the parameters (can be null for any organization).
     * @param organizer       who must resolve the appointment (can be null for any organizer).
     * @param examinationTypes  a collection of the appointments (can be null for any type).
     * @param lowerTimeBoundary the lower limit on time for searching an appointment (can be null for no limit).
     * @param upperTimeBoundary the upper limit on time for searching an appointment (can be null for no limit).
     * @return the total number of appointments.
     */
    @Query(value = """
            SELECT COUNT(r) FROM Recurrence r WHERE
            (:organizationId IS NULL OR r.organizationId = :organizationId) AND
            (:organizer IS NULL OR r.organizer = :organizer) AND
            (TRUE = :#{#examinationTypes == null} OR r.examinationType IN :examinationTypes) AND
            (((:lowerTimeBoundary IS NULL OR r.endsAt >= :lowerTimeBoundary) AND
            (:upperTimeBoundary IS NULL OR r.startsAt <= :upperTimeBoundary)) OR
            r.startsAt IS NULL AND r.endsAt IS NULL)
            """)
    long countExaminationTypesIn(
            @Param("organizationId") String organizationId, @Param("organizer") Long organizer,
            @Param("examinationTypes") Collection<ExaminationType> examinationTypes,
            @Param("lowerTimeBoundary") LocalDateTime lowerTimeBoundary,
            @Param("upperTimeBoundary") LocalDateTime upperTimeBoundary);
}
