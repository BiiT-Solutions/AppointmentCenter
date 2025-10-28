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

import com.biit.appointment.core.converters.models.AppointmentTemplateConverterRequest;
import com.biit.appointment.core.converters.models.ExaminationTypeConverterRequest;
import com.biit.appointment.core.models.AppointmentTemplateDTO;
import com.biit.appointment.persistence.entities.AppointmentTemplate;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class AppointmentTemplateConverter extends ElementConverter<AppointmentTemplate, AppointmentTemplateDTO, AppointmentTemplateConverterRequest> {

    private final ExaminationTypeConverter examinationTypeConverter;


    public AppointmentTemplateConverter(ExaminationTypeConverter examinationTypeConverter) {
        this.examinationTypeConverter = examinationTypeConverter;
    }

    @Override
    protected AppointmentTemplateDTO convertElement(AppointmentTemplateConverterRequest from) {
        if (from.getEntity() == null) {
            return null;
        }
        final AppointmentTemplateDTO appointmentDTO = new AppointmentTemplateDTO();
        BeanUtils.copyProperties(from.getEntity(), appointmentDTO);
        appointmentDTO.setExaminationType(examinationTypeConverter.convert(new ExaminationTypeConverterRequest(from.getEntity().getExaminationType())));
        if (from.getEntity().getSpeakers() != null) {
            appointmentDTO.setSpeakers(new HashSet<>(from.getEntity().getSpeakers()));
        }
        return appointmentDTO;
    }

    @Override
    public AppointmentTemplate reverse(AppointmentTemplateDTO to) {
        if (to == null) {
            return null;
        }
        final AppointmentTemplate appointmentTemplate = new AppointmentTemplate();
        BeanUtils.copyProperties(to, appointmentTemplate);
        appointmentTemplate.setExaminationType(examinationTypeConverter.reverse(to.getExaminationType()));
        if (to.getSpeakers() != null) {
            appointmentTemplate.setSpeakers(new HashSet<>(to.getSpeakers()));
        }
        return appointmentTemplate;
    }
}
