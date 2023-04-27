package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ExaminationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExaminationTypeRepository extends JpaRepository<ExaminationType, Long> {


    List<ExaminationType> findByNameAndDeleted(String name, boolean deleted);

    Optional<ExaminationType> findByNameAndOrganizationIdAndDeleted(String name, Long organizationId, boolean deleted);

    List<ExaminationType> findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(Long organizationId, AppointmentType appointmentType, boolean deleted);

    List<ExaminationType> findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(Long organizationId, Collection<AppointmentType> appointmentTypes, boolean deleted);
}
