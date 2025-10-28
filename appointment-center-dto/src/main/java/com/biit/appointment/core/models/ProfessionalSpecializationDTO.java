package com.biit.appointment.core.models;

/*-
 * #%L
 * AppointmentCenter (DTO)
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

import com.biit.server.controllers.models.ElementDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProfessionalSpecializationDTO extends ElementDTO<Long> {

    private Long id;

    @NotNull
    @Size(min = ElementDTO.MIN_FIELD_LENGTH, max = ElementDTO.MAX_SMALL_FIELD_LENGTH)
    private String name;

    @Valid
    @NotNull
    private AppointmentTypeDTO appointmentType;

    private String organizationId;

    private Long userId;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AppointmentTypeDTO getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(AppointmentTypeDTO appointmentType) {
        this.appointmentType = appointmentType;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
