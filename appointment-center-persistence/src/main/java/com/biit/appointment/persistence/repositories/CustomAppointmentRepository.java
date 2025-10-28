package com.biit.appointment.persistence.repositories;

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
import com.biit.appointment.persistence.entities.AppointmentStatus;
import com.biit.appointment.persistence.entities.ExaminationType;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface CustomAppointmentRepository {

    /**
     * Find all appointments that match the search parameters. If startTime and endTime are defined, will search any appointment inside this range.
     *
     * @param organizationId      the organization of the parameters (can be null for any organization).
     * @param organizer           who must resolve the appointment (can be null for any organizer).
     * @param examinationTypes    a collection of types of the appointment (can be null for any type).
     * @param appointmentStatuses the status of the appointment (can be null for any status or a list).
     * @param lowerTimeBoundary   the lower limit on time for searching an appointment (can be null for no limit).
     * @param upperTimeBoundary   the upper limit on time for searching an appointment (can be null for no limit).
     * @param deleted             the appointment is deleted or not.
     * @return a list of appointments.
     */
    List<Appointment> findBy(
            @Param("organizationId") String organizationId,
            @Param("organizer") UUID organizer,
            @Param("attendee") UUID attendee,
            @Param("createdBy") String createdBy,
            @Param("examinationTypes") Collection<ExaminationType> examinationTypes,
            @Param("appointmentStatuses") Collection<AppointmentStatus> appointmentStatuses,
            @Param("lowerTimeBoundary") LocalDateTime lowerTimeBoundary,
            @Param("upperTimeBoundary") LocalDateTime upperTimeBoundary,
            @Param("deleted") Boolean deleted);


    /**
     * Counts the total appointments that match the search parameters. If startTime and endTime are defined, will search any appointment inside this range.
     *
     * @param organizationId      the organization of the parameters (can be null for any organization).
     * @param organizer           who must resolve the appointment (can be null for any organizer).
     * @param examinationTypes    the type of the appointment (can be null for any type).
     * @param appointmentStatuses the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary   the lower limit on time for searching an appointment (can be null for no limit).
     * @param upperTimeBoundary   the upper limit on time for searching an appointment (can be null for no limit).
     * @param deleted             the appointment is deleted or not.
     * @return the total number of appointments
     */
    long count(
            @Param("organizationId") String organizationId,
            @Param("organizer") UUID organizer,
            @Param("attendee") UUID attendee,
            @Param("createdBy") String createdBy,
            @Param("examinationTypes") Collection<ExaminationType> examinationTypes,
            @Param("appointmentStatuses") Collection<AppointmentStatus> appointmentStatuses,
            @Param("lowerTimeBoundary") LocalDateTime lowerTimeBoundary,
            @Param("upperTimeBoundary") LocalDateTime upperTimeBoundary,
            @Param("deleted") Boolean deleted);
}
