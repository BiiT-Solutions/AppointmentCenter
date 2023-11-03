package com.biit.appointment.core.controllers;

import com.biit.appointment.core.converters.RecurrenceConverter;
import com.biit.appointment.core.converters.models.RecurrenceConverterRequest;
import com.biit.appointment.core.models.RecurrenceDTO;
import com.biit.appointment.core.providers.RecurrenceProvider;
import com.biit.appointment.persistence.entities.Recurrence;
import com.biit.appointment.persistence.repositories.RecurrenceRepository;
import com.biit.server.controller.ElementController;
import org.springframework.stereotype.Controller;

@Controller
public class RecurrenceController extends ElementController<Recurrence, Long, RecurrenceDTO, RecurrenceRepository,
        RecurrenceProvider, RecurrenceConverterRequest, RecurrenceConverter> {


    protected RecurrenceController(RecurrenceProvider provider, RecurrenceConverter converter) {
        super(provider, converter);
    }

    @Override
    protected RecurrenceConverterRequest createConverterRequest(Recurrence recurrence) {
        return new RecurrenceConverterRequest(recurrence);
    }


}
