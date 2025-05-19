package com.biit.appointment.core.providers;

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
