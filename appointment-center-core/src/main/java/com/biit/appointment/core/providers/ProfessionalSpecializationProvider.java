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

import com.biit.appointment.core.exceptions.ProfessionalSpecializationNotFoundException;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ProfessionalSpecialization;
import com.biit.appointment.persistence.repositories.ProfessionalSpecializationRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
public class ProfessionalSpecializationProvider extends ElementProvider<ProfessionalSpecialization, Long, ProfessionalSpecializationRepository> {

    public ProfessionalSpecializationProvider(ProfessionalSpecializationRepository repository) {
        super(repository);
    }

    public List<ProfessionalSpecialization> findByName(String name) {
        return getRepository().findByName(name);
    }

    public List<ProfessionalSpecialization> findByNameAndDeleted(Collection<String> names) {
        return getRepository().findByNameIn(names);
    }

    public ProfessionalSpecialization findByNameAndOrganizationId(String name, String organizationId) {
        return getRepository().findByNameAndOrganizationId(name, organizationId).orElseThrow(() ->
                new ProfessionalSpecializationNotFoundException(this.getClass(), "No specialization defined with name '" + name + "' and organization '"
                        + organizationId + "'"));
    }

    public List<ProfessionalSpecialization> findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(
            String organizationId, AppointmentType appointmentType) {
        return getRepository().findByOrganizationIdAndAppointmentType(organizationId, appointmentType);
    }

    public List<ProfessionalSpecialization> findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(
            String organizationId, Collection<AppointmentType> appointmentTypes) {
        return getRepository().findByOrganizationIdAndAppointmentTypeIn(organizationId, appointmentTypes);
    }

    public List<ProfessionalSpecialization> findByUserUUID(UUID userUUID) {
        return getRepository().findByUser(userUUID);
    }

    public List<ProfessionalSpecialization> findByUserUUIDAndOrganizationId(UUID userUUID, String organizationId) {
        return getRepository().findByUserAndOrganizationId(userUUID, organizationId);
    }

    public List<ProfessionalSpecialization> findByUserUUID(Collection<UUID> userUUIDs) {
        return getRepository().findByUserIn(userUUIDs);
    }

    public List<ProfessionalSpecialization> findByUserUUIDAndOrganizationId(Collection<UUID> userUUIDs, String organizationId) {
        return getRepository().findByUserInAndOrganizationId(userUUIDs, organizationId);
    }
}
