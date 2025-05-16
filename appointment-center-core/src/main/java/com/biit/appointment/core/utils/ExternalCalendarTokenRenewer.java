package com.biit.appointment.core.utils;

import com.biit.appointment.core.services.IExternalProviderCalendarService;
import com.biit.appointment.core.converters.CalendarProviderConverter;
import com.biit.appointment.core.converters.ExternalCalendarCredentialsConverter;
import com.biit.appointment.core.converters.models.ExternalCalendarCredentialsConverterRequest;
import com.biit.appointment.core.models.CalendarProviderDTO;
import com.biit.appointment.core.models.ExternalCalendarCredentialsDTO;
import com.biit.appointment.core.providers.ExternalCalendarCredentialsProvider;
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
        if (externalCalendarCredentials.hasExpired()) {
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
                    externalCalendarCredentials.getUserId(), externalCalendarCredentials.getProvider());
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
                    externalCalendarCredentials.getUserId(), externalCalendarCredentials.getProvider());
            AppointmentCenterLogger.errorMessage(this.getClass(), e);
            externalCalendarCredentialsProvider.delete(externalCalendarCredentialsConverter.reverse(externalCalendarCredentials));
        }
        return null;
    }
}
