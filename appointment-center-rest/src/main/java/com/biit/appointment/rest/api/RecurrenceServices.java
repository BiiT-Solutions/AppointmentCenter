package com.biit.appointment.rest.api;

import com.biit.appointment.core.controllers.RecurrenceController;
import com.biit.appointment.core.converters.RecurrenceConverter;
import com.biit.appointment.core.converters.models.RecurrenceConverterRequest;
import com.biit.appointment.core.models.RecurrenceDTO;
import com.biit.appointment.core.providers.RecurrenceProvider;
import com.biit.appointment.persistence.entities.Recurrence;
import com.biit.appointment.persistence.repositories.RecurrenceRepository;
import com.biit.server.rest.ElementServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recurrence")
public class RecurrenceServices extends ElementServices<Recurrence, Long, RecurrenceDTO, RecurrenceRepository,
        RecurrenceProvider, RecurrenceConverterRequest, RecurrenceConverter, RecurrenceController> {

    public RecurrenceServices(RecurrenceController controller) {
        super(controller);
    }

}
