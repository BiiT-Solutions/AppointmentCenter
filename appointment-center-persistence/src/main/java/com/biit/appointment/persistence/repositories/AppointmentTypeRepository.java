package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.server.persistence.repositories.ElementRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppointmentTypeRepository extends ElementRepository<AppointmentType, Long> {

    Optional<AppointmentType> findByNameAndOrganizationId(String name, Long organizationId);
}
