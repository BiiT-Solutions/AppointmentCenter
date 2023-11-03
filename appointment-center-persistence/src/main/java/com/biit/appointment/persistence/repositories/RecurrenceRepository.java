package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.Recurrence;
import com.biit.server.persistence.repositories.ElementRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurrenceRepository extends ElementRepository<Recurrence, Long> {


}
