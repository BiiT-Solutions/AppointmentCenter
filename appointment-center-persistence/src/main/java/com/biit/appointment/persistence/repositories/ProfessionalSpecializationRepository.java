package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ProfessionalSpecialization;
import com.biit.server.persistence.repositories.ElementRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessionalSpecializationRepository extends ElementRepository<ProfessionalSpecialization, Long> {


    List<ProfessionalSpecialization> findByName(String name);

    List<ProfessionalSpecialization> findByNameIn(Collection<String> name);

    Optional<ProfessionalSpecialization> findByNameAndOrganizationId(String name, Long organizationId);

    List<ProfessionalSpecialization> findByOrganizationIdAndAppointmentType(Long organizationId, AppointmentType appointmentType);

    List<ProfessionalSpecialization> findByOrganizationIdAndAppointmentTypeIn(
            Long organizationId, Collection<AppointmentType> appointmentTypes);

    List<ProfessionalSpecialization> findByUserId(Long userId);

    List<ProfessionalSpecialization> findByUserIdAndOrganizationId(Long userId, Long organizationId);

    List<ProfessionalSpecialization> findByUserIdIn(Collection<Long> userIds);

    List<ProfessionalSpecialization> findByUserIdInAndOrganizationId(Collection<Long> userIds, Long organizationId);
}
