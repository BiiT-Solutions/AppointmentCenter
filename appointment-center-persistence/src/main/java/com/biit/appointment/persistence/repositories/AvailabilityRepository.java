package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.Availability;
import com.biit.server.persistence.repositories.ElementRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvailabilityRepository extends ElementRepository<Availability, Long> {

    Optional<Availability> findByUser(UUID userId);
}
