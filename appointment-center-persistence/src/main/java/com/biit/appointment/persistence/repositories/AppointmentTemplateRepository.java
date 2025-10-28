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

import com.biit.appointment.persistence.entities.AppointmentTemplate;
import com.biit.server.persistence.repositories.ElementRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
@Transactional
public interface AppointmentTemplateRepository extends ElementRepository<AppointmentTemplate, Long> {

    /**
     * Finds all appointments from an organizer.
     *
     * @param organizationId the organizer of the appointment.
     * @return a list of appointments.
     */
    List<AppointmentTemplate> findByOrganizationId(String organizationId);

    Optional<AppointmentTemplate> findByTitleAndOrganizationId(String title, String organizationId);

    Optional<AppointmentTemplate> findByTitleHashAndOrganizationId(String titleHash, String organizationId);

    Optional<AppointmentTemplate> findByTitleHash(String titleHash);

    List<AppointmentTemplate> findByTitleIn(Collection<String> titles);

    List<AppointmentTemplate> findByTitleHashIn(Collection<String> titleHashes);

    @Query("""
            SELECT t FROM AppointmentTemplate t WHERE EXISTS
                (SELECT a.id FROM Appointment a WHERE a.appointmentTemplate = t AND :attendeeUUID MEMBER OF a.attendees)
            """)
    List<AppointmentTemplate> findDistinctByAttendeeIn(UUID attendeeUUID);

    @Query("""
            SELECT t FROM AppointmentTemplate t WHERE NOT EXISTS
                (SELECT a FROM Appointment a WHERE a.appointmentTemplate = t AND :attendeeUUID MEMBER OF a.attendees)
            """)
    List<AppointmentTemplate> findDistinctByAttendeeNotIn(UUID attendeeUUID);
}
