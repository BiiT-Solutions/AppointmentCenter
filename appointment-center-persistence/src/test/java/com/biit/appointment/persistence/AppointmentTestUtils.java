package com.biit.appointment.persistence;

/*-
 * #%L
 * AppointmentCenter (Persistence)
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

import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.ExaminationType;

import java.time.LocalDateTime;
import java.util.UUID;

public class AppointmentTestUtils {
    public static final long END_TIME_INCREMENT = 300;

    public static Appointment createAppointment(UUID organizer, String organizationId, LocalDateTime startAt, ExaminationType examinationType, UUID attendeeId) {
        return createAppointment(organizer, organizationId, startAt, startAt != null ? startAt.plusSeconds(END_TIME_INCREMENT) : null,
                examinationType, attendeeId);
    }

    public static Appointment createAppointment(UUID organizer, String organizationId, LocalDateTime startAt, LocalDateTime endsAt,
                                                ExaminationType examinationType, UUID attendeeId) {
        Appointment appointment = new Appointment();

        appointment.setOrganizer(organizer);
        appointment.setOrganizationId(organizationId);
        appointment.setStartTime(startAt);
        appointment.setEndTime(endsAt);
        appointment.setExaminationType(examinationType);
        appointment.addAttendee(attendeeId);
        return appointment;
    }
}
