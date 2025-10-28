package com.biit.appointment.rest.client;

/*-
 * #%L
 * AppointmentCenter (Rest Client Test)
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Primary
@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
@Qualifier("appointmentCenterRestClient")
public class TestAppointmentCenterClient implements IAppointmentCenterRestClient {
    private static final int STARTED_TIME_PASSED = 45;
    private static final int APPOINTMENT_DURATION = 120;

    private LocalDateTime startTime = LocalDateTime.now().minusMinutes(STARTED_TIME_PASSED);
    private LocalDateTime endTime = LocalDateTime.now().plusMinutes(APPOINTMENT_DURATION - (long) STARTED_TIME_PASSED);

    public Optional<AppointmentDTO> findByAttendeeAndTemplateCurrent(UUID userUUID, Long appointmentTemplateId) {
        final AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setAttendees(Collections.singleton(userUUID));
        appointmentDTO.setAppointmentTemplateId(appointmentTemplateId);
        appointmentDTO.setStartTime(startTime);
        appointmentDTO.setEndTime(endTime);

        return Optional.of(appointmentDTO);
    }

    public Optional<AppointmentDTO> findByAttendeeAndTemplateCurrent(UUID userUUID, String appointmentTemplateName) {
        final AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setAttendees(Collections.singleton(userUUID));
        appointmentDTO.setAppointmentTemplateId(1L);
        appointmentDTO.setStartTime(startTime);
        appointmentDTO.setEndTime(endTime);

        return Optional.of(appointmentDTO);
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
