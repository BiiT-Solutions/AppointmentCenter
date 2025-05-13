package com.biit.appointment.core.controllers;

import com.biit.appointment.core.converters.CalendarProviderConverter;
import com.biit.appointment.core.converters.ExternalCalendarCredentialsConverter;
import com.biit.appointment.core.converters.models.ExternalCalendarCredentialsConverterRequest;
import com.biit.appointment.core.models.CalendarProviderDTO;
import com.biit.appointment.core.models.ExternalCalendarCredentialsDTO;
import com.biit.appointment.core.providers.ExternalCalendarCredentialsProvider;
import com.biit.appointment.logger.AppointmentCenterLogger;
import com.biit.appointment.persistence.entities.ExternalCalendarCredentials;
import com.biit.appointment.persistence.repositories.ExternalCalendarCredentialsRepository;
import com.biit.server.controller.ElementController;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class ExternalCalendarCredentialsController extends ElementController<ExternalCalendarCredentials, Long, ExternalCalendarCredentialsDTO,
        ExternalCalendarCredentialsRepository, ExternalCalendarCredentialsProvider, ExternalCalendarCredentialsConverterRequest,
        ExternalCalendarCredentialsConverter> implements IExternalCredentialsController {

    private final CalendarProviderConverter calendarProviderConverter;

    protected ExternalCalendarCredentialsController(ExternalCalendarCredentialsProvider provider, ExternalCalendarCredentialsConverter converter,
                                                    CalendarProviderConverter calendarProviderConverter) {
        super(provider, converter);
        this.calendarProviderConverter = calendarProviderConverter;
    }


    @Override
    protected ExternalCalendarCredentialsConverterRequest createConverterRequest(ExternalCalendarCredentials schedule) {
        return new ExternalCalendarCredentialsConverterRequest(schedule);
    }


    public ExternalCalendarCredentialsDTO getByUserIdAndCalendarProvider(UUID userId, CalendarProviderDTO calendarProvider, String requestedBy) {
        AppointmentCenterLogger.debug(this.getClass(), "User '{}' is requesting the credentials from user '{}' on '{}'.",
                requestedBy, userId, calendarProvider);
        return convert(getProvider().getByUserIdAndCalendarProvider(userId, calendarProviderConverter.reverse(calendarProvider)));
    }

}
