package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.Schedule;
import com.biit.server.persistence.repositories.ElementRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends ElementRepository<Schedule, Long> {

    Optional<Schedule> findByUser(UUID userId);
}
