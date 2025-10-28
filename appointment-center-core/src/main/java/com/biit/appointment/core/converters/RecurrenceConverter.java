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

import com.biit.appointment.core.converters.models.AppointmentConverterRequest;
import com.biit.appointment.core.converters.models.ExaminationTypeConverterRequest;
import com.biit.appointment.core.converters.models.RecurrenceConverterRequest;
import com.biit.appointment.core.models.RecurrenceDTO;
import com.biit.appointment.persistence.entities.Recurrence;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;

@Component
public class RecurrenceConverter extends ElementConverter<Recurrence, RecurrenceDTO, RecurrenceConverterRequest> {

    private final AppointmentConverter appointmentConverter;
    private final ExaminationTypeConverter examinationTypeConverter;

    public RecurrenceConverter(AppointmentConverter appointmentConverter,
                               ExaminationTypeConverter examinationTypeConverter) {
        this.appointmentConverter = appointmentConverter;
        this.examinationTypeConverter = examinationTypeConverter;
    }

    @Override
    protected RecurrenceDTO convertElement(RecurrenceConverterRequest from) {
        if (from.getEntity() == null) {
            return null;
        }
        final RecurrenceDTO recurrenceDTO = new RecurrenceDTO();
        BeanUtils.copyProperties(from.getEntity(), recurrenceDTO);
        recurrenceDTO.setAppointments(new HashSet<>(appointmentConverter.convertAll(from.getEntity().getAppointments().stream()
                .map(AppointmentConverterRequest::new).toList())));
        recurrenceDTO.setExaminationType(examinationTypeConverter.convertElement(new ExaminationTypeConverterRequest(from.getEntity().getExaminationType())));
        return recurrenceDTO;
    }

    @Override
    public Recurrence reverse(RecurrenceDTO to) {
        if (to == null) {
            return null;
        }
        final Recurrence recurrence = new Recurrence();
        BeanUtils.copyProperties(to, recurrence);
        recurrence.setAppointments(new ArrayList<>(appointmentConverter.reverseAll(to.getAppointments())));
        recurrence.setExaminationType(examinationTypeConverter.reverse(to.getExaminationType()));
        return recurrence;
    }
}
