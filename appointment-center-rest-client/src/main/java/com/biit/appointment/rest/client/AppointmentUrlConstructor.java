package com.biit.appointment.rest.client;

import com.biit.usermanager.client.exceptions.InvalidConfigurationException;
import org.springframework.beans.factory.annotation.Value;

public class AppointmentUrlConstructor {

    @Value("${appointmentCenter.server.url:#{null}}")
    private String appointmentCenterServerUrl;

    public String getAppointmentCenterServerUrl() {
        if (appointmentCenterServerUrl == null) {
            throw new InvalidConfigurationException(this.getClass(), "Value 'appointmentCenter.server.url' not set on 'application.properties'!");
        }
        return appointmentCenterServerUrl;
    }

    public String getAppointments() {
        return "/appointments";
    }
}
