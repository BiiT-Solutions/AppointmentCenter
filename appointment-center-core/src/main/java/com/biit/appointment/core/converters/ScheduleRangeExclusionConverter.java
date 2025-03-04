package com.biit.appointment.core.converters;

import com.biit.appointment.core.converters.models.ScheduleRangeExclusionConverterRequest;
import com.biit.appointment.core.models.ScheduleRangeExclusionDTO;
import com.biit.appointment.persistence.entities.ScheduleRangeExclusion;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ScheduleRangeExclusionConverter extends ElementConverter<ScheduleRangeExclusion, ScheduleRangeExclusionDTO,
        ScheduleRangeExclusionConverterRequest> {

    @Override
    protected ScheduleRangeExclusionDTO convertElement(ScheduleRangeExclusionConverterRequest from) {
        if (from.getEntity() == null) {
            return null;
        }
        final ScheduleRangeExclusionDTO scheduleRangeExclusionDTO = new ScheduleRangeExclusionDTO();
        BeanUtils.copyProperties(from.getEntity(), scheduleRangeExclusionDTO);
        return scheduleRangeExclusionDTO;
    }


    @Override
    public ScheduleRangeExclusion reverse(ScheduleRangeExclusionDTO from) {
        if (from == null) {
            return null;
        }
        final ScheduleRangeExclusion scheduleRangeExclusion = new ScheduleRangeExclusion();
        BeanUtils.copyProperties(from, scheduleRangeExclusion);
        return scheduleRangeExclusion;
    }
}
