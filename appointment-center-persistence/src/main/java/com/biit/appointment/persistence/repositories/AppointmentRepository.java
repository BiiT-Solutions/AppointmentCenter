package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentStatus;
import com.biit.appointment.persistence.entities.ExaminationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Finds all appointments from an organization.
     *
     * @param organizerId the organizer of the appointment.
     * @return a list of appointments.
     */
    List<Appointment> findByOrganizerId(Long organizerId);

    /**
     * Find all appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId    the organization of the parameters (can be null for any organization).
     * @param organizerId       who must resolve the appointment (can be null for any organizer).
     * @param examinationType   the type of the appointment (can be null for any type).
     * @param appointmentStatus the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted           the appointment is deleted or not.
     * @return a list of appointments.
     */
    @Query("""
            SELECT a FROM Appointment a WHERE
            (:organizationId IS NULL OR a.organizationId = :organizationId) AND
            (:organizerId IS NULL OR a.organizerId = :organizerId) AND
            (:examinationType IS NULL OR a.examinationType = :examinationType) AND
            (:appointmentStatus IS NULL OR a.status = :appointmentStatus) AND
            (((:lowerTimeBoundary IS NULL OR a.endTime >= :lowerTimeBoundary) AND
            (:upperTimeBoundary IS NULL OR a.startTime <= :upperTimeBoundary)) OR
            a.startTime IS NULL AND a.endTime IS NULL) AND
            (:deleted IS NULL OR a.deleted = :deleted)
            """)
    List<Appointment> findAll(
            @Param("organizationId") Long organizationId, @Param("organizerId") Long organizerId, @Param("examinationType") ExaminationType examinationType,
            @Param("appointmentStatus") AppointmentStatus appointmentStatus, @Param("lowerTimeBoundary") LocalDateTime lowerTimeBoundary,
            @Param("upperTimeBoundary") LocalDateTime upperTimeBoundary, @Param("deleted") Boolean deleted);


    /**
     * Find all appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId    the organization of the parameters (can be null for any organization).
     * @param organizerId       who must resolve the appointment (can be null for any organizer).
     * @param examinationTypes  a collection of types of the appointment (can be null for any type).
     * @param appointmentStatus the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted           the appointment is deleted or not.
     * @return a list of appointments.
     */
    @Query("""
            SELECT a FROM Appointment a WHERE
            (:organizationId IS NULL OR a.organizationId = :organizationId) AND
            (:organizerId IS NULL OR a.organizerId = :organizerId) AND
            (:customerId IS NULL OR a.customerId = :customerId) AND
            (:examinationTypes IS NULL OR a.examinationType IN :examinationTypes) AND
            (:appointmentStatus IS NULL OR a.status = :appointmentStatus) AND
            (((:lowerTimeBoundary IS NULL OR a.endTime >= :lowerTimeBoundary) AND
            (:upperTimeBoundary IS NULL OR a.startTime <= :upperTimeBoundary)) OR
            a.startTime IS NULL AND a.endTime IS NULL) AND
            (:deleted IS NULL OR a.deleted = :deleted)
            """)
    List<Appointment> findBy(
            @Param("organizationId") Long organizationId, @Param("organizerId") Long organizerId, @Param("customerId") Long customerId,
            @Param("examinationTypes") Collection<ExaminationType> examinationTypes,
            @Param("appointmentStatus") AppointmentStatus appointmentStatus, @Param("lowerTimeBoundary") LocalDateTime lowerTimeBoundary,
            @Param("upperTimeBoundary") LocalDateTime upperTimeBoundary, @Param("deleted") Boolean deleted);


    /**
     * Counts the total appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId    the organization of the parameters (can be null for any organization).
     * @param organizerId       who must resolve the appointment (can be null for any organizer).
     * @param examinationType   the type of the appointment (can be null for any type).
     * @param appointmentStatus the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted           the appointment is deleted or not.
     * @return the total number of appointments
     */
    @Query("""
            SELECT COUNT(a) FROM Appointment a WHERE
            (:organizationId IS NULL OR a.organizationId = :organizationId) AND
            (:organizerId IS NULL OR a.organizerId = :organizerId) AND
            (:examinationType IS NULL OR a.examinationType = :examinationType) AND
            (:appointmentStatus IS NULL OR a.status = :appointmentStatus) AND
            (((:lowerTimeBoundary IS NULL OR a.endTime >= :lowerTimeBoundary) AND
            (:upperTimeBoundary IS NULL OR a.startTime <= :upperTimeBoundary)) OR
            a.startTime IS NULL AND a.endTime IS NULL) AND
            (:deleted IS NULL OR a.deleted = :deleted)
            """)
    long count(
            @Param("organizationId") Long organizationId, @Param("organizerId") Long organizerId, @Param("examinationType") ExaminationType examinationType,
            @Param("appointmentStatus") AppointmentStatus appointmentStatus, @Param("lowerTimeBoundary") LocalDateTime lowerTimeBoundary,
            @Param("upperTimeBoundary") LocalDateTime upperTimeBoundary, @Param("deleted") Boolean deleted);


    /**
     * Counts the total appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId    the organization of the parameters (can be null for any organization).
     * @param organizerId       who must resolve the appointment (can be null for any organizer).
     * @param examinationTypes  a collection of the appointment (can be null for any type).
     * @param appointmentStatus the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted           the appointment is deleted or not.
     * @return the total number of appointments.
     */
    @Query("""
            SELECT COUNT(a) FROM Appointment a WHERE
            (:organizationId IS NULL OR a.organizationId = :organizationId) AND
            (:organizerId IS NULL OR a.organizerId = :organizerId) AND
            (:customerId IS NULL OR a.customerId = :customerId) AND
            (:examinationTypes IS NULL OR a.examinationType IN :examinationTypes) AND
            (:appointmentStatus IS NULL OR a.status = :appointmentStatus) AND
            (((:lowerTimeBoundary IS NULL OR a.endTime >= :lowerTimeBoundary) AND
            (:upperTimeBoundary IS NULL OR a.startTime <= :upperTimeBoundary)) OR
            a.startTime IS NULL AND a.endTime IS NULL) AND
            (:deleted IS NULL OR a.deleted = :deleted)
            """)
    long count(
            @Param("organizationId") Long organizationId, @Param("organizerId") Long organizerId, @Param("customerId") Long customerId,
            @Param("examinationTypes") Collection<ExaminationType> examinationTypes,
            @Param("appointmentStatus") AppointmentStatus appointmentStatus, @Param("lowerTimeBoundary") LocalDateTime lowerTimeBoundary,
            @Param("upperTimeBoundary") LocalDateTime upperTimeBoundary, @Param("deleted") Boolean deleted);


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
            (:#{#appointment.organizerId} IS NULL OR a.organizerId = :#{#appointment.organizerId}) AND
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

    /**
     * Counts how many appointments has a customer.
     *
     * @param customerId the customer id card.
     * @return the total number of appointments.
     */
    long countByCustomerId(Long customerId);


    /**
     * Gets the appointment that are previously set on time from an appointment.
     *
     * @param appointment the appointment as a reference for the search.
     * @return the list of appointments that are before the parameters on a descendent order of start time.
     */
    @Query("""
            SELECT a FROM Appointment a WHERE
            (:#{#appointment.organizationId} IS NULL OR a.organizationId = :#{#appointment.organizationId}) AND
            (:#{#appointment.customerId} IS NULL OR a.customerId = :#{#appointment.customerId}) AND
            (a.status <> com.biit.appointment.persistence.entities.AppointmentStatus.CANCELLED) AND
            (a.startTime < :#{#appointment.startTime}) AND
            (a.deleted = false)
            ORDER BY a.startTime DESC
            """)
    List<Appointment> getPrevious(@Param("appointment") Appointment appointment);

    /**
     * Gets the appointments from an organization that are set on time after a specific selected time.
     *
     * @param organizationId    the organization of the parameters (can be null for any organization).
     * @param examinationType   the type of the appointment (can be null for any type).
     * @param lowerTimeBoundary the minimum time when the appointments must start.
     * @return a list of appointments ordered ascendant by start time.
     */
    @Query("""
            SELECT a FROM Appointment a WHERE
            (:organizationId IS NULL OR a.organizationId = :organizationId) AND
            (:examinationType IS NULL OR a.examinationType = :examinationType) AND
            (a.status <> com.biit.appointment.persistence.entities.AppointmentStatus.CANCELLED) AND
            (:lowerTimeBoundary IS NULL OR a.startTime < :lowerTimeBoundary) AND
            (a.deleted = false)
            ORDER BY a.startTime ASC
            """)
    List<Appointment> getPrevious(@Param("organizationId") Long organizationId, @Param("examinationType") ExaminationType examinationType,
                              @Param("lowerTimeBoundary") LocalDateTime lowerTimeBoundary);


    /**
     * Gets the appointment that are afterward of the selected appointment.
     *
     * @param appointment the appointment as a reference for the search.
     * @return the list of appointments that are before the parameters on an ascendant order of the starting time.
     */
    @Query("""
            SELECT a FROM Appointment a WHERE
            (:#{#appointment.organizationId} IS NULL OR a.organizationId = :#{#appointment.organizationId}) AND
            (:#{#appointment.customerId} IS NULL OR a.customerId = :#{#appointment.customerId}) AND
            (a.status <> com.biit.appointment.persistence.entities.AppointmentStatus.CANCELLED) AND
            (a.startTime > :#{#appointment.startTime}) AND
            (a.deleted = false)
            ORDER BY a.startTime ASC
            """)
    List<Appointment> getNext(@Param("appointment") Appointment appointment);


    /**
     * Gets the appointments from an organization that are set on time after a specific selected time.
     *
     * @param organizationId    the organization of the parameters (can be null for any organization).
     * @param examinationType   the type of the appointment (can be null for any type).
     * @param lowerTimeBoundary the minimum time when the appointments must start.
     * @return a list of appointments ordered ascendant by start time.
     */
    @Query("""
            SELECT a FROM Appointment a WHERE
            (:organizationId IS NULL OR a.organizationId = :organizationId) AND
            (:examinationType IS NULL OR a.examinationType = :examinationType) AND
            (a.status <> com.biit.appointment.persistence.entities.AppointmentStatus.CANCELLED) AND
            (:lowerTimeBoundary IS NULL OR a.startTime >= :lowerTimeBoundary) AND
            (a.deleted = false)
            ORDER BY a.startTime ASC
            """)
    List<Appointment> getNext(@Param("organizationId") Long organizationId, @Param("examinationType") ExaminationType examinationType,
                              @Param("lowerTimeBoundary") LocalDateTime lowerTimeBoundary);

//    Appointment findTop1ByCustomerIdAndStartTimeLessThanAndStatusNotAndDeletedOrderByStartTimeDesc(
//    Long customerId, LocalDateTime startTime, AppointmentStatus status, Boolean deleted);
}
