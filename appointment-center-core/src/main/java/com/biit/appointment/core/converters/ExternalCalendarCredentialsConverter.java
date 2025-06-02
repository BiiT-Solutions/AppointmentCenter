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
        externalCalendarCredentialsDTO.setCalendarProvider(calendarProviderConverter.convertElement(from.getEntity().getProvider()));
        return externalCalendarCredentialsDTO;
    }


    @Override
    public ExternalCalendarCredentials reverse(ExternalCalendarCredentialsDTO from) {
        if (from == null) {
            return null;
        }
        final ExternalCalendarCredentials externalCalendarCredentials = new ExternalCalendarCredentials();
        BeanUtils.copyProperties(from, externalCalendarCredentials);
        externalCalendarCredentials.setProvider(calendarProviderConverter.reverse(from.getCalendarProvider()));
        return externalCalendarCredentials;
    }
}
