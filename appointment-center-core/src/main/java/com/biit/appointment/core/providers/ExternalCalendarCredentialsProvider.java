package com.biit.appointment.core.providers;


import com.biit.appointment.persistence.entities.CalendarProvider;
import com.biit.appointment.persistence.entities.ExternalCalendarCredentials;
import com.biit.appointment.persistence.repositories.ExternalCalendarCredentialsRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ExternalCalendarCredentialsProvider extends ElementProvider<ExternalCalendarCredentials, Long, ExternalCalendarCredentialsRepository> {


    public ExternalCalendarCredentialsProvider(ExternalCalendarCredentialsRepository repository) {
        super(repository);
    }


    public ExternalCalendarCredentials getByUserIdAndCalendarProvider(UUID userId, CalendarProvider calendarProvider) {
        return getRepository().findByUserIdAndCalendarProvider(userId, calendarProvider);
    }

    public List<ExternalCalendarCredentials> findByExpiresAtAfter(LocalDateTime expiresAt) {
        return getRepository().findByExpiresAtAfter(expiresAt);
    }


}
