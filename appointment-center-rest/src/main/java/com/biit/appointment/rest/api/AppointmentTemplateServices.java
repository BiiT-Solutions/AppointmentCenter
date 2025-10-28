package com.biit.appointment.rest.api;

/*-
 * #%L
 * AppointmentCenter (Rest)
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

import com.biit.appointment.core.controllers.AppointmentTemplateController;
import com.biit.appointment.core.converters.AppointmentTemplateConverter;
import com.biit.appointment.core.converters.models.AppointmentTemplateConverterRequest;
import com.biit.appointment.core.models.AppointmentTemplateAvailabilityDTO;
import com.biit.appointment.core.models.AppointmentTemplateDTO;
import com.biit.appointment.core.providers.AppointmentTemplateProvider;
import com.biit.appointment.persistence.entities.AppointmentTemplate;
import com.biit.appointment.persistence.repositories.AppointmentTemplateRepository;
import com.biit.server.providers.StorableObjectProvider;
import com.biit.server.rest.ElementServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/appointment-templates")
public class AppointmentTemplateServices extends ElementServices<AppointmentTemplate, Long, AppointmentTemplateDTO, AppointmentTemplateRepository,
        AppointmentTemplateProvider, AppointmentTemplateConverterRequest, AppointmentTemplateConverter, AppointmentTemplateController> {

    public AppointmentTemplateServices(AppointmentTemplateController controller) {
        super(controller);
    }

    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets an entity.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/{id}/viewers", produces = MediaType.APPLICATION_JSON_VALUE)
    public AppointmentTemplateDTO getByViewers(@Parameter(description = "Id of an existing application", required = true) @PathVariable("id") Long id,
                                               Authentication authentication, HttpServletRequest request) {
        return getController().get(id);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets all templates from an organization.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/organization/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AppointmentTemplateDTO> findByOrganizationId(@Parameter(description = "Id of an existing organization.")
                                                             @PathVariable(name = "organizationId") String organizationId, HttpServletRequest request) {
        return getController().findByOrganizationId(organizationId);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets all templates from an attendee.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/appointments/attendee/{attendeeUUID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AppointmentTemplateDTO> findByAttendeeOnAppointment(@Parameter(description = "Id of an existing attendee", required = true)
                                                                    @PathVariable("attendeeUUID") UUID attendeeUUID, HttpServletRequest request) {
        return getController().findByAttendeeOnAppointment(attendeeUUID);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets all templates from an attendee.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/appointments/nonattendee/{nonattendeeUUID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AppointmentTemplateDTO> findByNonAttendeeOnAppointment(@Parameter(description = "Id of an existing nonattendee", required = true)
                                                                       @PathVariable("nonattendeeUUID") UUID attendeeUUID, HttpServletRequest request) {
        return getController().findByNonAttendeeOnAppointment(attendeeUUID);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets the schedule from a collection of templates.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/lower-time-boundary/{lowerTimeBoundary}/upper-time-boundary/{upperTimeBoundary}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AppointmentTemplateAvailabilityDTO> schedule(
            @Parameter(description = "Minimum time for the appointment (yyyy-MM-dd hh:mm).")
            @PathVariable(name = "lowerTimeBoundary") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
            LocalDateTime lowerTimeBoundary,
            @Parameter(description = "Maximum time for the appointment (yyyy-MM-dd hh:mm).")
            @PathVariable(name = "upperTimeBoundary") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
            LocalDateTime upperTimeBoundary,
            @Parameter(description = "List of templates' ids.")
            @RequestParam(value = "templateId") Long[] templatesId,
            HttpServletRequest request) {
        return getController().schedule(lowerTimeBoundary, upperTimeBoundary, templatesId);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets all, but viewers has permissions", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/viewers", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AppointmentTemplateDTO> getAllByViewers(
            @RequestParam(name = "page", defaultValue = "0") Optional<Integer> page,
            @RequestParam(name = "size", defaultValue = StorableObjectProvider.MAX_PAGE_SIZE + "") Optional<Integer> size,
            Authentication authentication, HttpServletRequest request) {
        return super.getAll(page, size, authentication, request);
    }


}
