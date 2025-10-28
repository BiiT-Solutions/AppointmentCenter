package com.biit.appointment.core.converters.models;

/*-
 * #%L
 * AppointmentCenter (Core)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import com.biit.appointment.core.exceptions.InvalidParameterException;
import com.biit.appointment.logger.AppointmentCenterLogger;
import com.biit.database.encryption.ChaCha20CipherEngine;
import com.biit.database.encryption.InvalidEncryptionException;
import com.biit.database.encryption.KeyProperty;
import com.biit.kafka.config.ObjectMapperFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public class AttendanceRequest {

    @NotNull
    private Long appointmentId;

    @NotNull
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
            if (KeyProperty.getEncryptionKey() != null && !KeyProperty.getEncryptionKey().isBlank()) {
                return CHA_CHA_20_CIPHER_ENGINE.encrypt(jsonCode);
            }
            return Base64.getEncoder().encodeToString(jsonCode.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException | InvalidEncryptionException e) {
            throw new InvalidParameterException(this.getClass(), "AttendanceRequest cannot be coded!", e);
        }
    }

    public static AttendanceRequest decode(String code) {
        try {
            if (KeyProperty.getEncryptionKey() != null && !KeyProperty.getEncryptionKey().isBlank()) {
                final String jsonCode = CHA_CHA_20_CIPHER_ENGINE.decrypt(code);
                AppointmentCenterLogger.debug(AttendanceRequest.class, "Received codified code is '{}'.", jsonCode);
                return ObjectMapperFactory.getObjectMapper().readValue(jsonCode, AttendanceRequest.class);
            } else {
                return ObjectMapperFactory.getObjectMapper().readValue(Base64.getDecoder().decode(code), AttendanceRequest.class);
            }
        } catch (IOException e) {
            throw new InvalidParameterException(AttendanceRequest.class, "AttendanceRequest cannot be decoded!", e);
        }
    }
}
