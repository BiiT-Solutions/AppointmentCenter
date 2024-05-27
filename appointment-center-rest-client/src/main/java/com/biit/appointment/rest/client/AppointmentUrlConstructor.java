package com.biit.appointment.rest.client;


import com.biit.appointment.core.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class AppointmentUrlConstructor {

    @Value("${appointments.server.url:#{null}}")
    private String appointmentCenterServerUrl;

    public String getAppointmentCenterServerUrl() {
        if (appointmentCenterServerUrl == null) {
            throw new InvalidFormatException(this.getClass(), "Value 'appointmentCenter.server.url' not set on 'application.properties'!");
        }
        return appointmentCenterServerUrl;
    }

    public String getAppointments() {
        return "/appointments";
    }

    public String getByAttendeeIdAndTemplateCurrent(Long appointmentTemplateId, UUID attendeeUUID) {
        return getAppointments() + "/template/" + appointmentTemplateId + "/attendee/" + attendeeUUID + "/next";
    }

    public String getByAttendeeIdAndTemplateCurrent(String appointmentTemplateName, UUID attendeeUUID) {
        return getAppointments() + "/template/name/" + URLEncoder.encode(appointmentTemplateName, StandardCharsets.UTF_8) + "/attendee/"
                + attendeeUUID + "/next";
    }
}
