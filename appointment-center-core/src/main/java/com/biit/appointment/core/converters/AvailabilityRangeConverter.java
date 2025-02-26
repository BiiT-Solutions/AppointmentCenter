package com.biit.appointment.core.converters;

import com.biit.appointment.core.converters.models.AvailabilityRangeConverterRequest;
import com.biit.appointment.core.models.AvailabilityRangeDTO;
import com.biit.appointment.persistence.entities.AvailabilityRange;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class AvailabilityRangeConverter extends ElementConverter<AvailabilityRange, AvailabilityRangeDTO, AvailabilityRangeConverterRequest> {

    @Override
    protected AvailabilityRangeDTO convertElement(AvailabilityRangeConverterRequest from) {
        if (from.getEntity() == null) {
            return null;
        }
        final AvailabilityRangeDTO availabilityRangeDTO = new AvailabilityRangeDTO();
        BeanUtils.copyProperties(from.getEntity(), availabilityRangeDTO);
        return availabilityRangeDTO;
    }

    @Override
    public AvailabilityRange reverse(AvailabilityRangeDTO to) {
        if (to == null) {
            return null;
        }
        final AvailabilityRange availabilityRange = new AvailabilityRange();
        BeanUtils.copyProperties(to, availabilityRange);
        return availabilityRange;
    }
}
