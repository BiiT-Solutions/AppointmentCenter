package com.biit.appointment.core.controllers;

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

import com.biit.appointment.core.converters.AppointmentConverter;
import com.biit.appointment.core.converters.AttendanceConverter;
import com.biit.appointment.core.converters.models.AttendanceConverterRequest;
import com.biit.appointment.core.converters.models.AttendanceRequest;
import com.biit.appointment.core.exceptions.AppointmentNotFoundException;
import com.biit.appointment.core.exceptions.AttendanceNotFoundException;
import com.biit.appointment.core.exceptions.InvalidParameterException;
import com.biit.appointment.core.exceptions.YouAreAlreadyOnThisAppointmentException;
import com.biit.appointment.core.exceptions.YouAreNotOnThisAppointmentException;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.models.AttendanceDTO;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.core.providers.AttendanceProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.Attendance;
import com.biit.appointment.persistence.repositories.AttendanceRepository;
import com.biit.server.controller.ElementController;
import com.biit.server.exceptions.UserNotFoundException;
import com.biit.server.security.IAuthenticatedUserProvider;
import com.biit.server.security.IUserOrganizationProvider;
import com.biit.server.security.model.IAuthenticatedUser;
import com.biit.server.security.model.IUserOrganization;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class AttendanceController extends ElementController<Attendance, Long, AttendanceDTO, AttendanceRepository,
        AttendanceProvider, AttendanceConverterRequest, AttendanceConverter> {

    private final IAuthenticatedUserProvider<? extends IAuthenticatedUser> authenticatedUserProvider;
    private final AppointmentProvider appointmentProvider;
    private final AppointmentConverter appointmentConverter;

    protected AttendanceController(AttendanceProvider provider, AttendanceConverter converter,
                                   IAuthenticatedUserProvider<? extends IAuthenticatedUser> authenticatedUserProvider,
                                   AppointmentProvider appointmentProvider,
                                   AppointmentConverter appointmentConverter,
                                   List<IUserOrganizationProvider<? extends IUserOrganization>> userOrganizationProvider) {
        super(provider, converter, userOrganizationProvider);
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.appointmentProvider = appointmentProvider;
        this.appointmentConverter = appointmentConverter;
    }

    @Override
    protected AttendanceConverterRequest createConverterRequest(Attendance attendance) {
        return new AttendanceConverterRequest(attendance);
    }

    public List<AttendanceDTO> findByAppointment(Long appointmentId) {
        final Appointment appointment = appointmentProvider.findById(appointmentId).orElseThrow(() -> new AppointmentNotFoundException(this.getClass(),
                "Appointment with id '" + appointmentId + "' not found."));
        return findByAppointment(appointment);
    }

    public List<AttendanceDTO> findByAppointment(AppointmentDTO appointment) {
        return findByAppointment(appointmentConverter.reverse(appointment));
    }

    private List<AttendanceDTO> findByAppointment(Appointment appointment) {
        return convertAll(getProvider().findByAppointment(appointment));
    }

    public List<AttendanceDTO> findByAttendee(String username) {
        final IAuthenticatedUser user = authenticatedUserProvider.findByUsername(username).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "No user found with username '" + username + "'."));
        return findByAttendee(UUID.fromString(user.getUID()));
    }

    public List<AttendanceDTO> findByAttendee(UUID attendee) {
        return convertAll(getProvider().findByAttendee(attendee));
    }

    public AttendanceDTO findBy(UUID attendee, Long appointmentId) {
        final Appointment appointment = appointmentProvider.findById(appointmentId).orElseThrow(() ->
                new AppointmentNotFoundException(this.getClass(),
                        "Appointment with id '" + appointmentId + "' not found."));
        return findBy(attendee, appointment);
    }

    public AttendanceDTO findBy(UUID attendee, Appointment appointment) {
        return convert(getProvider().findBy(attendee, appointment).orElseThrow(() ->
                new AttendanceNotFoundException(this.getClass(), "Attendance not found!")));
    }

    public void attend(Long appointmentId, String attendanceRequest, String createdBy) {
        attend(appointmentId, AttendanceRequest.decode(attendanceRequest), createdBy);
    }

    public void attend(Long appointmentId, AttendanceRequest attendanceRequest, String createdBy) {
        attend(appointmentId, attendanceRequest.getAppointmentId(), attendanceRequest.getAttender(), createdBy);
    }

    public void attend(Long currentAppointmentId, Long appointmentToAttendId, UUID userUUID, String createdBy) {

        if (!Objects.equals(currentAppointmentId, appointmentToAttendId)) {
            throw new InvalidParameterException(this.getClass(), "The QR obtained (" + appointmentToAttendId + ") is not valid for the selected appointment ("
                    + currentAppointmentId + ")!");
        }

        final IAuthenticatedUser user = authenticatedUserProvider.findByUID(userUUID.toString()).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "No user found with UUID '" + userUUID + "'."));

        final Appointment appointment = appointmentProvider.findById(appointmentToAttendId).orElseThrow(() ->
                new AppointmentNotFoundException(this.getClass(), "No appointment found with id '" + appointmentToAttendId + "'."));

        if (!appointment.getAttendees().contains(userUUID)) {
            throw new YouAreNotOnThisAppointmentException(this.getClass(), "User '" + user.getName() + "' is not on the attendees list!");
        }

        final Optional<Attendance> currenAttendance = getProvider().findBy(userUUID, appointment);
        if (currenAttendance.isPresent()) {
            throw new YouAreAlreadyOnThisAppointmentException(this.getClass(), "User '" + user.getName() + "' has already attended this appointment!");
        }
        final Attendance attendance = new Attendance(userUUID, appointment);
        attendance.setCreatedBy(createdBy);
        getProvider().save(attendance);
    }


    public void isAttending(Long appointmentId, String username) {
        if (username == null) {
            throw new UserNotFoundException(this.getClass(), "Username cannot be null.");
        }
        final IAuthenticatedUser user = authenticatedUserProvider.findByUsername(username).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "No user found with username '" + username + "'."));
        isAttending(appointmentId, user);
    }


    public void isAttending(Long appointmentId, UUID userUUID) {
        if (userUUID == null) {
            throw new UserNotFoundException(this.getClass(), "UUID cannot be null.");
        }
        final IAuthenticatedUser user = authenticatedUserProvider.findByUID(userUUID.toString()).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "No user found with UUID '" + userUUID + "'."));
        isAttending(appointmentId, user);
    }


    public void isAttending(Long appointmentId, IAuthenticatedUser user) {
        final Appointment appointment = appointmentProvider.findById(appointmentId).orElseThrow(() ->
                new AppointmentNotFoundException(this.getClass(), "No appointment found with id '" + appointmentId + "'."));

        if (getProvider().findBy(UUID.fromString(user.getUID()), appointment).isEmpty()) {
            throw new AttendanceNotFoundException(this.getClass(), "User '" + user + "' with UUID '" + user.getUID() + "' has not passed the QR code on '"
                    + appointmentId + "'.");
        }
    }


    public void unattend(Long appointmentId, UUID userUUID) {
        final IAuthenticatedUser user = authenticatedUserProvider.findByUID(userUUID.toString()).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "No user found with UUID '" + userUUID + "'."));

        final Appointment appointment = appointmentProvider.findById(appointmentId).orElseThrow(() ->
                new AppointmentNotFoundException(this.getClass(), "No appointment found with id '" + appointmentId + "'."));

        if (!appointment.getAttendees().contains(userUUID)) {
            throw new YouAreNotOnThisAppointmentException(this.getClass(), "User '" + user.getName() + "' is not on the attendees list!");
        }

        final Set<Attendance> attendances = getProvider().findByAppointment(appointment);

        //Check you are attending!
        final Attendance userAttendance = attendances.stream().filter(attendance -> Objects.equals(attendance.getAttendee(), userUUID)).findFirst()
                .orElseThrow(() -> new YouAreNotOnThisAppointmentException(this.getClass(),
                        "User '" + user.getName() + "' is not attending this appointment!"));
        getProvider().delete(userAttendance);

        appointment.setAttendances(attendances.stream().filter(
                attendance -> !Objects.equals(attendance.getAttendee(), userUUID)
        ).collect(Collectors.toSet()));
    }

}
