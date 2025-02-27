package com.biit.appointment.core.converters;

import com.biit.appointment.core.converters.models.ScheduleConverterRequest;
import com.biit.appointment.core.converters.models.ScheduleRangeConverterRequest;
import com.biit.appointment.core.models.ScheduleDTO;
import com.biit.appointment.persistence.entities.Schedule;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ScheduleConverter extends ElementConverter<Schedule, ScheduleDTO, ScheduleConverterRequest> {

    private final ScheduleRangeConverter scheduleRangeConverter;

    public ScheduleConverter(ScheduleRangeConverter scheduleRangeConverter) {
        this.scheduleRangeConverter = scheduleRangeConverter;
    }


    @Override
    protected ScheduleDTO convertElement(ScheduleConverterRequest from) {
        if (from.getEntity() == null) {
            return null;
        }
        final ScheduleDTO scheduleDTO = new ScheduleDTO();
        BeanUtils.copyProperties(from.getEntity(), scheduleDTO);
        from.getEntity().getRanges().forEach(scheduleRange ->
                scheduleDTO.addRange(scheduleRangeConverter.convertElement(new ScheduleRangeConverterRequest(scheduleRange))));
        return scheduleDTO;
    }


    @Override
    public Schedule reverse(ScheduleDTO from) {
        if (from == null) {
            return null;
        }
        final Schedule schedule = new Schedule();
        BeanUtils.copyProperties(from, schedule);
        from.getRanges().forEach(scheduleRange ->
                schedule.addRange(scheduleRangeConverter.reverse(scheduleRange)));
        return schedule;
    }
}
