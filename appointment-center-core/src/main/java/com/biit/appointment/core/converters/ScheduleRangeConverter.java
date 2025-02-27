package com.biit.appointment.core.converters;

import com.biit.appointment.core.converters.models.ScheduleRangeConverterRequest;
import com.biit.appointment.core.models.ScheduleRangeDTO;
import com.biit.appointment.persistence.entities.ScheduleRange;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ScheduleRangeConverter extends ElementConverter<ScheduleRange, ScheduleRangeDTO, ScheduleRangeConverterRequest> {

    @Override
    protected ScheduleRangeDTO convertElement(ScheduleRangeConverterRequest from) {
        if (from.getEntity() == null) {
            return null;
        }
        final ScheduleRangeDTO scheduleRangeDTO = new ScheduleRangeDTO();
        BeanUtils.copyProperties(from.getEntity(), scheduleRangeDTO);
        return scheduleRangeDTO;
    }

    @Override
    public ScheduleRange reverse(ScheduleRangeDTO to) {
        if (to == null) {
            return null;
        }
        final ScheduleRange scheduleRange = new ScheduleRange();
        BeanUtils.copyProperties(to, scheduleRange);
        return scheduleRange;
    }
}
