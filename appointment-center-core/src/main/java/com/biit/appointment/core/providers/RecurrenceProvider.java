package com.biit.appointment.core.providers;

import com.biit.appointment.persistence.entities.Recurrence;
import com.biit.appointment.persistence.repositories.RecurrenceRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

@Service
public class RecurrenceProvider extends ElementProvider<Recurrence, Long, RecurrenceRepository> {

    public RecurrenceProvider(RecurrenceRepository repository) {
        super(repository);
    }

}
