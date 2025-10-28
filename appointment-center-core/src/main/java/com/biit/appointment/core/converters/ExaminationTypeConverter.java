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

import com.biit.appointment.core.converters.models.AppointmentTypeConverterRequest;
import com.biit.appointment.core.converters.models.ExaminationTypeConverterRequest;
import com.biit.appointment.core.models.ExaminationTypeDTO;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ExaminationTypeConverter extends ElementConverter<ExaminationType, ExaminationTypeDTO, ExaminationTypeConverterRequest> {

    private final AppointmentTypeConverter appointmentTypeConverter;

    public ExaminationTypeConverter(AppointmentTypeConverter appointmentTypeConverter) {
        this.appointmentTypeConverter = appointmentTypeConverter;
    }

    @Override
    protected ExaminationTypeDTO convertElement(ExaminationTypeConverterRequest from) {
        final ExaminationTypeDTO examinationTypeDTO = new ExaminationTypeDTO();
        if (from.getEntity() != null) {
            BeanUtils.copyProperties(from.getEntity(), examinationTypeDTO);
            examinationTypeDTO.setAppointmentType(appointmentTypeConverter.convert(new AppointmentTypeConverterRequest(from.getEntity().getAppointmentType())));
        }
        return examinationTypeDTO;
    }

    @Override
    public ExaminationType reverse(ExaminationTypeDTO to) {
        if (to == null) {
            return null;
        }
        final ExaminationType examinationType = new ExaminationType();
        BeanUtils.copyProperties(to, examinationType);
        examinationType.setAppointmentType(appointmentTypeConverter.reverse(to.getAppointmentType()));
        return examinationType;
    }
}
