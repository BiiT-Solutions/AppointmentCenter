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

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByOrganizerId(Long organizerId);

    @Query("SELECT a FROM Appointment a WHERE " +
            "(:organizationId IS NULL OR a.organizationId = :organizationId) AND " +
            "(:organizerId IS NULL OR a.organizerId = :organizerId) AND " +
            "(:examinationType IS NULL OR a.examinationType = :examinationType) AND " +
            "(:appointmentStatus IS NULL OR a.status = :appointmentStatus) AND " +
            "(:startTime IS NULL OR a.startTime = :startTime) AND " +
            "(:endTime IS NULL OR a.endTime = :endTime) AND " +
            "(:deleted IS NULL OR a.deleted = :deleted) ")
    List<Appointment> findByOrganizerIdAndOrganizerIdAndExaminationTypeAndAppointmentStatusAndStartTimeAndEndTimeAndDeleted(
            @Param("organizationId") Long organizationId, @Param("organizerId") Long organizerId, @Param("examinationType") ExaminationType examinationType,
            @Param("appointmentStatus") AppointmentStatus appointmentStatus, @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime, @Param("deleted") Boolean deleted);

    @Query("SELECT a FROM Appointment a WHERE " +
            "(:organizationId IS NULL OR a.organizationId = :organizationId) AND " +
            "(:organizerId IS NULL OR a.organizerId = :organizerId) AND " +
            "(:examinationTypes IS NULL OR a.examinationType IN :examinationTypes) AND " +
            "(:appointmentStatus IS NULL OR a.status = :appointmentStatus) AND " +
            "(:startTime IS NULL OR a.startTime = :startTime) AND " +
            "(:endTime IS NULL OR a.endTime = :endTime) AND " +
            "(:deleted IS NULL OR a.deleted = :deleted) ")
    long findByOrganizerIdAndOrganizerIdAndExaminationTypeAndAppointmentStatusAndStartTimeAndEndTimeAndDeleted(
            @Param("organizationId") Long organizationId, @Param("organizerId") Long organizerId, @Param("examinationTypes") ExaminationType[] examinationTypes,
            @Param("appointmentStatus") AppointmentStatus appointmentStatus, @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime, @Param("deleted") Boolean deleted);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE " +
            "(:organizationId IS NULL OR a.organizationId = :organizationId) AND " +
            "(:organizerId IS NULL OR a.organizerId = :organizerId) AND " +
            "(:examinationType IS NULL OR a.examinationType = :examinationType) AND " +
            "(:appointmentStatus IS NULL OR a.status = :appointmentStatus) AND " +
            "(:startTime IS NULL OR a.startTime = :startTime) AND " +
            "(:endTime IS NULL OR a.endTime = :endTime) AND " +
            "(:deleted IS NULL OR a.deleted = :deleted) ")
    long countByOrganizerIdAndOrganizerIdAndExaminationTypeAndAppointmentStatusAndStartTimeAndEndTimeAndDeleted(
            @Param("organizationId") Long organizationId, @Param("organizerId") Long organizerId, @Param("examinationType") ExaminationType examinationType,
            @Param("appointmentStatus") AppointmentStatus appointmentStatus, @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime, @Param("deleted") Boolean deleted);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE " +
            "(:organizationId IS NULL OR a.organizationId = :organizationId) AND " +
            "(:organizerId IS NULL OR a.organizerId = :organizerId) AND " +
            "(:examinationTypes IS NULL OR a.examinationType IN :examinationTypes) AND " +
            "(:appointmentStatus IS NULL OR a.status = :appointmentStatus) AND " +
            "(:startTime IS NULL OR a.startTime = :startTime) AND " +
            "(:endTime IS NULL OR a.endTime = :endTime) AND " +
            "(:deleted IS NULL OR a.deleted = :deleted) ")
    long countByOrganizerIdAndOrganizerIdAndExaminationTypeAndAppointmentStatusAndStartTimeAndEndTimeAndDeleted(
            @Param("organizationId") Long organizationId, @Param("organizerId") Long organizerId, @Param("examinationTypes") ExaminationType[] examinationTypes,
            @Param("appointmentStatus") AppointmentStatus appointmentStatus, @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime, @Param("deleted") Boolean deleted);

    long countByCustomerId(Long customerId);
}
