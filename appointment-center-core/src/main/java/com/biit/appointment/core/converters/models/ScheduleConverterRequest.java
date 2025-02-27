package com.biit.appointment.core.converters.models;

import com.biit.appointment.persistence.entities.Schedule;
import com.biit.server.converters.models.ConverterRequest;

public class ScheduleConverterRequest extends ConverterRequest<Schedule> {
    public ScheduleConverterRequest(Schedule schedule) {
        super(schedule);
    }
}
