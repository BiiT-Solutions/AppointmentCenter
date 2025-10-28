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

import com.biit.appointment.core.exceptions.ExaminationTypeNotFoundException;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.repositories.ExaminationTypeRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ExaminationTypeProvider extends ElementProvider<ExaminationType, String, ExaminationTypeRepository> {

    public ExaminationTypeProvider(ExaminationTypeRepository repository) {
        super(repository);
    }

    public List<ExaminationType> findByNameAndDeleted(String name, boolean deleted) {
        return getRepository().findByNameAndDeleted(name, deleted);
    }

    public List<ExaminationType> findByNameAndDeleted(Collection<String> names, boolean deleted) {
        return getRepository().findByNameInAndDeleted(names == null ? new ArrayList<>() : names, deleted);
    }

    public ExaminationType findByNameAndOrganizationId(String name, String organizationId, boolean deleted) {
        return getRepository().findByNameAndOrganizationIdAndDeleted(name, organizationId, deleted).orElseThrow(() ->
                new ExaminationTypeNotFoundException(this.getClass(), "No examination defined with name '" + name + "' and organization '"
                        + organizationId + "'"));
    }

    public List<ExaminationType> findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(
            String organizationId, AppointmentType appointmentType, boolean deleted) {
        return getRepository().findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(organizationId, appointmentType, deleted);
    }

    public List<ExaminationType> findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(
            String organizationId, Collection<AppointmentType> appointmentTypes, boolean deleted) {
        return getRepository().findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(organizationId, appointmentTypes, deleted);
    }
}
