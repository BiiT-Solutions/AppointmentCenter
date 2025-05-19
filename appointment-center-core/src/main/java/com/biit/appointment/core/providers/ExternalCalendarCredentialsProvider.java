package com.biit.appointment.core.providers;


import com.biit.appointment.core.converters.ExternalCalendarCredentialsConverter;
import com.biit.appointment.core.converters.models.ExternalCalendarCredentialsConverterRequest;
import com.biit.appointment.core.models.CalendarProviderDTO;
import com.biit.appointment.core.models.ExternalCalendarCredentialsDTO;
import com.biit.appointment.core.services.IExternalProviderCalendarService;
import com.biit.appointment.logger.AppointmentCenterLogger;
import com.biit.appointment.persistence.entities.CalendarProvider;
import com.biit.appointment.persistence.entities.ExternalCalendarCredentials;
import com.biit.appointment.persistence.repositories.ExternalCalendarCredentialsRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ExternalCalendarCredentialsProvider extends ElementProvider<ExternalCalendarCredentials, Long, ExternalCalendarCredentialsRepository> {

    private final List<IExternalProviderCalendarService> externalCalendarServices;
    private final ExternalCalendarCredentialsConverter externalCalendarCredentialsConverter;

    protected ExternalCalendarCredentialsProvider(ExternalCalendarCredentialsRepository repository,
                                                  List<IExternalProviderCalendarService> externalCalendarServices,
                                                  ExternalCalendarCredentialsConverter externalCalendarCredentialsConverter) {
        super(repository);
        this.externalCalendarServices = externalCalendarServices;
        this.externalCalendarCredentialsConverter = externalCalendarCredentialsConverter;
    }

    public ExternalCalendarCredentials getByUserIdAndCalendarProvider(UUID userId, CalendarProvider calendarProvider) {
        return getRepository().findByUserIdAndCalendarProvider(userId, calendarProvider);
    }

    public void deleteByUserIdAndCalendarProvider(UUID userId, CalendarProvider calendarProvider) {
        getRepository().deleteByUserIdAndCalendarProvider(userId, calendarProvider);
    }

    public List<ExternalCalendarCredentials> findByCreatedAtBefore(LocalDateTime expiresAt) {
        return getRepository().findByCreatedAtBefore(expiresAt);
    }


    public ExternalCalendarCredentialsDTO refreshIfExpired(ExternalCalendarCredentialsDTO externalCalendarCredentials) {
        if (externalCalendarCredentials != null && externalCalendarCredentials.hasExpired()) {
            return refreshExternalCredentials(externalCalendarCredentials);
        }
        return externalCalendarCredentials;
    }


    private IExternalProviderCalendarService getExternalCalendarProvider(CalendarProviderDTO calendarProvider) {
        for (IExternalProviderCalendarService externalProviderCalendarService : externalCalendarServices) {
            if (Objects.equals(externalProviderCalendarService.from(), calendarProvider)) {
                return externalProviderCalendarService;
            }
        }
        return null;
    }


    public ExternalCalendarCredentialsDTO refreshExternalCredentials(ExternalCalendarCredentialsDTO externalCalendarCredentials) {
        try {
            AppointmentCenterLogger.info(this.getClass(), "Updating token for user '{}' and provider '{}'.",
                    externalCalendarCredentials.getUserId(), externalCalendarCredentials.getProvider());
            final IExternalProviderCalendarService externalProviderCalendarService = getExternalCalendarProvider(
                    externalCalendarCredentials.getCalendarProvider());
            if (externalProviderCalendarService != null) {
                final ExternalCalendarCredentialsDTO refreshedExternalCalendarCredentials = externalProviderCalendarService
                        .updateToken(externalCalendarCredentials);
                delete(externalCalendarCredentialsConverter.reverse(externalCalendarCredentials));
                return externalCalendarCredentialsConverter.convert(new ExternalCalendarCredentialsConverterRequest(
                        save(externalCalendarCredentialsConverter.reverse(refreshedExternalCalendarCredentials))));
            } else {
                AppointmentCenterLogger.warning(this.getClass(), "no calendar provider found for '{}'",
                        externalCalendarCredentials.getCalendarProvider());
            }
        } catch (Exception e) {
            AppointmentCenterLogger.severe(this.getClass(), "Authorization token for '{}' and '{}' not updated!",
                    externalCalendarCredentials.getUserId(), externalCalendarCredentials.getProvider());
            AppointmentCenterLogger.errorMessage(this.getClass(), e);
            delete(externalCalendarCredentialsConverter.reverse(externalCalendarCredentials));
        }
        return null;
    }

}
