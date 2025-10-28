package com.biit.appointment.core.converters;

/*-
 * #%L
 * AppointmentCenter (Core)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
