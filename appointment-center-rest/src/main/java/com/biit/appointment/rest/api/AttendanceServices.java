package com.biit.appointment.rest.api;

import com.biit.appointment.core.controllers.AttendanceController;
import com.biit.appointment.core.converters.AttendanceConverter;
import com.biit.appointment.core.converters.models.AttendanceConverterRequest;
import com.biit.appointment.core.converters.models.AttendanceRequest;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.models.AttendanceDTO;
import com.biit.appointment.core.providers.AttendanceProvider;
import com.biit.appointment.persistence.entities.Attendance;
import com.biit.appointment.persistence.repositories.AttendanceRepository;
import com.biit.server.rest.ElementServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/attendances")
public class AttendanceServices extends ElementServices<Attendance, Long, AttendanceDTO, AttendanceRepository,
        AttendanceProvider, AttendanceConverterRequest, AttendanceConverter, AttendanceController> {


    protected AttendanceServices(AttendanceController controller) {
        super(controller);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets the attendance from an appointment.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/appointments", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<AttendanceDTO> findByAppointment(@Valid @RequestBody AppointmentDTO appointment) {
        return getController().findByAppointment(appointment);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets the attendance from an appointment.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/appointments/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
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


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Mark an attendee as has been present on an appointment.", security = {@SecurityRequirement(name = "bearerAuth")})
    @PutMapping(value = "/appointments/{appointmentId}/attendees/{attendeeUUID}/attend", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void attend(@Parameter(description = "Id of the appointment.")
                       @PathVariable(name = "appointmentId") Long appointmentId,
                       @Parameter(description = "Id of an existing attendee", required = true)
                       @PathVariable("attendeeUUID") UUID attendeeUUID,
                       Authentication authentication, HttpServletRequest request) {
        getController().attend(appointmentId, appointmentId, attendeeUUID, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Mark an attendee as has been present on an appointment.", security = {@SecurityRequirement(name = "bearerAuth")})
    @PutMapping(value = "/appointments/{appointmentId}/attend", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void attend(@Parameter(description = "Id of the appointment to check.")
                       @PathVariable(name = "appointmentId") Long appointmentId,
                       @Valid @RequestBody AttendanceRequest attendanceRequest,
                       Authentication authentication, HttpServletRequest request) {
        getController().attend(appointmentId, attendanceRequest, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Checks if current logged in user is already attending the event.", security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping(value = "/appointments/{appointmentId}/attending")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void isAttending(@Parameter(description = "Id of the appointment.")
                            @PathVariable(name = "appointmentId") Long appointmentId,
                            Authentication authentication, HttpServletRequest request) {
        getController().isAttending(appointmentId, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Checks if a user is already attending the event.", security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping(value = "/appointments/{appointmentId}/attending/{attendeeUUID}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void isAttending(@Parameter(description = "Id of the appointment.")
                            @PathVariable(name = "appointmentId") Long appointmentId,
                            @Parameter(description = "Id of an existing attendee", required = true)
                            @PathVariable("attendeeUUID") UUID attendeeUUID,
                            Authentication authentication, HttpServletRequest request) {
        getController().isAttending(appointmentId, attendeeUUID);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Mark an attendee as has been present on an appointment. Using the codified information from the QR.",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @PutMapping(value = "/appointments/{appointmentId}/attend/text", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void attend(@Parameter(description = "Id of the appointment to check.")
                       @PathVariable(name = "appointmentId") Long appointmentId,
                       @Valid @RequestBody String attendanceRequest,
                       Authentication authentication, HttpServletRequest request) {
        getController().attend(appointmentId, attendanceRequest, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Remove an attendee as has been present on an appointment.", security = {@SecurityRequirement(name = "bearerAuth")})
    @PutMapping(value = "/appointments/{appointmentId}/attendees/{attendeeUUID}/unattend", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unattend(@Parameter(description = "Id of the appointment.")
                         @PathVariable(name = "appointmentId") Long appointmentId,
                         @Parameter(description = "Id of an existing attendee", required = true)
                         @PathVariable("attendeeUUID") UUID attendeeUUID,
                         Authentication authentication, HttpServletRequest request) {
        getController().unattend(appointmentId, attendeeUUID);
    }


}
