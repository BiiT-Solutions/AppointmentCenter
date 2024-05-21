package com.biit.appointment.rest.api;

import com.biit.appointment.core.controllers.AppointmentController;
import com.biit.appointment.core.converters.AppointmentConverter;
import com.biit.appointment.core.converters.models.AppointmentConverterRequest;
import com.biit.appointment.core.exceptions.InvalidParameterException;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.models.AppointmentTemplateDTO;
import com.biit.appointment.core.models.AttendanceRequest;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentStatus;
import com.biit.appointment.persistence.repositories.AppointmentRepository;
import com.biit.server.exceptions.UserNotFoundException;
import com.biit.server.rest.ElementServices;
import com.biit.server.rest.SecurityService;
import com.biit.server.security.IAuthenticatedUser;
import com.biit.server.security.IAuthenticatedUserProvider;
import com.biit.server.security.ISecurityController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/appointments")
public class AppointmentServices extends ElementServices<Appointment, Long, AppointmentDTO, AppointmentRepository,
        AppointmentProvider, AppointmentConverterRequest, AppointmentConverter, AppointmentController> {

    private final IAuthenticatedUserProvider authenticatedUserProvider;
    private final ISecurityController securityController;
    private final SecurityService securityService;

    public AppointmentServices(AppointmentController controller, IAuthenticatedUserProvider authenticatedUserProvider,
                               ISecurityController securityController, SecurityService securityService) {
        super(controller);
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.securityController = securityController;
        this.securityService = securityService;
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets all appointments using some filters.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AppointmentDTO> findAll(@Parameter(description = "Id of an existing organization")
                                        @RequestParam(name = "organizationId") Optional<String> organizationId,
                                        @Parameter(description = "Id of an existing organizer")
                                        @RequestParam(name = "organizer") Optional<UUID> organizer,
                                        @Parameter(description = "Id of an existing customer")
                                        @RequestParam(name = "attendeeId") Optional<UUID> attendeeId,
                                        @Parameter(description = "Filter by different examinations types")
                                        @RequestParam(name = "examinationType") Optional<Collection<String>> examinationTypesNames,
                                        @Parameter(description = "Filter by appointment status")
                                        @RequestParam(name = "appointmentStatuses") Optional<Collection<AppointmentStatus>> appointmentStatuses,
                                        @Parameter(description = "Minimum time for the appointment")
                                        @RequestParam(name = "lowerTimeBoundary") Optional<LocalDateTime> lowerTimeBoundary,
                                        @Parameter(description = "Maximum time for the appointment")
                                        @RequestParam(name = "upperTimeBoundary") Optional<LocalDateTime> upperTimeBoundary,
                                        @Parameter(description = "If it is marked as deleted")
                                        @RequestParam(name = "deleted") Optional<Boolean> deleted,
                                        HttpServletRequest request) {
        return getController().findByWithExaminationTypeNames(organizationId.orElse(null), organizer.orElse(null), attendeeId.orElse(null),
                examinationTypesNames.orElse(null), appointmentStatuses.orElse(null), lowerTimeBoundary.orElse(null),
                upperTimeBoundary.orElse(null), deleted.orElse(null));
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets all appointments using some filters.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/find/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public long countAll(@Parameter(description = "Id of an existing organization")
                         @RequestParam(name = "organizationId") Optional<String> organizationId,
                         @Parameter(description = "Id of an existing organizer")
                         @RequestParam(name = "organizer") Optional<UUID> organizer,
                         @Parameter(description = "Id of an existing customer")
                         @RequestParam(name = "attendeeId") Optional<UUID> attendeeId,
                         @Parameter(description = "Filter by different examinations types")
                         @RequestParam(name = "examinationTypes") Optional<Collection<String>> examinationTypesNames,
                         @Parameter(description = "Filter by appointment status")
                         @RequestParam(name = "appointmentStatuses") Optional<Collection<AppointmentStatus>> appointmentStatuses,
                         @Parameter(description = "Minimum time for the appointment")
                         @RequestParam(name = "lowerTimeBoundary") Optional<LocalDateTime> lowerTimeBoundary,
                         @Parameter(description = "Maximum time for the appointment")
                         @RequestParam(name = "upperTimeBoundary") Optional<LocalDateTime> upperTimeBoundary,
                         @Parameter(description = "If it is marked as deleted")
                         @RequestParam(name = "deleted") Optional<Boolean> deleted,
                         HttpServletRequest request) {
        return getController().countByWithExaminationTypeNames(organizationId.orElse(null), organizer.orElse(null), attendeeId.orElse(null),
                examinationTypesNames.orElse(null), appointmentStatuses.orElse(null), lowerTimeBoundary.orElse(null),
                upperTimeBoundary.orElse(null), deleted.orElse(null));
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Checks if the provided appointment collides with any other appointment. An appointment collides if is defined for "
            + "the same organizer at the same time.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/overlaps", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean overlaps(@RequestBody AppointmentDTO appointment, HttpServletRequest request) {
        return getController().overlaps(appointment);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Adds an speaker to an appointment. The speaker must have a Professional Specialization required on the appointment",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/speakers/{speakerUUID}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AppointmentDTO addSpeaker(@Parameter(description = "UUID of an existing user")
                                     @PathVariable(name = "speakerUUID") UUID speakerId,
                                     @RequestBody AppointmentDTO appointment, Authentication authentication, HttpServletRequest request) {
        return getController().addSpeaker(appointment, speakerId, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Adds an speaker to an appointment. The speaker must have a Professional Specialization required on the appointment",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "{appointmentId}/speakers/{speakerUUID}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AppointmentDTO addSpeaker(@Parameter(description = "Id of the appointment.")
                                     @PathVariable(name = "appointmentId") Long appointmentId,
                                     @Parameter(description = "Id of an existing user.")
                                     @PathVariable(name = "speakerUUID") UUID speakerUUID,
                                     Authentication authentication, HttpServletRequest request) {
        return getController().addSpeaker(appointmentId, speakerUUID, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Generates an appointment from a template.", security = @SecurityRequirement(name = "bearerAuth"))
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/templates/starting-time/{startingTime}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public AppointmentDTO fromTemplate(@Parameter(description = "Starting time of the appointment (yyyy-MM-dd hh:mm)")
                                       @PathVariable(name = "startingTime") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime startingTime,
                                       @RequestBody AppointmentTemplateDTO appointmentTemplateDTO, Authentication authentication, HttpServletRequest request) {
        final Optional<IAuthenticatedUser> user = authenticatedUserProvider.findByUsername(authentication.getName());
        if (user.isEmpty()) {
            throw new UserNotFoundException(this.getClass(), "No user exists with username '" + authentication.getName() + "'.");
        }
        return getController().create(appointmentTemplateDTO, startingTime, UUID.fromString(user.get().getUID()), authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets all appointments from a template.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/templates/{templateId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AppointmentDTO> getFromTemplate(@Parameter(description = "Template's id")
                                                @PathVariable(name = "templateId") Long templateId,
                                                Authentication authentication, HttpServletRequest request) {
        return getController().findByAppointmentTemplatesIn(Collections.singletonList(templateId));
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets all appointments from a list of templates.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/templates", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AppointmentDTO> getFromTemplateIds(@Parameter(description = "List of templates' ids")
                                                   @RequestParam(name = "templateId") Optional<Collection<Long>> templateIds,
                                                   Authentication authentication, HttpServletRequest request) {
        if (templateIds.isEmpty()) {
            throw new InvalidParameterException(this.getClass(), "You need to provide at least one template id!");
        }
        return getController().findByAppointmentTemplatesIn(templateIds.get());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets all appointments organizer by a user.", security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping(value = {"/organizers/{organizerUUID}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AppointmentDTO> getByOrganizer(@Parameter(description = "Id of an existing organizer", required = true)
                                               @PathVariable("organizerUUID") UUID organizerUUID, Authentication authentication, HttpServletRequest request) {
        securityController.checkIfCanSeeUserData(authentication.getName(), organizerUUID, securityService.getAdminPrivilege());
        return getController().getByOrganizer(organizerUUID);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets all appointments from an organization.", security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping(value = {"/organizations/{organizationId}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AppointmentDTO> getByOrganizationId(@Parameter(description = "Id of an existing organization", required = true)
                                                    @PathVariable("organizationId") String organizationId, Authentication authentication,
                                                    HttpServletRequest request) {
        return getController().getByOrganizationId(organizationId);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets all appointments from an attendee.", security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping(value = {"/attendees/{attendeeUUID}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AppointmentDTO> getByAttendeeId(@Parameter(description = "Id of an existing attendee", required = true)
                                                @PathVariable("attendeeUUID") UUID attendeeUUID, Authentication authentication, HttpServletRequest request) {
        securityController.checkIfCanSeeUserData(authentication.getName(), attendeeUUID, securityService.getAdminPrivilege());
        return getController().getByAttendeesIds(Collections.singletonList(attendeeUUID));
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets all appointments from an attendee.", security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping(value = {"/attendees/{attendeeUUID}/template/{appointmentTemplateId}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AppointmentDTO> getByAttendeeIdAndTemplate(@Parameter(description = "Id of an existing attendee", required = true)
                                                           @PathVariable("attendeeUUID") UUID attendeeUUID,
                                                           @Parameter(description = "Id of an existing template", required = true)
                                                           @PathVariable("appointmentTemplateId") Long appointmentTemplateId,
                                                           Authentication authentication, HttpServletRequest request) {
        securityController.checkIfCanSeeUserData(authentication.getName(), attendeeUUID, securityService.getAdminPrivilege());
        return getController().getByAttendeesIdsAndTemplates(Collections.singleton(attendeeUUID), Collections.singleton(appointmentTemplateId));
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets current appointment from the user. If one appointment is currently on execution, get this one, "
            + "if not, get the last one at the past, if not the first one at the future.",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping(value = {"/template/{appointmentTemplateId}/next"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public AppointmentDTO getByAttendeeIdAndTemplateCurrent(@Parameter(description = "Id of an existing template", required = true)
                                                            @PathVariable("appointmentTemplateId") Long appointmentTemplateId,
                                                            Authentication authentication, HttpServletRequest request) {
        return getController().getCurrentByAttendeeAndTemplates(authentication.getName(), Collections.singleton(appointmentTemplateId));
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets current appointment from a selected user. If one appointment is currently on execution, get this one, "
            + "if not, get the last one at the past, if not the first one at the future.",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping(value = {"/template/{appointmentTemplateId}/attendee/{attendeeUUID}/next"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public AppointmentDTO getByAttendeeNameAndTemplateCurrent(@Parameter(description = "Id of an existing template", required = true)
                                                              @PathVariable("appointmentTemplateId") Long appointmentTemplateId,
                                                              @Parameter(description = "Id of an existing attendee", required = true)
                                                              @PathVariable("attendeeUUID") UUID attendeeUUID,
                                                              Authentication authentication, HttpServletRequest request) {
        return getController().getCurrentByAttendeeAndTemplates(attendeeUUID, Collections.singleton(appointmentTemplateId));
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets current appointment from a selected user. If one appointment is currently on execution, get this one, "
            + "if not, get the last one at the past, if not the first one at the future.",
            security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping(value = {"/template/title/{appointmentTemplateTitle}/attendee/{attendeeUUID}/next"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public AppointmentDTO getByAttendeeNameAndTemplateCurrent(@Parameter(description = "Name of an existing template", required = true)
                                                              @PathVariable("appointmentTemplateTitle") String appointmentTemplateTitle,
                                                              @Parameter(description = "Id of an existing attendee", required = true)
                                                              @PathVariable("attendeeUUID") UUID attendeeUUID,
                                                              Authentication authentication, HttpServletRequest request) {
        return getController().getCurrentByAttendeeAndTemplatesNames(attendeeUUID, Collections.singleton(appointmentTemplateTitle));
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Subscribes current logged in user into the appointment.", security = {@SecurityRequirement(name = "bearerAuth")})
    @PutMapping(value = "/{appointmentId}/attendees/subscribe", produces = MediaType.APPLICATION_JSON_VALUE)
    public AppointmentDTO subscribe(@Parameter(description = "Id of the appointment.")
                                    @PathVariable(name = "appointmentId") Long appointmentId,
                                    Authentication authentication, HttpServletRequest request) {
        return getController().subscribe(appointmentId, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Unsubscribes current logged in user from the appointment.", security = {@SecurityRequirement(name = "bearerAuth")})
    @PutMapping(value = "/{appointmentId}/attendees/unsubscribe", produces = MediaType.APPLICATION_JSON_VALUE)
    public AppointmentDTO unsubscribe(@Parameter(description = "Id of the appointment.")
                                      @PathVariable(name = "appointmentId") Long appointmentId,
                                      Authentication authentication, HttpServletRequest request) {
        return getController().unsubscribe(appointmentId, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Mark an attendee as has been present on an appointment.", security = {@SecurityRequirement(name = "bearerAuth")})
    @PutMapping(value = "/{appointmentId}/attendees/{attendeeUUID}/attend", produces = MediaType.APPLICATION_JSON_VALUE)
    public AppointmentDTO attend(@Parameter(description = "Id of the appointment.")
                                 @PathVariable(name = "appointmentId") Long appointmentId,
                                 @Parameter(description = "Id of an existing attendee", required = true)
                                 @PathVariable("attendeeUUID") UUID attendeeUUID,
                                 Authentication authentication, HttpServletRequest request) {
        return getController().attend(appointmentId, attendeeUUID, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Mark an attendee as has been present on an appointment.", security = {@SecurityRequirement(name = "bearerAuth")})
    @PutMapping(value = "/attend", produces = MediaType.APPLICATION_JSON_VALUE)
    public AppointmentDTO attend(@RequestBody AttendanceRequest attendanceRequest,
                                 Authentication authentication, HttpServletRequest request) {
        return getController().attend(attendanceRequest, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Checks if current logged in user is already attending the event.", security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping(value = "/{appointmentId}/attending", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void isAttending(@Parameter(description = "Id of the appointment.")
                            @PathVariable(name = "appointmentId") Long appointmentId,
                            Authentication authentication, HttpServletRequest request) {
        getController().isAttending(appointmentId, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Checks if a user is already attending the event.", security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping(value = "/{appointmentId}/attending/{attendeeUUID}", produces = MediaType.APPLICATION_JSON_VALUE)
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
    @PutMapping(value = "/attend/text", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.TEXT_PLAIN_VALUE)
    public AppointmentDTO attend(@RequestBody String attendanceRequest,
                                 Authentication authentication, HttpServletRequest request) {
        return getController().attend(attendanceRequest, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Remove an attendee as has been present on an appointment.", security = {@SecurityRequirement(name = "bearerAuth")})
    @PutMapping(value = "/{appointmentId}/attendees/{attendeeUUID}/unattend", produces = MediaType.APPLICATION_JSON_VALUE)
    public AppointmentDTO unattend(@Parameter(description = "Id of the appointment.")
                                   @PathVariable(name = "appointmentId") Long appointmentId,
                                   @Parameter(description = "Id of an existing attendee", required = true)
                                   @PathVariable("attendeeUUID") UUID attendeeUUID,
                                   Authentication authentication, HttpServletRequest request) {
        return getController().unattend(appointmentId, attendeeUUID, authentication.getName());
    }
}
