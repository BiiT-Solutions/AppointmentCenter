package com.biit.appointment.core.controllers;

import com.biit.appointment.core.controllers.kafka.AvailabilityEventSender;
import com.biit.appointment.core.converters.AvailabilityConverter;
import com.biit.appointment.core.converters.AvailabilityRangeConverter;
import com.biit.appointment.core.converters.models.AvailabilityConverterRequest;
import com.biit.appointment.core.models.AvailabilityDTO;
import com.biit.appointment.core.models.AvailabilityRangeDTO;
import com.biit.appointment.core.providers.AvailabilityProvider;
import com.biit.appointment.logger.AppointmentCenterLogger;
import com.biit.appointment.persistence.entities.Availability;
import com.biit.appointment.persistence.repositories.AvailabilityRepository;
import com.biit.kafka.controllers.KafkaElementController;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.UUID;

@Controller
public class AvailabilityController extends KafkaElementController<Availability, Long, AvailabilityDTO, AvailabilityRepository,
        AvailabilityProvider, AvailabilityConverterRequest, AvailabilityConverter> {

    private final AvailabilityRangeConverter availabilityRangeConverter;

    protected AvailabilityController(AvailabilityProvider provider, AvailabilityConverter converter,
                                     AvailabilityEventSender eventSender, AvailabilityRangeConverter availabilityRangeConverter) {
        super(provider, converter, eventSender);
        this.availabilityRangeConverter = availabilityRangeConverter;
    }

    @Override
    protected AvailabilityConverterRequest createConverterRequest(Availability availability) {
        return new AvailabilityConverterRequest(availability);
    }


    public AvailabilityDTO add(Collection<AvailabilityRangeDTO> availabilityRanges, UUID user, String createdBy) {
        AppointmentCenterLogger.info(this.getClass(), "User '{}' has added availabilities '{}' to user '{}'.",
                createdBy, availabilityRanges, user);
        return convert(getProvider().add(availabilityRangeConverter.reverseAll(availabilityRanges), user));
    }


    public AvailabilityDTO add(AvailabilityRangeDTO availabilityRange, UUID user, String createdBy) {
        AppointmentCenterLogger.info(this.getClass(), "User '{}' has added availability '{}' to user '{}'.",
                createdBy, availabilityRange, user);
        return convert(getProvider().add(availabilityRangeConverter.reverse(availabilityRange), user));
    }


    public AvailabilityDTO remove(AvailabilityRangeDTO availabilityRange, UUID user, String createdBy) {
        AppointmentCenterLogger.info(this.getClass(), "User '{}' has removed availability '{}' to user '{}'.",
                createdBy, availabilityRange, user);
        return convert(getProvider().remove(availabilityRangeConverter.reverse(availabilityRange), user));
    }
}
