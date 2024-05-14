package com.biit.appointment.rest.client;


import com.biit.appointment.logger.AppointmentCenterClientLogger;
import com.biit.rest.exceptions.EmptyResultException;
import com.biit.rest.exceptions.InvalidResponseException;
import com.biit.server.client.SecurityClient;
import com.biit.server.security.IAuthenticatedUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Order(2)
@Qualifier("appointmentCenterClient")
public class AppointmentCenterClient {

    private final SecurityClient securityClient;
    private final AppointmentUrlConstructor appointmentUrlConstructor;

    public AppointmentCenterClient(SecurityClient securityClient, AppointmentUrlConstructor appointmentUrlConstructor) {
        this.securityClient = securityClient;
        this.appointmentUrlConstructor = appointmentUrlConstructor;
    }


    public Optional<AppointmentDTO> findByUsername(String username) {
        try {
            try (Response response = securityClient.get(appointmentUrlConstructor.getUserManagerServerUrl(),
                    appointmentUrlConstructor.getUserByName(username))) {
                AppointmentCenterClientLogger.debug(this.getClass(), "Response obtained from '{}' is '{}'.",
                        appointmentUrlConstructor.getUserManagerServerUrl() + appointmentUrlConstructor.getUserByName(username), response.getStatus());
                return Optional.of(mapper.readValue(response.readEntity(String.class), UserDTO.class));
            }
        } catch (JsonProcessingException e) {
            throw new InvalidResponseException(e);
        } catch (EmptyResultException e) {
            throw new RuntimeException(e);
        } catch (InvalidConfigurationException e) {
            AppointmentCenterClientLogger.warning(this.getClass(), e.getMessage());
            return Optional.empty();
        }
    }
}
