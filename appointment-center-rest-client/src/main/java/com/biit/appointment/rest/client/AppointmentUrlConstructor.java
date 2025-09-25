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
            throw new InvalidFormatException(this.getClass(), "Value 'appointments.server.url' not set on 'application.properties'!");
        }
        return appointmentCenterServerUrl;
    }

    public String getAppointments() {
        return "/appointments";
    }

    public String getAttendances() {
        return "/attendances";
    }

    public String getQr() {
        return "/qr";
    }

    public String getAllAppointments() {
        return getAppointments();
    }

    public String getByAttendeeIdAndTemplateCurrent(Long appointmentTemplateId, UUID attendeeUUID) {
        return getAppointments() + "/template/" + appointmentTemplateId + "/attendee/" + attendeeUUID + "/next";
    }

    public String getByAttendeeIdAndTemplateCurrent(String appointmentTemplateName, UUID attendeeUUID) {
        return getAppointments() + "/template/title/" + URLEncoder.encode(appointmentTemplateName, StandardCharsets.UTF_8) + "/attendee/"
                + attendeeUUID + "/next";
    }

    public String getQrCode(Long appointmentId) {
        return getQr() + "/appointments/" + appointmentId + "/attendance";
    }

    public String attendWithQrCode(Long appointmentId, UUID userUUID) {
        return getAppointments() + "/" + appointmentId + "/attend/" + userUUID + "/text";
    }

    public String getAttendanceRequest(UUID userUUID, Long appointmentId) {
        return getAttendances() + "/appointments/" + appointmentId + "/attendees/" + userUUID + "/code";
    }

    public String putAttendanceRequest(Long appointmentId) {
        return getAttendances() + "/appointments/" + appointmentId + "/attend";
    }
}
