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

import com.biit.appointment.core.controllers.ScheduleRangeExclusionController;
import com.biit.appointment.core.converters.ScheduleRangeExclusionConverter;
import com.biit.appointment.core.converters.models.ScheduleRangeExclusionConverterRequest;
import com.biit.appointment.core.models.ScheduleRangeExclusionDTO;
import com.biit.appointment.core.providers.ScheduleRangeExclusionProvider;
import com.biit.appointment.persistence.entities.ScheduleRangeExclusion;
import com.biit.appointment.persistence.repositories.ScheduleRangeExclusionRepository;
import com.biit.server.rest.ElementServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/availabilities/exceptions/")
public class ScheduleRangeExclusionServices extends ElementServices<ScheduleRangeExclusion, Long, ScheduleRangeExclusionDTO, ScheduleRangeExclusionRepository,
        ScheduleRangeExclusionProvider, ScheduleRangeExclusionConverterRequest, ScheduleRangeExclusionConverter, ScheduleRangeExclusionController> {

    protected ScheduleRangeExclusionServices(ScheduleRangeExclusionController controller) {
        super(controller);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets your availability exceptions.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/users/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ScheduleRangeExclusionDTO> getAvailabilityExceptions(Authentication authentication,
                                                                     HttpServletRequest request) {
        return getController().getFromUser(authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets the availability exceptions from a user.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/users/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ScheduleRangeExclusionDTO> getAvailabilityExceptions(@Parameter(description = "UUID of the user.", required = true)
                                                                     @PathVariable(name = "uuid") UUID uuid,
                                                                     Authentication authentication,
                                                                     HttpServletRequest request) {
        return getController().getFromUser(uuid);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege,@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Add an availability exception to your schedule.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/users/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public void addAvailabilityExceptions(@Valid @RequestBody Collection<ScheduleRangeExclusionDTO> scheduleRangeExclusionDTOS,
                                          Authentication authentication,
                                          HttpServletRequest request) {
        getController().add(scheduleRangeExclusionDTOS, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Add an availability exception to a user's schedule.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/users/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void addAvailabilityExceptions(@Parameter(description = "UUID of the user.", required = true)
                                          @PathVariable(name = "uuid") UUID uuid,
                                          @Valid @RequestBody Collection<ScheduleRangeExclusionDTO> scheduleRangeExclusionDTOS,
                                          Authentication authentication,
                                          HttpServletRequest request) {
        getController().add(scheduleRangeExclusionDTOS, uuid, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Sets the availability exceptions for your schedule.", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(value = "/users/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public void setScheduleRange(Authentication authentication,
                                 @Valid @RequestBody Collection<ScheduleRangeExclusionDTO> scheduleRangeExclusionDTOS,
                                 HttpServletRequest request) {
        getController().set(scheduleRangeExclusionDTOS, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Sets the availability exceptions for a user.", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(value = "/users/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void setScheduleRange(@Parameter(description = "UUID of the user.", required = true)
                                 @PathVariable(name = "uuid") UUID uuid,
                                 @Valid @RequestBody Collection<ScheduleRangeExclusionDTO> scheduleRangeExclusionDTOS,
                                 Authentication authentication,
                                 HttpServletRequest request) {
        getController().set(scheduleRangeExclusionDTOS, uuid, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege,@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Removes all availability exceptions from your user."
            + "the provided range to delete.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(value = "/users/me/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public void removeScheduleRange(
            Authentication authentication,
            HttpServletRequest request) {
        getController().delete(authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Removes all availability exceptions from a user."
            + "the provided range to delete.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(value = "/users/{uuid}/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public void removeScheduleRange(@Parameter(description = "UUID of the user.", required = true)
                                    @PathVariable(name = "uuid") UUID uuid,
                                    Authentication authentication,
                                    HttpServletRequest request) {
        getController().delete(uuid, authentication.getName());
    }
}
