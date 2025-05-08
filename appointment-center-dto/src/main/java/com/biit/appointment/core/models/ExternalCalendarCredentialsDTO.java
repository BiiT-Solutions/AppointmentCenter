package com.biit.appointment.core.models;


import com.biit.server.controllers.models.ElementDTO;

import java.io.Serial;
import java.util.UUID;

public class ExternalCalendarCredentialsDTO extends ElementDTO<Long> {

    @Serial
    private static final long serialVersionUID = -5593134183953887764L;


    private Long id;

    private UUID userId;

    private CalendarProviderDTO calendarProvider;

    private String credentials;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public CalendarProviderDTO getProvider() {
        return calendarProvider;
    }

    public void setProvider(CalendarProviderDTO calendarProvider) {
        this.calendarProvider = calendarProvider;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
