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
