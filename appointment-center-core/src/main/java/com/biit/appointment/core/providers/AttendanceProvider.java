package com.biit.appointment.core.providers;

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

import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.Attendance;
import com.biit.appointment.persistence.repositories.AttendanceRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class AttendanceProvider extends ElementProvider<Attendance, Long, AttendanceRepository> {

    public AttendanceProvider(AttendanceRepository repository) {
        super(repository);
    }

    public Set<Attendance> findByAppointment(Appointment appointment) {
        return getRepository().findByAppointment(appointment);
    }

    public Set<Attendance> findByAttendee(UUID attendee) {
        return getRepository().findByAttendee(attendee);
    }

    public Optional<Attendance> findBy(UUID attendee, Appointment appointment) {
        return getRepository().findByAttendeeAndAppointment(attendee, appointment);
    }
}
