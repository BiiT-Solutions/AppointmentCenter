package com.biit.appointment.core.providers;

import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.entities.Recurrence;
import com.biit.appointment.persistence.repositories.RecurrenceRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
public class RecurrenceProvider extends ElementProvider<Recurrence, Long, RecurrenceRepository> {

    public RecurrenceProvider(RecurrenceRepository repository) {
        super(repository);
    }

    public List<Appointment> findAll(Long organizationId, Long organizerId, Collection<ExaminationType> examinationType,
                                     LocalDateTime lowerTimeBoundary,
                                     LocalDateTime upperTimeBoundary) {
        return getRepository().findBy(organizationId, organizerId, examinationType, lowerTimeBoundary, upperTimeBoundary);
    }

}
