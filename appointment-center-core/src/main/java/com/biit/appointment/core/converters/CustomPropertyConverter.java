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


import com.biit.appointment.core.converters.models.CustomPropertyConverterRequest;
import com.biit.appointment.core.models.CustomPropertyDTO;
import com.biit.appointment.persistence.entities.CustomProperty;
import com.biit.server.controller.converters.SimpleConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class CustomPropertyConverter extends SimpleConverter<CustomProperty, CustomPropertyDTO, CustomPropertyConverterRequest> {


    @Override
    protected CustomPropertyDTO convertElement(CustomPropertyConverterRequest from) {
        final CustomPropertyDTO customPropertyDTO = new CustomPropertyDTO();
        BeanUtils.copyProperties(from.getEntity(), customPropertyDTO);
        return customPropertyDTO;
    }

    @Override
    public CustomProperty reverse(CustomPropertyDTO to) {
        final CustomProperty customProperty = new CustomProperty();
        BeanUtils.copyProperties(to, customProperty);
        return customProperty;
    }
}
