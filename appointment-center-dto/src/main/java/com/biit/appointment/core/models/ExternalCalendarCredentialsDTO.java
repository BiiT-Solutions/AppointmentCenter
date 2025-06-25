package com.biit.appointment.core.models;


import com.biit.appointment.core.exceptions.ExternalCalendarException;
import com.biit.appointment.core.utils.ObjectMapperFactory;
import com.biit.appointment.logger.AppointmentCenterLogger;
import com.biit.server.controllers.models.ElementDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.UUID;

public class ExternalCalendarCredentialsDTO extends ElementDTO<Long> {

    @Serial
    private static final long serialVersionUID = -5593134183953887764L;

    private Long id;

    @NotNull
    private UUID userId;

    @Valid
    @NotNull
    private CalendarProviderDTO calendarProvider;

    @NotNull
    private String userCredentials;

    private LocalDateTime expiresAt;

    private LocalDateTime forceRefreshAt;

    public ExternalCalendarCredentialsDTO() {
        super();
    }

    public ExternalCalendarCredentialsDTO(UUID userId, CalendarProviderDTO calendarProvider) {
        this();
        this.userId = userId;
        this.calendarProvider = calendarProvider;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public CalendarProviderDTO getCalendarProvider() {
        return calendarProvider;
    }

    public void setCalendarProvider(CalendarProviderDTO calendarProvider) {
        this.calendarProvider = calendarProvider;
    }

    public boolean hasExpired() {
        if (expiresAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public LocalDateTime getForceRefreshAt() {
        return forceRefreshAt;
    }

    public void setForceRefreshAt(LocalDateTime forceRefreshAt) {
        this.forceRefreshAt = forceRefreshAt;
    }
}
