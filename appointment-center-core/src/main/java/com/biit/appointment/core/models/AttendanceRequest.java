package com.biit.appointment.core.models;


import com.biit.appointment.core.exceptions.InvalidParameterException;
import com.biit.appointment.logger.AppointmentCenterLogger;
import com.biit.database.encryption.ChaCha20CipherEngine;
import com.biit.database.encryption.InvalidEncryptionException;
import com.biit.database.encryption.KeyProperty;
import com.biit.kafka.config.ObjectMapperFactory;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.UUID;

public class AttendanceRequest {

    private Long appointmentId;
    private UUID attender;

    private static final ChaCha20CipherEngine CHA_CHA_20_CIPHER_ENGINE = new ChaCha20CipherEngine();

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
            final String jsonCode = ObjectMapperFactory.getObjectMapper().writeValueAsString(this);
            if (KeyProperty.getEncryptionKey() != null) {
                return CHA_CHA_20_CIPHER_ENGINE.encrypt(jsonCode);
            }
            return jsonCode;
        } catch (JsonProcessingException | InvalidEncryptionException e) {
            throw new InvalidParameterException(this.getClass(), "AttendanceRequest cannot be coded!", e);
        }
    }

    public static AttendanceRequest decode(String code) {
        try {
            if (KeyProperty.getEncryptionKey() != null) {
                final String jsonCode = CHA_CHA_20_CIPHER_ENGINE.decrypt(code);
                AppointmentCenterLogger.debug(AttendanceRequest.class, "Received codified code is '{}'.", jsonCode);
                return ObjectMapperFactory.getObjectMapper().readValue(jsonCode, AttendanceRequest.class);
            } else {
                return ObjectMapperFactory.getObjectMapper().readValue(code, AttendanceRequest.class);
            }
        } catch (IOException e) {
            throw new InvalidParameterException(AttendanceRequest.class, "AttendanceRequest cannot be decoded!", e);
        }
    }
}
