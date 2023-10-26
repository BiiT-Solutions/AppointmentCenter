package com.biit.appointment.rest.api;

import com.biit.appointment.core.controllers.AppointmentController;
import com.biit.appointment.core.converters.AppointmentConverter;
import com.biit.appointment.core.converters.models.AppointmentConverterRequest;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentStatus;
import com.biit.appointment.persistence.repositories.AppointmentRepository;
import com.biit.server.rest.ElementServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/appointments")
public class AppointmentServices extends ElementServices<Appointment, Long, AppointmentDTO, AppointmentRepository,
        AppointmentProvider, AppointmentConverterRequest, AppointmentConverter, AppointmentController> {

    public AppointmentServices(AppointmentController controller) {
        super(controller);
    }

    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets all appointments from an organizer.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/organizer/{organizerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AppointmentDTO> findByOrganizerId(@Parameter(description = "Id of an existing organizer", required = true)
                                                  @PathVariable("organizerId") Long organizerId, HttpServletRequest request) {
        return getController().findByOrganizerId(organizerId);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets all appointments using some filters.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/find", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AppointmentDTO> findAll(@Parameter(description = "Id of an existing organization")
                                        @RequestParam(name = "organizationId") Optional<Long> organizationId,
                                        @Parameter(description = "Id of an existing organizer")
                                        @RequestParam(name = "organizerId") Optional<Long> organizerId,
                                        @Parameter(description = "Id of an existing customer")
                                        @RequestParam(name = "customerId") Optional<Long> customerId,
                                        @Parameter(description = "Filter by different examinations types")
                                        @RequestParam(name = "examinationType") Optional<Collection<String>> examinationTypesNames,
                                        @Parameter(description = "Filter by appointment status")
                                        @RequestParam(name = "appointmentStatus") Optional<AppointmentStatus> appointmentStatus,
                                        @Parameter(description = "Minimum time for the appointment")
                                        @RequestParam(name = "lowerTimeBoundary") Optional<LocalDateTime> lowerTimeBoundary,
                                        @Parameter(description = "Maximum time for the appointment")
                                        @RequestParam(name = "upperTimeBoundary") Optional<LocalDateTime> upperTimeBoundary,
                                        @Parameter(description = "If it is marked as deleted")
                                        @RequestParam(name = "deleted") Optional<Boolean> deleted,
                                        HttpServletRequest request) {
        return getController().findByUsingNames(organizationId.orElse(null), organizerId.orElse(null), customerId.orElse(null),
                examinationTypesNames.orElse(new ArrayList<>()), appointmentStatus.orElse(null), lowerTimeBoundary.orElse(null),
                upperTimeBoundary.orElse(null), deleted.orElse(null));
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets all appointments using some filters.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/find/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public long countAll(@Parameter(description = "Id of an existing organization")
                         @RequestParam(name = "organizationId") Optional<Long> organizationId,
                         @Parameter(description = "Id of an existing organizer")
                         @RequestParam(name = "organizerId") Optional<Long> organizerId,
                         @Parameter(description = "Id of an existing customer")
                         @RequestParam(name = "customerId") Optional<Long> customerId,
                         @Parameter(description = "Filter by different examinations types")
                         @RequestParam(name = "examinationTypes") Optional<Collection<String>> examinationTypesNames,
                         @Parameter(description = "Filter by appointment status")
                         @RequestParam(name = "appointmentStatus") Optional<AppointmentStatus> appointmentStatus,
                         @Parameter(description = "Minimum time for the appointment")
                         @RequestParam(name = "lowerTimeBoundary") Optional<LocalDateTime> lowerTimeBoundary,
                         @Parameter(description = "Maximum time for the appointment")
                         @RequestParam(name = "upperTimeBoundary") Optional<LocalDateTime> upperTimeBoundary,
                         @Parameter(description = "If it is marked as deleted")
                         @RequestParam(name = "deleted") Optional<Boolean> deleted,
                         HttpServletRequest request) {
        return getController().countUsingNames(organizationId.orElse(null), organizerId.orElse(null), customerId.orElse(null),
                examinationTypesNames.orElse(new ArrayList<>()), appointmentStatus.orElse(null), lowerTimeBoundary.orElse(null),
                upperTimeBoundary.orElse(null), deleted.orElse(null));
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Checks if the provided appointment collides with any other appointment. An appointment collides if is defined for "
            + "the same organizer at the same time.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/overlaps", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean overlaps(@RequestBody AppointmentDTO appointment, HttpServletRequest request) {
        return getController().overlaps(appointment);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a list of appointments that are defined before the provided appointment.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/past", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<AppointmentDTO> getPrevious(@RequestBody AppointmentDTO appointment, HttpServletRequest request) {
        return getController().getPrevious(appointment);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a list of appointments that are defined on the past.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/past/organizations/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AppointmentDTO> getPrevious(@Parameter(description = "Id of an existing organization")
                                            @PathVariable(name = "organizationId") Long organizationId,
                                            @Parameter(description = "Filter by examination type")
                                            @RequestParam(name = "examinationTypeName") Optional<String> examinationTypeName,
                                            HttpServletRequest request) {
        return getController().getPrevious(organizationId, examinationTypeName.orElse(null));
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a list of appointments that are defined after the provided appointment.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/future", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<AppointmentDTO> getNext(@RequestBody AppointmentDTO appointment, HttpServletRequest request) {
        return getController().getNext(appointment);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a list of appointments that are defined on the future.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/future/organizations/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AppointmentDTO> getNext(@Parameter(description = "Id of an existing organization")
                                        @PathVariable(name = "organizationId") Long organizationId,
                                        @Parameter(description = "Filter by examination type")
                                        @RequestParam(name = "examinationTypeName") Optional<String> examinationTypeName,
                                        HttpServletRequest request) {
        return getController().getNext(organizationId, examinationTypeName.orElse(null));
    }
}
