package com.biit.appointment.core.controllers;

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

import com.biit.server.controller.ElementController;
import com.biit.appointment.core.converters.AppointmentTypeConverter;
import com.biit.appointment.core.converters.models.AppointmentTypeConverterRequest;
import com.biit.appointment.core.models.AppointmentTypeDTO;
import com.biit.appointment.core.providers.AppointmentTypeProvider;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.repositories.AppointmentTypeRepository;
import com.biit.server.security.IUserOrganizationProvider;
import com.biit.server.security.model.IUserOrganization;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AppointmentTypeController extends ElementController<AppointmentType, Long, AppointmentTypeDTO, AppointmentTypeRepository,
        AppointmentTypeProvider, AppointmentTypeConverterRequest, AppointmentTypeConverter> {

    protected AppointmentTypeController(AppointmentTypeProvider provider, AppointmentTypeConverter converter,
                                        List<IUserOrganizationProvider<? extends IUserOrganization>> userOrganizationProvider) {
        super(provider, converter, userOrganizationProvider);
    }

    @Override
    protected AppointmentTypeConverterRequest createConverterRequest(AppointmentType appointmentType) {
        return new AppointmentTypeConverterRequest(appointmentType);
    }

    public AppointmentTypeDTO findByNameAndOrganizationId(String name, String organizationId) {
        return convert(getProvider().findByNameAndOrganizationId(name, organizationId));
    }
}
