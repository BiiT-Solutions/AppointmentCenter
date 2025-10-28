package com.biit.appointment.core.utils;

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
import com.biit.appointment.core.models.CalendarProviderDTO;
import com.biit.appointment.core.models.ExternalCalendarCredentialsDTO;
import com.biit.appointment.core.providers.ExternalCalendarCredentialsProvider;
import com.biit.appointment.core.services.IExternalProviderCalendarService;
import com.biit.appointment.logger.AppointmentCenterLogger;
import com.biit.appointment.persistence.entities.ExternalCalendarCredentials;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class ExternalCalendarTokenRenewer {

    private final List<IExternalProviderCalendarService> externalCalendarControllers;
    private final ExternalCalendarCredentialsConverter externalCalendarCredentialsConverter;
    private final ExternalCalendarCredentialsProvider externalCalendarCredentialsProvider;

    public ExternalCalendarTokenRenewer(List<IExternalProviderCalendarService> externalCalendarControllers,
                                        CalendarProviderConverter calendarProviderConverter,
                                        ExternalCalendarCredentialsConverter externalCalendarCredentialsConverter,
                                        ExternalCalendarCredentialsProvider externalCalendarCredentialsProvider) {
        this.externalCalendarControllers = externalCalendarControllers;
        this.externalCalendarCredentialsConverter = externalCalendarCredentialsConverter;
        this.externalCalendarCredentialsProvider = externalCalendarCredentialsProvider;
    }


    public ExternalCalendarCredentialsDTO refreshIfExpired(ExternalCalendarCredentialsDTO externalCalendarCredentials) {
        if (externalCalendarCredentials != null && externalCalendarCredentials.hasExpired()) {
            return refreshExternalCredentials(externalCalendarCredentials);
        }
        return externalCalendarCredentials;
    }


    private IExternalProviderCalendarService getExternalCalendarProvider(CalendarProviderDTO calendarProvider) {
        for (IExternalProviderCalendarService externalCalendarController : externalCalendarControllers) {
            if (Objects.equals(externalCalendarController.from(), calendarProvider)) {
                return externalCalendarController;
            }
        }
        return null;
    }


    public ExternalCalendarCredentialsDTO refreshExternalCredentials(ExternalCalendarCredentialsDTO externalCalendarCredentials) {
        try {
            AppointmentCenterLogger.info(this.getClass(), "Updating token for user '{}' and provider '{}'.",
                    externalCalendarCredentials.getUserId(), externalCalendarCredentials.getCalendarProvider());
            final IExternalProviderCalendarService externalCalendarController = getExternalCalendarProvider(
                    externalCalendarCredentials.getCalendarProvider());
            if (externalCalendarController != null) {
                final ExternalCalendarCredentials refreshedExternalCalendarCredentials = externalCalendarCredentialsConverter.reverse(
                        externalCalendarController.updateToken(externalCalendarCredentials));
                externalCalendarCredentialsProvider.delete(externalCalendarCredentialsConverter.reverse(externalCalendarCredentials));
                return externalCalendarCredentialsConverter.convert(new ExternalCalendarCredentialsConverterRequest(
                        externalCalendarCredentialsProvider.save(refreshedExternalCalendarCredentials)));
            } else {
                AppointmentCenterLogger.warning(this.getClass(), "no calendar provider found for '{}'",
                        externalCalendarCredentials.getCalendarProvider());
            }
        } catch (Exception e) {
            AppointmentCenterLogger.severe(this.getClass(), "Authorization token for '{}' and '{}' not updated!",
                    externalCalendarCredentials.getUserId(), externalCalendarCredentials.getCalendarProvider());
            AppointmentCenterLogger.errorMessage(this.getClass(), e);
            externalCalendarCredentialsProvider.delete(externalCalendarCredentialsConverter.reverse(externalCalendarCredentials));
        }
        return null;
    }
}
