package com.biit.appointment.rest.client;


import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.models.IAppointmentCenterRestClient;
import com.biit.appointment.logger.AppointmentCenterClientLogger;
import com.biit.rest.exceptions.InvalidResponseException;
import com.biit.server.client.SecurityClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

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
}
