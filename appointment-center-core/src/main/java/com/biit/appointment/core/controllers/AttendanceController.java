package com.biit.appointment.core.controllers;

import com.biit.appointment.core.converters.AttendanceConverter;
import com.biit.appointment.core.converters.models.AttendanceConverterRequest;
import com.biit.appointment.core.exceptions.AppointmentNotFoundException;
import com.biit.appointment.core.exceptions.AttendanceNotFoundException;
import com.biit.appointment.core.models.AttendanceDTO;
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
import java.util.UUID;

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

}
