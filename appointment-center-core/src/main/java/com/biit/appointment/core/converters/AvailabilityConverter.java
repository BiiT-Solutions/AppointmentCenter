package com.biit.appointment.core.converters;

import com.biit.appointment.core.converters.models.AvailabilityConverterRequest;
import com.biit.appointment.core.converters.models.AvailabilityRangeConverterRequest;
import com.biit.appointment.core.models.AvailabilityDTO;
import com.biit.appointment.persistence.entities.Availability;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class AvailabilityConverter extends ElementConverter<Availability, AvailabilityDTO, AvailabilityConverterRequest> {

    private final AvailabilityRangeConverter availabilityRangeConverter;

    public AvailabilityConverter(AvailabilityRangeConverter availabilityRangeConverter) {
        this.availabilityRangeConverter = availabilityRangeConverter;
    }


    @Override
    protected AvailabilityDTO convertElement(AvailabilityConverterRequest from) {
        if (from.getEntity() == null) {
            return null;
        }
        final AvailabilityDTO availabilityDTO = new AvailabilityDTO();
        BeanUtils.copyProperties(from.getEntity(), availabilityDTO);
        from.getEntity().getRanges().forEach(availabilityRange ->
                availabilityDTO.addRange(availabilityRangeConverter.convertElement(new AvailabilityRangeConverterRequest(availabilityRange))));
        return availabilityDTO;
    }


    @Override
    public Availability reverse(AvailabilityDTO from) {
        if (from == null) {
            return null;
        }
        final Availability availability = new Availability();
        BeanUtils.copyProperties(from, availability);
        from.getRanges().forEach(availabilityRange ->
                availability.addRange(availabilityRangeConverter.reverse(availabilityRange)));
        return availability;
    }
}
