package com.biit.appointment.core.converters;

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

import com.biit.appointment.core.converters.models.AttendanceConverterRequest;
import com.biit.appointment.core.models.AttendanceDTO;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.persistence.entities.Attendance;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class AttendanceConverter extends ElementConverter<Attendance, AttendanceDTO, AttendanceConverterRequest> {

    private final AppointmentProvider appointmentProvider;


    public AttendanceConverter(AppointmentProvider appointmentProvider) {
        this.appointmentProvider = appointmentProvider;
    }

    @Override
    protected AttendanceDTO convertElement(AttendanceConverterRequest from) {
        final AttendanceDTO attendanceDTO = new AttendanceDTO();
        if (from.getEntity() != null) {
            BeanUtils.copyProperties(from.getEntity(), attendanceDTO);
            if (from.getEntity().getAppointment() != null) {
                attendanceDTO.setAppointmentId(from.getEntity().getAppointment().getId());
            }
        }
        return attendanceDTO;
    }

    @Override
    public Attendance reverse(AttendanceDTO to) {
        if (to == null) {
            return null;
        }
        final Attendance attendance = new Attendance();
        BeanUtils.copyProperties(to, attendance);
        if (to.getAppointmentId() != null) {
            attendance.setAppointment(appointmentProvider.findById(to.getAppointmentId()).orElse(null));
        }
        return attendance;
    }
}
