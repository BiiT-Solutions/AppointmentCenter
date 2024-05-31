package com.biit.appointment.rest.api;

import com.biit.appointment.core.controllers.AttendanceController;
import com.biit.appointment.core.converters.AttendanceConverter;
import com.biit.appointment.core.converters.models.AttendanceConverterRequest;
import com.biit.appointment.core.models.AttendanceDTO;
import com.biit.appointment.core.providers.AttendanceProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.Attendance;
import com.biit.appointment.persistence.repositories.AttendanceRepository;
import com.biit.server.rest.ElementServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/attendances/")
public class AttendanceServices extends ElementServices<Attendance, Long, AttendanceDTO, AttendanceRepository,
        AttendanceProvider, AttendanceConverterRequest, AttendanceConverter, AttendanceController> {


    protected AttendanceServices(AttendanceController controller) {
        super(controller);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets the attendance from an appointment.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/appointments", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<AttendanceDTO> findByAppointment(@RequestBody Appointment appointment) {
        return getController().findByAppointment(appointment);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets the attendance from an appointment.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/appointments/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<AttendanceDTO> findByAppointment(@Parameter(description = "Id of the appointment")
                                                 @PathVariable(name = "appointmentId") Long appointmentId) {
        return getController().findByAppointment(appointmentId);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets the attendance from a user.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/attendees/{attendeeUUID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AttendanceDTO> findByAttendee(@Parameter(description = "UUID of the attendee")
                                              @PathVariable(name = "attendeeUUID") UUID attendee) {
        return getController().findByAttendee(attendee);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets the attendance from current logged in user.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/attendees", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AttendanceDTO> findByAttendee(Authentication authentication) {
        return getController().findByAttendee(authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets the attendance for a specific user and appointment.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/attendees/{attendeeUUID}/appointments/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AttendanceDTO findBy(@Parameter(description = "UUID of the attendee")
                                @PathVariable(name = "attendeeUUID") UUID attendee,
                                @Parameter(description = "Id of the appointment")
                                @PathVariable(name = "appointmentId") Long appointmentId) {
        return getController().findBy(attendee, appointmentId);
    }


}
