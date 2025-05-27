package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.Recurrence;
import com.biit.server.persistence.repositories.ElementRepository;


public interface RecurrenceRepository extends ElementRepository<Recurrence, Long>, CustomRecurrenceRepository {

}
