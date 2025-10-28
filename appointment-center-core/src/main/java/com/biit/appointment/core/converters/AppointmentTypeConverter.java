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
import com.biit.appointment.core.models.AppointmentTypeDTO;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class AppointmentTypeConverter extends ElementConverter<AppointmentType, AppointmentTypeDTO, AppointmentTypeConverterRequest> {

    @Override
    protected AppointmentTypeDTO convertElement(AppointmentTypeConverterRequest from) {
        final AppointmentTypeDTO appointmentTypeDTO = new AppointmentTypeDTO();
        BeanUtils.copyProperties(from.getEntity(), appointmentTypeDTO);
        return appointmentTypeDTO;
    }

    @Override
    public AppointmentType reverse(AppointmentTypeDTO to) {
        if (to == null) {
            return null;
        }
        final AppointmentType appointmentType = new AppointmentType();
        BeanUtils.copyProperties(to, appointmentType);
        return appointmentType;
    }
}
