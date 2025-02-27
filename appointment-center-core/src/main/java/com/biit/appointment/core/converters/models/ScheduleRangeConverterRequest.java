package com.biit.appointment.core.converters.models;

import com.biit.appointment.persistence.entities.ScheduleRange;
import com.biit.server.converters.models.ConverterRequest;

public class ScheduleRangeConverterRequest extends ConverterRequest<ScheduleRange> {
    public ScheduleRangeConverterRequest(ScheduleRange scheduleRange) {
        super(scheduleRange);
    }
}
