package com.biit.appointment.core.converters.models;

import com.biit.appointment.persistence.entities.ScheduleRangeExclusion;
import com.biit.server.converters.models.ConverterRequest;

public class ScheduleRangeExclusionConverterRequest extends ConverterRequest<ScheduleRangeExclusion> {
    public ScheduleRangeExclusionConverterRequest(ScheduleRangeExclusion scheduleRange) {
        super(scheduleRange);
    }
}
