package com.biit.appointment.core.providers;

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

import com.biit.appointment.core.exceptions.AppointmentTypeNotFoundException;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.repositories.AppointmentTypeRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

@Service
public class AppointmentTypeProvider extends ElementProvider<AppointmentType, Long, AppointmentTypeRepository> {

    public AppointmentTypeProvider(AppointmentTypeRepository repository) {
        super(repository);
    }

    public AppointmentType findByNameAndOrganizationId(String name, String organizationId) {
        return  getRepository().findByNameAndOrganizationId(name, organizationId).orElseThrow(() ->
                new AppointmentTypeNotFoundException(this.getClass(), "No appointment type found"));
    }
}
