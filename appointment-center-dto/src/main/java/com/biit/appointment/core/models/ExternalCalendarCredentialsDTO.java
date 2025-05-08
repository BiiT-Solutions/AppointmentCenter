package com.biit.appointment.core.models;


import com.biit.appointment.core.exceptions.ExternalCalendarException;
import com.biit.appointment.core.utils.ObjectMapperFactory;
import com.biit.appointment.logger.AppointmentCenterLogger;
import com.biit.server.controllers.models.ElementDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serial;
import java.util.UUID;

public class ExternalCalendarCredentialsDTO extends ElementDTO<Long> {

    @Serial
    private static final long serialVersionUID = -5593134183953887764L;

    private Long id;

    private UUID userId;

    private CalendarProviderDTO calendarProvider;

    private String userCredentials;

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

    public String getUserCredentials() {
        return userCredentials;
    }

    public void setUserCredentials(String userCredentials) {
        this.userCredentials = userCredentials;
    }

    public void setCredentialData(Object credentials) {
        try {
            this.userCredentials = ObjectMapperFactory.getObjectMapper().writeValueAsString(credentials);
        } catch (JsonProcessingException e) {
            AppointmentCenterLogger.errorMessage(this.getClass(), e);
            throw new ExternalCalendarException(this.getClass(), e);
        }
    }

    public <C> C getCredentialData(Class<C> elementClass) {
        if (getUserCredentials() != null && !getUserCredentials().isEmpty()) {
            try {
                final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
                final JavaType elementType = objectMapper.getTypeFactory().constructType(elementClass);
                return objectMapper.readValue(getUserCredentials(), elementType);
            } catch (JsonProcessingException e) {
                AppointmentCenterLogger.errorMessage(this.getClass(), e);
                throw new ExternalCalendarException(this.getClass(), e);
            }
        }
        return null;
    }


    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
