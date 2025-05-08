package com.biit.appointment.core.converters;

import com.biit.appointment.core.models.CalendarProviderDTO;
import com.biit.appointment.persistence.entities.CalendarProvider;
import org.springframework.stereotype.Component;

@Component
public class CalendarProviderConverter {

    protected CalendarProviderDTO convertElement(CalendarProvider from) {
        switch (from) {
            case APPLE -> {
                return CalendarProviderDTO.APPLE;
            }
            case MICROSOFT -> {
                return CalendarProviderDTO.MICROSOFT;
            }
            default -> {
                return CalendarProviderDTO.GOOGLE;
            }
        }
    }

    public CalendarProvider reverse(CalendarProviderDTO from) {
        switch (from) {
            case APPLE -> {
                return CalendarProvider.APPLE;
            }
            case MICROSOFT -> {
                return CalendarProvider.MICROSOFT;
            }
            default -> {
                return CalendarProvider.GOOGLE;
            }
        }
    }

}
