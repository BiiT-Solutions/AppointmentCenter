package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ProfessionalSpecialization;
import com.biit.server.persistence.repositories.ElementRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfessionalSpecializationRepository extends ElementRepository<ProfessionalSpecialization, Long> {


    List<ProfessionalSpecialization> findByName(String name);

    List<ProfessionalSpecialization> findByNameIn(Collection<String> name);

    Optional<ProfessionalSpecialization> findByNameAndOrganizationId(String name, String organizationId);

    List<ProfessionalSpecialization> findByOrganizationIdAndAppointmentType(String organizationId, AppointmentType appointmentType);

    List<ProfessionalSpecialization> findByOrganizationIdAndAppointmentTypeIn(
            String organizationId, Collection<AppointmentType> appointmentTypes);

    List<ProfessionalSpecialization> findByUser(UUID userUUID);

    List<ProfessionalSpecialization> findByUserAndOrganizationId(UUID userUUID, String organizationId);

    List<ProfessionalSpecialization> findByUserIn(Collection<UUID> userIds);

    List<ProfessionalSpecialization> findByUserInAndOrganizationId(Collection<UUID> userUUIDs, String organizationId);
}
