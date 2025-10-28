package com.biit.appointment.rest.client;

/*-
 * #%L
 * AppointmentCenter (Rest Client)
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


import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.models.IAppointmentCenterRestClient;
import com.biit.appointment.core.models.QrCodeDTO;
import com.biit.appointment.logger.AppointmentCenterClientLogger;
import com.biit.rest.exceptions.InvalidResponseException;
import com.biit.server.client.SecurityClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Order(2)
@Qualifier("appointmentCenterRestClient")
public class AppointmentCenterClient implements IAppointmentCenterRestClient {

    private final SecurityClient securityClient;
    private final AppointmentUrlConstructor appointmentUrlConstructor;
    private final ObjectMapper mapper;

    public AppointmentCenterClient(SecurityClient securityClient, AppointmentUrlConstructor appointmentUrlConstructor, ObjectMapper mapper) {
        this.securityClient = securityClient;
        this.appointmentUrlConstructor = appointmentUrlConstructor;
        this.mapper = mapper;
    }


    /**
     * If one appointment is currently on execution, get this one,
     * if not, get the last one at the past, if not the first one at the future.
     */
    @Override
    public Optional<AppointmentDTO> findByAttendeeAndTemplateCurrent(UUID userUUID, Long appointmentTemplateId) {
        try {
            try (Response response = securityClient.get(appointmentUrlConstructor.getAppointmentCenterServerUrl(),
                    appointmentUrlConstructor.getByAttendeeIdAndTemplateCurrent(appointmentTemplateId, userUUID))) {
                AppointmentCenterClientLogger.debug(this.getClass(), "Response obtained from '{}' is '{}'.",
                        appointmentUrlConstructor.getAppointmentCenterServerUrl()
                                + appointmentUrlConstructor.getByAttendeeIdAndTemplateCurrent(appointmentTemplateId, userUUID),
                        response.getStatus());
                if (response.getLength() == 0) {
                    return Optional.empty();
                }
                return Optional.of(mapper.readValue(response.readEntity(String.class), AppointmentDTO.class));
            }
        } catch (JsonProcessingException e) {
            throw new InvalidResponseException(e);
        }
    }

    /**
     * If one appointment is currently on execution, get this one,
     * if not, get the last one at the past, if not the first one at the future.
     */
    @Override
    public Optional<AppointmentDTO> findByAttendeeAndTemplateCurrent(UUID userUUID, String appointmentTemplateName) {
        try {
            try (Response response = securityClient.get(appointmentUrlConstructor.getAppointmentCenterServerUrl(),
                    appointmentUrlConstructor.getByAttendeeIdAndTemplateCurrent(appointmentTemplateName, userUUID))) {
                AppointmentCenterClientLogger.debug(this.getClass(), "Response obtained from '{}' is '{}'.",
                        appointmentUrlConstructor.getAppointmentCenterServerUrl()
                                + appointmentUrlConstructor.getByAttendeeIdAndTemplateCurrent(appointmentTemplateName, userUUID),
                        response.getStatus());
                if (response.getLength() == 0) {
                    return Optional.empty();
                }
                return Optional.of(mapper.readValue(response.readEntity(String.class), AppointmentDTO.class));
            }
        } catch (JsonProcessingException e) {
            throw new InvalidResponseException(e);
        }
    }

    /**
     * Gets all appointments. For integration testing.
     */
    public List<AppointmentDTO> findAll() {
        try {
            try (Response response = securityClient.get(appointmentUrlConstructor.getAppointmentCenterServerUrl(),
                    appointmentUrlConstructor.getAllAppointments())) {
                AppointmentCenterClientLogger.debug(this.getClass(), "Response obtained from '{}' is '{}'.",
                        appointmentUrlConstructor.getAppointmentCenterServerUrl()
                                + appointmentUrlConstructor.getAllAppointments(),
                        response.getStatus());
                if (response.getLength() == 0) {
                    return new ArrayList<>();
                }
                return Arrays.asList(mapper.readValue(response.readEntity(String.class), AppointmentDTO[].class));
            }
        } catch (JsonProcessingException e) {
            throw new InvalidResponseException(e);
        }
    }

    /**
     * Gets an QR Code object. For integration testing.
     */
    public Optional<QrCodeDTO> getQrCode(Long appointmentId, UUID attendeeUUID) {
        try {
            try (Response response = securityClient.get(appointmentUrlConstructor.getAppointmentCenterServerUrl(),
                    appointmentUrlConstructor.getQrCode(appointmentId, attendeeUUID))) {
                AppointmentCenterClientLogger.debug(this.getClass(), "Response obtained from '{}' is '{}'.",
                        appointmentUrlConstructor.getAppointmentCenterServerUrl()
                                + appointmentUrlConstructor.getQrCode(appointmentId, attendeeUUID),
                        response.getStatus());
                if (response.getLength() == 0) {
                    return Optional.empty();
                }
                return Optional.of(mapper.readValue(response.readEntity(String.class), QrCodeDTO.class));
            }
        } catch (JsonProcessingException e) {
            throw new InvalidResponseException(e);
        }
    }

    /**
     * Put a QR Code object. For integration testing.
     */
    public void attendByQrCode(Long appointmentId, QrCodeDTO qrCodeDTO) {
        try (Response response = securityClient.put(appointmentUrlConstructor.getAppointmentCenterServerUrl(),
                appointmentUrlConstructor.attendWithQrCode(appointmentId), qrCodeDTO.getContent(),
                MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN)) {
            AppointmentCenterClientLogger.debug(this.getClass(), "Response obtained from '{}' is '{}'.",
                    appointmentUrlConstructor.getAppointmentCenterServerUrl()
                            + appointmentUrlConstructor.attendWithQrCode(appointmentId),
                    response.getStatus());
            if (response.getStatus() != HttpStatus.NO_CONTENT.value()) {
                throw new InvalidResponseException("Action failed");
            }
        }
    }


    /**
     * Gets an Attendance Request. For integration testing.
     */
    public Optional<String> getAttendanceRequest(UUID userUUID, Long appointmentId) {
        try (Response response = securityClient.get(appointmentUrlConstructor.getAppointmentCenterServerUrl(),
                appointmentUrlConstructor.getAttendanceRequest(userUUID, appointmentId))) {
            AppointmentCenterClientLogger.debug(this.getClass(), "Response obtained from '{}' is '{}'.",
                    appointmentUrlConstructor.getAppointmentCenterServerUrl()
                            + appointmentUrlConstructor.getAttendanceRequest(userUUID, appointmentId),
                    response.getStatus());
            if (response.getLength() == 0) {
                return Optional.empty();
            }
            return Optional.of(response.readEntity(String.class));
        }
    }

    /**
     * Attends from a encrypted attendanceRequest. For integration testing.
     */
    public void putAttendance(String attendanceRequest, Long appointmentId) {
        try (Response response = securityClient.put(appointmentUrlConstructor.getAppointmentCenterServerUrl(),
                appointmentUrlConstructor.putAttendanceRequest(appointmentId), attendanceRequest)) {
            AppointmentCenterClientLogger.debug(this.getClass(), "Response obtained from '{}' is '{}'.",
                    appointmentUrlConstructor.getAppointmentCenterServerUrl()
                            + appointmentUrlConstructor.putAttendanceRequest(appointmentId),
                    response.getStatus());
        }
    }
}
