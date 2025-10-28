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

import com.biit.appointment.persistence.entities.CalendarProvider;
import com.biit.appointment.persistence.entities.ExternalCalendarCredentials;
import com.biit.server.persistence.repositories.ElementRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Transactional
@Repository
public interface ExternalCalendarCredentialsRepository extends ElementRepository<ExternalCalendarCredentials, Long> {

    ExternalCalendarCredentials findByUserIdAndCalendarProvider(UUID userId, CalendarProvider calendarProvider);

    List<ExternalCalendarCredentials> findByUserId(UUID userId);

    void deleteByUserIdAndCalendarProvider(UUID userId, CalendarProvider calendarProvider);

    List<ExternalCalendarCredentials> findByExpiresAtAfter(LocalDateTime expiresAt);

    List<ExternalCalendarCredentials> findByCreatedAtBefore(LocalDateTime createdAt);

    List<ExternalCalendarCredentials> findByForceRefreshAtBefore(LocalDateTime createdAt);

    List<ExternalCalendarCredentials> findByExpiresAtBefore(LocalDateTime expiresAt);
}
