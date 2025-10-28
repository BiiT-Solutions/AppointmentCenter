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

import com.biit.appointment.core.converters.ProfessionalSpecializationConverter;
import com.biit.appointment.core.converters.models.ProfessionalSpecializationConverterRequest;
import com.biit.appointment.core.models.ProfessionalSpecializationDTO;
import com.biit.appointment.core.providers.AppointmentTypeProvider;
import com.biit.appointment.core.providers.ProfessionalSpecializationProvider;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ProfessionalSpecialization;
import com.biit.appointment.persistence.repositories.ProfessionalSpecializationRepository;
import com.biit.server.controller.ElementController;
import com.biit.server.security.IUserOrganizationProvider;
import com.biit.server.security.model.IUserOrganization;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Controller
public class ProfessionalSpecializationController extends ElementController<ProfessionalSpecialization, Long, ProfessionalSpecializationDTO,
        ProfessionalSpecializationRepository, ProfessionalSpecializationProvider, ProfessionalSpecializationConverterRequest,
        ProfessionalSpecializationConverter> {

    private final AppointmentTypeProvider appointmentTypeProvider;

    protected ProfessionalSpecializationController(ProfessionalSpecializationProvider provider, ProfessionalSpecializationConverter converter,
                                                   AppointmentTypeProvider appointmentTypeProvider,
                                                   List<IUserOrganizationProvider<? extends IUserOrganization>> userOrganizationProvider) {
        super(provider, converter, userOrganizationProvider);
        this.appointmentTypeProvider = appointmentTypeProvider;
    }

    @Override
    protected ProfessionalSpecializationConverterRequest createConverterRequest(ProfessionalSpecialization professionalSpecialization) {
        return new ProfessionalSpecializationConverterRequest(professionalSpecialization);
    }

    public List<ProfessionalSpecializationDTO> findByName(String name) {
        return convertAll(getProvider().findByName(name));
    }

    public ProfessionalSpecializationDTO findByNameAndOrganizationId(String name, String organizationId) {
        return convert(getProvider().findByNameAndOrganizationId(name, organizationId));
    }

    public List<ProfessionalSpecializationDTO> findAllByOrOrganizationIdAndAppointmentType(String organizationId, String appointmentTypeName) {
        return findAllByOrOrganizationIdAndAppointmentType(organizationId,
                appointmentTypeProvider.findByNameAndOrganizationId(appointmentTypeName, organizationId));
    }

    public List<ProfessionalSpecializationDTO> findAllByOrOrganizationIdAndAppointmentType(
            String organizationId, AppointmentType appointmentType) {
        return convertAll(getProvider().findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(organizationId, appointmentType));
    }

    public List<ProfessionalSpecializationDTO> findAllByOrOrganizationIdAndAppointmentTypeInUsingNames(
            String organizationId, Collection<String> appointmentTypeNames) {
        if (appointmentTypeNames != null) {
            final Set<AppointmentType> appointmentTypes = new HashSet<>();
            for (final String appointmentTypeName : appointmentTypeNames) {
                appointmentTypes.add(appointmentTypeProvider.findByNameAndOrganizationId(appointmentTypeName, organizationId));
            }
            return findAllByOrOrganizationIdAndAppointmentTypeIn(organizationId, appointmentTypes);
        } else {
            return findAllByOrOrganizationIdAndAppointmentTypeIn(organizationId, null);
        }
    }

    public List<ProfessionalSpecializationDTO> findAllByOrOrganizationIdAndAppointmentTypeIn(
            String organizationId, Collection<AppointmentType> appointmentTypes) {
        return convertAll(getProvider().findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(organizationId, appointmentTypes));
    }

    public List<ProfessionalSpecializationDTO> findByUserUUID(UUID userUUID) {
        return convertAll(getProvider().findByUserUUID(userUUID));
    }

    public List<ProfessionalSpecializationDTO> findByUserUUIDAndOrganizationId(UUID userUUID, String organizationId) {
        return convertAll(getProvider().findByUserUUIDAndOrganizationId(userUUID, organizationId));
    }

    public List<ProfessionalSpecializationDTO> findByUserUUID(Collection<UUID> usersUUIDs) {
        return convertAll(getProvider().findByUserUUID(usersUUIDs));
    }

    public List<ProfessionalSpecializationDTO> findByUserUUIDAndOrganizationId(Collection<UUID> usersUUIDs, String organizationId) {
        return convertAll(getProvider().findByUserUUIDAndOrganizationId(usersUUIDs, organizationId));
    }
}
