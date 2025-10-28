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

    public String getQrCode(Long appointmentId, UUID attendeeUUID) {
        return getQr() + "/appointments/" + appointmentId + "/attendance/" + attendeeUUID;
    }

    public String attendWithQrCode(Long appointmentId) {
        return getAttendances() + "/appointments/" + appointmentId + "/attend/text";
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
