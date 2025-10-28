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

import com.biit.appointment.core.controllers.ScheduleController;
import com.biit.appointment.core.converters.ScheduleConverter;
import com.biit.appointment.core.converters.models.ScheduleConverterRequest;
import com.biit.appointment.core.models.ScheduleDTO;
import com.biit.appointment.core.models.ScheduleRangeDTO;
import com.biit.appointment.core.providers.ScheduleProvider;
import com.biit.appointment.persistence.entities.Schedule;
import com.biit.appointment.persistence.repositories.ScheduleRepository;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/schedules")
public class ScheduleServices extends ElementServices<Schedule, Long, ScheduleDTO, ScheduleRepository,
        ScheduleProvider, ScheduleConverterRequest, ScheduleConverter, ScheduleController> {

    public ScheduleServices(ScheduleController controller) {
        super(controller);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets default schedule.", description = "The default schedule is applied to any user that has no schedule defined."
            + "It is not persisted on database.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/default", produces = MediaType.APPLICATION_JSON_VALUE)
    public ScheduleDTO getDefaultSchedule(Authentication authentication,
                                          HttpServletRequest request) {
        return getController().getDefault();
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets your schedule.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/users/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ScheduleDTO getSchedule(Authentication authentication,
                                   HttpServletRequest request) {
        return getController().get(authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege,@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Add a range to your schedule.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/users/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ScheduleDTO addScheduleRange(@Valid @RequestBody Collection<ScheduleRangeDTO> scheduleRangeDTOs,
                                        Authentication authentication,
                                        HttpServletRequest request) {
        return getController().add(scheduleRangeDTOs, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Sets the set of ranges for your schedule.", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(value = "/users/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ScheduleDTO setScheduleRange(Authentication authentication,
                                        @Valid @RequestBody Collection<ScheduleRangeDTO> scheduleRangeDTOs,
                                        HttpServletRequest request) {
        return getController().set(scheduleRangeDTOs, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Sets a ranges for your schedule. If already exists, the range will be updated.", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(value = "/users/me/ranges", produces = MediaType.APPLICATION_JSON_VALUE)
    public ScheduleDTO setScheduleRange(Authentication authentication,
                                        @Valid @RequestBody ScheduleRangeDTO scheduleRangeDTO,
                                        HttpServletRequest request) {
        return getController().update(scheduleRangeDTO, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege,@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Removes an schedule range from your user. Any existing range will be adjusted or remove to not overlap."
            + "the provided range to delete.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(value = "/users/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ScheduleDTO removeScheduleRange(@Valid @RequestBody Collection<ScheduleRangeDTO> scheduleRangeDTOs,
                                           Authentication authentication,
                                           HttpServletRequest request) {
        return getController().remove(scheduleRangeDTOs, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege,@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Removes an schedule range from your user. Any existing range will be adjusted or remove to not overlap."
            + "the provided range to delete.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(value = "/users/me/ids", produces = MediaType.APPLICATION_JSON_VALUE)
    public void removeScheduleRange(@RequestParam(value = "id") List<Long> ids,
                                    Authentication authentication,
                                    HttpServletRequest request) {
        getController().removeScheduleRange(ids, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege,@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Removes all the schedule from your user. "
            + "the provided range to delete.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(value = "/users/me/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ScheduleDTO removeAllScheduleRange(Authentication authentication,
                                              HttpServletRequest request) {
        return getController().removeAll(authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets the schedule from a user.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/users/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ScheduleDTO getSchedule(@Parameter(description = "UUID of the user.", required = true)
                                   @PathVariable(name = "uuid") UUID uuid,
                                   Authentication authentication,
                                   HttpServletRequest request) {
        return getController().get(uuid);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Removes an schedule range from a user. Any existing range will be adjusted or remove to not overlap."
            + "the provided range to delete.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(value = "/users/{uuid}/ids", produces = MediaType.APPLICATION_JSON_VALUE)
    public void removeScheduleRange(@Parameter(description = "UUID of the user.", required = true)
                                    @PathVariable(name = "uuid") UUID uuid,
                                    @RequestParam(value = "id") List<Long> ids,
                                    Authentication authentication,
                                    HttpServletRequest request) {
        getController().removeScheduleRange(ids, uuid, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Add a range to the schedule of a user.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/users/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ScheduleDTO addScheduleRange(@Parameter(description = "UUID of the user.", required = true)
                                        @PathVariable(name = "uuid") UUID uuid,
                                        @Valid @RequestBody Collection<ScheduleRangeDTO> scheduleRangeDTOs,
                                        Authentication authentication,
                                        HttpServletRequest request) {
        return getController().add(scheduleRangeDTOs, uuid, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Sets the set of ranges for the schedule of a user.", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(value = "/users/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ScheduleDTO setScheduleRange(@Parameter(description = "UUID of the user.", required = true)
                                        @PathVariable(name = "uuid") UUID uuid,
                                        @Valid @RequestBody Collection<ScheduleRangeDTO> scheduleRangeDTOs,
                                        Authentication authentication,
                                        HttpServletRequest request) {
        return getController().set(scheduleRangeDTOs, uuid, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Removes an schedule range from a user. Any existing range will be adjusted or remove to not overlap."
            + "the provided range to delete.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(value = "/users/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ScheduleDTO removeScheduleRange(@Parameter(description = "UUID of the user.", required = true)
                                           @PathVariable(name = "uuid") UUID uuid,
                                           @Valid @RequestBody Collection<ScheduleRangeDTO> scheduleRangeDTOs,
                                           Authentication authentication,
                                           HttpServletRequest request) {
        return getController().remove(scheduleRangeDTOs, uuid, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Removes all the schedule from a user. "
            + "the provided range to delete.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(value = "/users/{uuid}/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ScheduleDTO removeAllScheduleRange(@Parameter(description = "UUID of the user.", required = true)
                                              @PathVariable(name = "uuid") UUID uuid,
                                              Authentication authentication,
                                              HttpServletRequest request) {
        return getController().removeAll(uuid, authentication.getName());
    }
}
