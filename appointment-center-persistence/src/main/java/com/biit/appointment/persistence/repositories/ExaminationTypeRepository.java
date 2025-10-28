package com.biit.appointment.persistence.repositories;

/*-
 * #%L
 * AppointmentCenter (Persistence)
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

import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.server.persistence.repositories.ElementRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExaminationTypeRepository extends ElementRepository<ExaminationType, String> {


    List<ExaminationType> findByNameAndDeleted(String name, boolean deleted);

    List<ExaminationType> findByNameInAndDeleted(Collection<String> name, boolean deleted);

    Optional<ExaminationType> findByNameAndOrganizationIdAndDeleted(String name, String organizationId, boolean deleted);

    List<ExaminationType> findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(String organizationId, AppointmentType appointmentType, boolean deleted);

    List<ExaminationType> findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(
            String organizationId, Collection<AppointmentType> appointmentTypes, boolean deleted);
}
