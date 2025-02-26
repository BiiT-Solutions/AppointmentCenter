package com.biit.appointment.core.controllers;

import com.biit.appointment.core.converters.AttendanceConverter;
import com.biit.appointment.core.converters.models.AttendanceConverterRequest;
import com.biit.appointment.core.exceptions.AppointmentNotFoundException;
import com.biit.appointment.core.exceptions.AttendanceNotFoundException;
import com.biit.appointment.core.exceptions.InvalidParameterException;
import com.biit.appointment.core.exceptions.YouAreAlreadyOnThisAppointmentException;
import com.biit.appointment.core.exceptions.YouAreNotOnThisAppointmentException;
import com.biit.appointment.core.models.AttendanceDTO;
import com.biit.appointment.core.converters.models.AttendanceRequest;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.core.providers.AttendanceProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.Attendance;
import com.biit.appointment.persistence.repositories.AttendanceRepository;
import com.biit.server.controller.ElementController;
import com.biit.server.exceptions.UserNotFoundException;
import com.biit.server.security.IAuthenticatedUser;
import com.biit.server.security.IAuthenticatedUserProvider;
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

    private final IAuthenticatedUserProvider authenticatedUserProvider;
    private final AppointmentProvider appointmentProvider;

    protected AttendanceController(AttendanceProvider provider, AttendanceConverter converter,
                                   IAuthenticatedUserProvider authenticatedUserProvider,
                                   AppointmentProvider appointmentProvider) {
        super(provider, converter);
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.appointmentProvider = appointmentProvider;
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

    public List<AttendanceDTO> findByAppointment(Appointment appointment) {
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

        getProvider().findBy(UUID.fromString(user.getUID()), appointment).orElseThrow(() ->
                new AttendanceNotFoundException(this.getClass(), "User '" + user + "' with UUID '" + user.getUID() + "' has not passed the QR code."));
    }


    public void unattend(Long appointmentId, UUID userUUID, String createdBy) {
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
