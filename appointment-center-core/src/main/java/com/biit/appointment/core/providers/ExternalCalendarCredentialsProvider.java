package com.biit.appointment.core.providers;


import com.biit.appointment.persistence.entities.CalendarProvider;
import com.biit.appointment.persistence.entities.ExternalCalendarCredentials;
import com.biit.appointment.persistence.repositories.ExternalCalendarCredentialsRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ExternalCalendarCredentialsProvider extends ElementProvider<ExternalCalendarCredentials, Long, ExternalCalendarCredentialsRepository> {


    public ExternalCalendarCredentialsProvider(ExternalCalendarCredentialsRepository repository) {
        super(repository);
    }


    public ExternalCalendarCredentials getByUserIdAndCalendarProvider(UUID userId, CalendarProvider calendarProvider) {
        return getRepository().findByUserIdAndCalendarProvider(userId, calendarProvider);
    }
}
