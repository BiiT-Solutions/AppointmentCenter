package com.biit.appointment.core.models;


import com.biit.appointment.core.exceptions.InvalidParameterException;
import com.biit.kafka.config.ObjectMapperFactory;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class AttendanceRequest {

    private Long appointmentId;
    private UUID attender;

    public AttendanceRequest() {
        super();
    }

    public AttendanceRequest(Long appointmentId, UUID attender) {
        this();
        this.appointmentId = appointmentId;
        this.attender = attender;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public UUID getAttender() {
        return attender;
    }

    public void setAttender(UUID attender) {
        this.attender = attender;
    }

    public String code() {
        try {
            return Base64.getEncoder().encodeToString(ObjectMapperFactory.getObjectMapper().writeValueAsString(this)
                    .getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            throw new InvalidParameterException(this.getClass(), "AttendanceRequest cannot be coded!", e);
        }
    }

    public static AttendanceRequest decode(String code) {
        try {
            return ObjectMapperFactory.getObjectMapper().readValue(Base64.getDecoder().decode(code), AttendanceRequest.class);
        } catch (IOException e) {
            throw new InvalidParameterException(AttendanceRequest.class, "AttendanceRequest cannot be decoded!", e);
        }
    }
}
