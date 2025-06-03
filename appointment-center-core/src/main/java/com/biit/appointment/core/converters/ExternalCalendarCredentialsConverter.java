package com.biit.appointment.core.converters;

import com.biit.appointment.core.converters.models.ExternalCalendarCredentialsConverterRequest;
import com.biit.appointment.core.models.ExternalCalendarCredentialsDTO;
import com.biit.appointment.persistence.entities.ExternalCalendarCredentials;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ExternalCalendarCredentialsConverter extends ElementConverter<ExternalCalendarCredentials,
        ExternalCalendarCredentialsDTO, ExternalCalendarCredentialsConverterRequest> {

    private final CalendarProviderConverter calendarProviderConverter;

    public ExternalCalendarCredentialsConverter(CalendarProviderConverter calendarProviderConverter) {
        super();
        this.calendarProviderConverter = calendarProviderConverter;
    }


    @Override
    protected ExternalCalendarCredentialsDTO convertElement(ExternalCalendarCredentialsConverterRequest from) {
        if (from.getEntity() == null) {
            return null;
        }
        final ExternalCalendarCredentialsDTO externalCalendarCredentialsDTO = new ExternalCalendarCredentialsDTO();
        BeanUtils.copyProperties(from.getEntity(), externalCalendarCredentialsDTO);
        if (from.getEntity().getCalendarProvider() != null) {
            externalCalendarCredentialsDTO.setCalendarProvider(calendarProviderConverter.convertElement(from.getEntity().getCalendarProvider()));
        }
        if (from.getEntity().getUserCredentials() != null && !from.getEntity().getUserCredentials().isBlank()) {
            externalCalendarCredentialsDTO.setUserCredentials(from.getEntity().getUserCredentials());
        }
        return externalCalendarCredentialsDTO;
    }


    @Override
    public ExternalCalendarCredentials reverse(ExternalCalendarCredentialsDTO from) {
        if (from == null) {
            return null;
        }
        final ExternalCalendarCredentials externalCalendarCredentials = new ExternalCalendarCredentials();
        BeanUtils.copyProperties(from, externalCalendarCredentials);
        if (from.getCalendarProvider() != null) {
            externalCalendarCredentials.setCalendarProvider(calendarProviderConverter.reverse(from.getCalendarProvider()));
        }
        if (from.getUserCredentials() != null && !from.getUserCredentials().isBlank()) {
            externalCalendarCredentials.setUserCredentials(from.getUserCredentials());
        }
        return externalCalendarCredentials;
    }
}
