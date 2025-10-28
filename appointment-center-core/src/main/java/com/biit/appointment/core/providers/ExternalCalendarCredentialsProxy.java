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
import com.biit.appointment.core.models.CalendarProviderDTO;
import com.biit.appointment.core.models.ExternalCalendarCredentialsDTO;
import com.biit.appointment.logger.AppointmentCenterLogger;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ExternalCalendarCredentialsProxy {
    private final ExternalCalendarCredentialsProvider externalCalendarCredentialsProvider;
    private final ExternalCalendarCredentialsConverter externalCalendarCredentialsConverter;
    private final CalendarProviderConverter calendarProviderConverter;

    public ExternalCalendarCredentialsProxy(ExternalCalendarCredentialsProvider externalCalendarCredentialsProvider,
                                            ExternalCalendarCredentialsConverter externalCalendarCredentialsConverter,
                                            CalendarProviderConverter calendarProviderConverter) {
        this.externalCalendarCredentialsProvider = externalCalendarCredentialsProvider;
        this.externalCalendarCredentialsConverter = externalCalendarCredentialsConverter;
        this.calendarProviderConverter = calendarProviderConverter;
    }

    public void delete(UUID userId, CalendarProviderDTO calendarProvider) {
        AppointmentCenterLogger.info(this.getClass(), "Deleting external calendar credentials for " + userId.toString());
        externalCalendarCredentialsProvider.deleteByUserIdAndCalendarProvider(userId, calendarProviderConverter.reverse(calendarProvider));
    }

    public ExternalCalendarCredentialsDTO create(ExternalCalendarCredentialsDTO externalCalendarCredentialsDTO) {
        return externalCalendarCredentialsConverter.convert(new ExternalCalendarCredentialsConverterRequest(externalCalendarCredentialsProvider
                .save(externalCalendarCredentialsConverter.reverse(externalCalendarCredentialsDTO))));
    }

}
