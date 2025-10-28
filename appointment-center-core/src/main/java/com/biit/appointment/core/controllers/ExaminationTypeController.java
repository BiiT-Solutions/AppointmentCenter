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

import com.biit.appointment.core.converters.ExaminationTypeConverter;
import com.biit.appointment.core.converters.models.ExaminationTypeConverterRequest;
import com.biit.appointment.core.models.ExaminationTypeDTO;
import com.biit.appointment.core.providers.AppointmentTypeProvider;
import com.biit.appointment.core.providers.ExaminationTypeProvider;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.repositories.ExaminationTypeRepository;
import com.biit.server.controller.ElementController;
import com.biit.server.security.IUserOrganizationProvider;
import com.biit.server.security.model.IUserOrganization;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class ExaminationTypeController extends ElementController<ExaminationType, String, ExaminationTypeDTO, ExaminationTypeRepository,
        ExaminationTypeProvider, ExaminationTypeConverterRequest, ExaminationTypeConverter> {

    private final AppointmentTypeProvider appointmentTypeProvider;

    protected ExaminationTypeController(ExaminationTypeProvider provider, ExaminationTypeConverter converter,
                                        AppointmentTypeProvider appointmentTypeProvider,
                                        List<IUserOrganizationProvider<? extends IUserOrganization>> userOrganizationProvider) {
        super(provider, converter, userOrganizationProvider);
        this.appointmentTypeProvider = appointmentTypeProvider;
    }

    @Override
    protected ExaminationTypeConverterRequest createConverterRequest(ExaminationType examinationType) {
        return new ExaminationTypeConverterRequest(examinationType);
    }

    public List<ExaminationTypeDTO> findByNameAndDeleted(String name, boolean deleted) {
        return convertAll(getProvider().findByNameAndDeleted(name, deleted));
    }

    public ExaminationTypeDTO findByNameAndOrganizationId(String name, String organizationId, boolean deleted) {
        return convert(getProvider().findByNameAndOrganizationId(name, organizationId, deleted));
    }

    public List<ExaminationTypeDTO> findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(String organizationId, String appointmentTypeName, boolean deleted) {
        return findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(organizationId,
                appointmentTypeProvider.findByNameAndOrganizationId(appointmentTypeName, organizationId), deleted);
    }

    public List<ExaminationTypeDTO> findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(
            String organizationId, AppointmentType appointmentType, boolean deleted) {
        return convertAll(getProvider().findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(organizationId, appointmentType, deleted));
    }

    public List<ExaminationTypeDTO> findAllByOrOrganizationIdAndAppointmentTypeInAndDeletedUsingNames(
            String organizationId, Collection<String> appointmentTypeNames, boolean deleted) {
        if (appointmentTypeNames != null) {
            final Set<AppointmentType> appointmentTypes = new HashSet<>();
            for (final String appointmentTypeName : appointmentTypeNames) {
                appointmentTypes.add(appointmentTypeProvider.findByNameAndOrganizationId(appointmentTypeName, organizationId));
            }
            return findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(organizationId, appointmentTypes, deleted);
        } else {
            return findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(organizationId, null, deleted);
        }
    }

    public List<ExaminationTypeDTO> findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(
            String organizationId, Collection<AppointmentType> appointmentTypes, boolean deleted) {
        return convertAll(getProvider().findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(organizationId, appointmentTypes, deleted));
    }
}
