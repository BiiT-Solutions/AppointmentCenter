package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentStatus;
import com.biit.appointment.persistence.entities.ExaminationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByOrganizerId(Long organizerId);

    @Query("""
            SELECT a FROM Appointment a WHERE 
            (:organizationId IS NULL OR a.organizationId = :organizationId) AND 
            (:organizerId IS NULL OR a.organizerId = :organizerId) AND 
            (:examinationType IS NULL OR a.examinationType = :examinationType) AND 
            (:appointmentStatus IS NULL OR a.status = :appointmentStatus) AND 
            (((:startTime IS NULL OR a.endTime >= :startTime) AND 
            (:endTime IS NULL OR a.startTime <= :endTime)) OR 
            a.startTime IS NULL AND a.endTime IS NULL) AND 
            (:deleted IS NULL OR a.deleted = :deleted) 
            """)
    List<Appointment> findAll(
            @Param("organizationId") Long organizationId, @Param("organizerId") Long organizerId, @Param("examinationType") ExaminationType examinationType,
            @Param("appointmentStatus") AppointmentStatus appointmentStatus, @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime, @Param("deleted") Boolean deleted);

    @Query("""
            SELECT a FROM Appointment a WHERE 
            (:organizationId IS NULL OR a.organizationId = :organizationId) AND 
            (:organizerId IS NULL OR a.organizerId = :organizerId) AND 
            (:examinationTypes IS NULL OR a.examinationType IN :examinationTypes) AND 
            (:appointmentStatus IS NULL OR a.status = :appointmentStatus) AND 
            (((:startTime IS NULL OR a.endTime >= :startTime) AND 
            (:endTime IS NULL OR a.startTime <= :endTime)) OR 
            a.startTime IS NULL AND a.endTime IS NULL) AND 
            (:deleted IS NULL OR a.deleted = :deleted) 
            """)
    Optional<Appointment> findBy(
            @Param("organizationId") Long organizationId, @Param("organizerId") Long organizerId, @Param("examinationTypes") List<ExaminationType> examinationTypes,
            @Param("appointmentStatus") AppointmentStatus appointmentStatus, @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime, @Param("deleted") Boolean deleted);

    @Query("""
            SELECT COUNT(a) FROM Appointment a WHERE 
            (:organizationId IS NULL OR a.organizationId = :organizationId) AND 
            (:organizerId IS NULL OR a.organizerId = :organizerId) AND 
            (:examinationType IS NULL OR a.examinationType = :examinationType) AND 
            (:appointmentStatus IS NULL OR a.status = :appointmentStatus) AND 
            (((:startTime IS NULL OR a.endTime >= :startTime) AND 
            (:endTime IS NULL OR a.startTime <= :endTime)) OR 
            a.startTime IS NULL AND a.endTime IS NULL) AND 
            (:deleted IS NULL OR a.deleted = :deleted)
            """)
    long count(
            @Param("organizationId") Long organizationId, @Param("organizerId") Long organizerId, @Param("examinationType") ExaminationType examinationType,
            @Param("appointmentStatus") AppointmentStatus appointmentStatus, @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime, @Param("deleted") Boolean deleted);

    @Query("""
            SELECT COUNT(a) FROM Appointment a WHERE
            (:organizationId IS NULL OR a.organizationId = :organizationId) AND 
            (:organizerId IS NULL OR a.organizerId = :organizerId) AND 
            (:examinationTypes IS NULL OR a.examinationType IN :examinationTypes) AND 
            (:appointmentStatus IS NULL OR a.status = :appointmentStatus) AND 
            (((:startTime IS NULL OR a.endTime >= :startTime) AND 
            (:endTime IS NULL OR a.startTime <= :endTime)) OR 
            a.startTime IS NULL AND a.endTime IS NULL) AND 
            (:deleted IS NULL OR a.deleted = :deleted) 
            """)
    long count(
            @Param("organizationId") Long organizationId, @Param("organizerId") Long organizerId, @Param("examinationTypes") List<ExaminationType> examinationTypes,
            @Param("appointmentStatus") AppointmentStatus appointmentStatus, @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime, @Param("deleted") Boolean deleted);

    @Query("""
            SELECT COUNT(a) FROM Appointment a WHERE
            (:#{#appointment.id} IS NULL OR a.id != :#{#appointment.id}) AND 
            (:#{#appointment.organizationId} IS NULL OR a.organizationId = :#{#appointment.organizationId}) AND 
            (:#{#appointment.organizerId} IS NULL OR a.organizerId = :#{#appointment.organizerId}) AND 
            (a.status != com.biit.appointment.persistence.entities.AppointmentStatus.CANCELLED) AND
            (:#{#appointment.examinationType.appointmentOverlapsAllowed} IS false OR a.examinationType.appointmentOverlapsAllowed IS false) AND 
            (
                (a.startTime >= :#{#appointment.startTime} AND a.startTime < :#{#appointment.endTime}) OR
                (a.endTime > :#{#appointment.startTime} AND a.endTime < :#{#appointment.endTime}) OR
                (a.startTime <= :#{#appointment.startTime} AND a.endTime >= :#{#appointment.endTime})
            ) AND  
            (a.deleted = false) 
            """)
    long overlaps(@Param("appointment") Appointment appointment);

    long countByCustomerId(Long customerId);


    @Query("""
            SELECT a FROM Appointment a WHERE
            (:#{#appointment.organizationId} IS NULL OR a.organizationId = :#{#appointment.organizationId}) AND 
            (:#{#appointment.customerId} IS NULL OR a.customerId = :#{#appointment.customerId}) AND 
            (a.status != com.biit.appointment.persistence.entities.AppointmentStatus.CANCELLED) AND
            (a.startTime < :#{#appointment.startTime}) AND
            (a.deleted = false) 
            ORDER BY a.startTime DESC
            """)
    List<Appointment> getPrevious(@Param("appointment") Appointment appointment);

    @Query("""
            SELECT a FROM Appointment a WHERE
            (:#{#appointment.organizationId} IS NULL OR a.organizationId = :#{#appointment.organizationId}) AND 
            (:#{#appointment.customerId} IS NULL OR a.customerId = :#{#appointment.customerId}) AND 
            (a.status != com.biit.appointment.persistence.entities.AppointmentStatus.CANCELLED) AND
            (a.startTime > :#{#appointment.startTime}) AND
            (a.deleted = false) 
            ORDER BY a.startTime ASC
            """)
    List<Appointment> getNext(@Param("appointment") Appointment appointment);

    @Query("""
            SELECT a FROM Appointment a WHERE
            (:organizationId IS NULL OR a.organizationId = :organizationId) AND
            (:examinationType IS NULL OR a.examinationType = :examinationType) AND
            (a.status != com.biit.appointment.persistence.entities.AppointmentStatus.CANCELLED) AND
            (a.deleted = false) 
            ORDER BY a.startTime ASC
            """)
    List<Appointment> getNext(@Param("organizationId") Long organizationId, @Param("examinationType") ExaminationType examinationType);

//    Appointment findTop1ByCustomerIdAndStartTimeLessThanAndStatusNotAndDeletedOrderByStartTimeDesc(Long customerId, LocalDateTime startTime, AppointmentStatus status, Boolean deleted);
}
