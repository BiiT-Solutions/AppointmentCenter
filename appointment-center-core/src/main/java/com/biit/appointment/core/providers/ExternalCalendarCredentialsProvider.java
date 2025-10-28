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


import com.biit.appointment.core.converters.CalendarProviderConverter;
import com.biit.appointment.core.converters.ExternalCalendarCredentialsConverter;
import com.biit.appointment.core.converters.models.ExternalCalendarCredentialsConverterRequest;
import com.biit.appointment.core.services.IExternalProviderCalendarService;
import com.biit.appointment.logger.AppointmentCenterLogger;
import com.biit.appointment.persistence.entities.CalendarProvider;
import com.biit.appointment.persistence.entities.ExternalCalendarCredentials;
import com.biit.appointment.persistence.repositories.ExternalCalendarCredentialsRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ExternalCalendarCredentialsProvider extends ElementProvider<ExternalCalendarCredentials, Long, ExternalCalendarCredentialsRepository> {

    private final List<IExternalProviderCalendarService> externalCalendarServices;
    private final ExternalCalendarCredentialsConverter externalCalendarCredentialsConverter;
    private final CalendarProviderConverter calendarProviderConverter;

    protected ExternalCalendarCredentialsProvider(ExternalCalendarCredentialsRepository repository,
                                                  List<IExternalProviderCalendarService> externalCalendarServices,
                                                  ExternalCalendarCredentialsConverter externalCalendarCredentialsConverter,
                                                  CalendarProviderConverter calendarProviderConverter) {
        super(repository);
        this.externalCalendarServices = externalCalendarServices;
        this.externalCalendarCredentialsConverter = externalCalendarCredentialsConverter;
        this.calendarProviderConverter = calendarProviderConverter;
    }

    @Override
    public ExternalCalendarCredentials save(ExternalCalendarCredentials entity) {
        deleteByUserIdAndCalendarProvider(entity.getUserId(), entity.getCalendarProvider());
        return super.save(entity);
    }

    public ExternalCalendarCredentials getByUserIdAndCalendarProvider(UUID userId, CalendarProvider calendarProvider) {
        return refreshIfExpired(getRepository().findByUserIdAndCalendarProvider(userId, calendarProvider));
    }

    public List<ExternalCalendarCredentials> getByUserId(UUID userId) {
        final List<ExternalCalendarCredentials> oldCredentials = getRepository().findByUserId(userId);
        final List<ExternalCalendarCredentials> newCredentials = new ArrayList<>();
        oldCredentials.forEach(oldCredential -> newCredentials.add(refreshIfExpired(oldCredential)));
        return newCredentials;
    }

    public void deleteByUserIdAndCalendarProvider(UUID userId, CalendarProvider calendarProvider) {
        getRepository().deleteByUserIdAndCalendarProvider(userId, calendarProvider);
    }

    public List<ExternalCalendarCredentials> findByCreatedAtBefore(LocalDateTime expiresAt) {
        return getRepository().findByCreatedAtBefore(expiresAt);
    }

    public List<ExternalCalendarCredentials> findByForceRefreshAtBefore(LocalDateTime expiresAt) {
        return getRepository().findByForceRefreshAtBefore(expiresAt);
    }


    public ExternalCalendarCredentials refreshIfExpired(ExternalCalendarCredentials externalCalendarCredentials) {
        if (externalCalendarCredentials == null) {
            return null;
        }
        if (externalCalendarCredentials.hasExpired()) {
            return refreshExternalCredentials(externalCalendarCredentials);
        }
        //Has not expired yet, but will do it soon. Use the current token, but ask meanwhile for a new one.
        if (externalCalendarCredentials.getForceRefreshAt() != null && externalCalendarCredentials.getForceRefreshAt().isBefore(LocalDateTime.now())) {
            new Thread(() -> refreshExternalCredentials(externalCalendarCredentials)).start();
        }
        return externalCalendarCredentials;
    }


    private IExternalProviderCalendarService getExternalCalendarProvider(CalendarProvider calendarProvider) {
        for (IExternalProviderCalendarService externalProviderCalendarService : externalCalendarServices) {
            if (Objects.equals(calendarProviderConverter.reverse(externalProviderCalendarService.from()), calendarProvider)) {
                return externalProviderCalendarService;
            }
        }
        return null;
    }


    public ExternalCalendarCredentials refreshExternalCredentials(ExternalCalendarCredentials externalCalendarCredentials) {
        try {
            AppointmentCenterLogger.info(this.getClass(), "Updating token for user '{}' and provider '{}'.",
                    externalCalendarCredentials.getUserId(), externalCalendarCredentials.getCalendarProvider());
            final IExternalProviderCalendarService externalProviderCalendarService = getExternalCalendarProvider(
                    externalCalendarCredentials.getCalendarProvider());
            if (externalProviderCalendarService != null) {
                final ExternalCalendarCredentials refreshedExternalCalendarCredentials =
                        externalCalendarCredentialsConverter.reverse(externalProviderCalendarService
                                .updateToken(externalCalendarCredentialsConverter
                                        .convert(new ExternalCalendarCredentialsConverterRequest(externalCalendarCredentials))));
                delete(refreshedExternalCalendarCredentials);
                return save(refreshedExternalCalendarCredentials);
            } else {
                AppointmentCenterLogger.warning(this.getClass(), "no calendar provider found for '{}'",
                        externalCalendarCredentials.getCalendarProvider());
            }
        } catch (Exception e) {
            AppointmentCenterLogger.severe(this.getClass(), "Authorization token for '{}' and '{}' not updated!",
                    externalCalendarCredentials.getUserId(), externalCalendarCredentials.getCalendarProvider());
            AppointmentCenterLogger.errorMessage(this.getClass(), e);
            if (e.getMessage() == null || !e.getMessage().contains("401 Unauthorized")) {
                delete(externalCalendarCredentials);
            }
        }
        return null;
    }

}
