package com.biit.appointment.core.controllers.kafka.payloads;

import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.logger.AppointmentCenterLogger;
import com.biit.kafka.config.ObjectMapperFactory;
import com.biit.kafka.events.EventPayload;
import com.fasterxml.jackson.core.JsonProcessingException;

public class AppointmentPayload implements EventPayload {
    private String json;

    public AppointmentPayload(AppointmentDTO appointment) {
        try {
            setJson(ObjectMapperFactory.getObjectMapper().writeValueAsString(appointment));
        } catch (JsonProcessingException e) {
            AppointmentCenterLogger.errorMessage(this.getClass(), e);
            throw new RuntimeException(e);
        }
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
