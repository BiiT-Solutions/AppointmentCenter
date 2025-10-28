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

import com.biit.appointment.core.controllers.ExternalCalendarController;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.models.CalendarProviderDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/appointments/external-providers/")
public class ExternalAppointmentServices {

    private final ExternalCalendarController externalCalendarController;

    public ExternalAppointmentServices(ExternalCalendarController externalCalendarController) {
        this.externalCalendarController = externalCalendarController;
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets an event from your external calendar.", security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping(value = {"/{provider}/external-references/{externalReference}"}, produces = {"application/json"})
    public AppointmentDTO get(@Parameter(description = "Credentials provider.", required = true)
                              @PathVariable(name = "provider") CalendarProviderDTO calendarProviderDTO,
                              @Parameter(description = "ExternalReference from an Appointment hosted on a provider", required = true)
                              @PathVariable("externalReference") String externalReference,
                              Authentication authentication, HttpServletRequest request) {
        return externalCalendarController.getExternalAppointment(authentication.getName(), externalReference, calendarProviderDTO, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a list of events within a range.", security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping(value = {"/from/{start}/to/{end}"}, produces = {"application/json"})
    public List<AppointmentDTO> get(@Parameter(description = "Lower boundary for search.  Format ISO 8601 (yyyy-MM-ddTHH:mm:ssZ)",
                                            example = "2025-01-01T00:00:00.00Z")
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                    @PathVariable(name = "start") LocalDateTime start,
                                    @Parameter(description = "Upper boundary for search. Format ISO 8601 (yyyy-MM-ddTHH:mm:ssZ)",
                                            example = "2025-31-01T23:59:59.99Z")
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                    @PathVariable(name = "end") LocalDateTime end,
                                    Authentication authentication, HttpServletRequest request) {
        return externalCalendarController.getExternalAppointments(authentication.getName(), start, end, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a list of events within a range.", security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping(value = {"/{provider}/from/{start}/to/{end}"}, produces = {"application/json"})
    public List<AppointmentDTO> get(@Parameter(description = "Credentials provider.", required = true)
                                    @PathVariable(name = "provider") CalendarProviderDTO calendarProviderDTO,
                                    @Parameter(description = "Lower boundary for search.  Format ISO 8601 (yyyy-MM-ddTHH:mm:ssZ)",
                                            example = "2025-01-01T00:00:00.00Z")
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                    @PathVariable(name = "start") LocalDateTime start,
                                    @Parameter(description = "Upper boundary for search. Format ISO 8601 (yyyy-MM-ddTHH:mm:ssZ)",
                                            example = "2025-31-01T23:59:59.99Z")
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                    @PathVariable(name = "end") LocalDateTime end,
                                    Authentication authentication, HttpServletRequest request) {
        return externalCalendarController.getExternalAppointments(authentication.getName(), start, end,
                calendarProviderDTO, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a number of events starting on a date.", security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping(value = {"/from/{start}/total/{numberOfEvents}"}, produces = {"application/json"})
    public List<AppointmentDTO> get(@Parameter(description = "Lower boundary for search.  Format ISO 8601 (yyyy-MM-ddTHH:mm:ssZ)",
                                            example = "2025-01-01T00:00:00.00Z")
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                    @PathVariable(name = "start") LocalDateTime start,
                                    @Parameter(description = "Number of events to retrieve.", required = true)
                                    @PathVariable("numberOfEvents") int numberOfEvents,
                                    Authentication authentication, HttpServletRequest request) {
        return externalCalendarController.getExternalAppointments(authentication.getName(), start, numberOfEvents, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a number of events starting on a date.", security = {@SecurityRequirement(name = "bearerAuth")})
    @GetMapping(value = {"/{provider}/from/{start}/total/{numberOfEvents}"}, produces = {"application/json"})
    public List<AppointmentDTO> get(@Parameter(description = "Credentials provider.", required = true)
                                    @PathVariable(name = "provider") CalendarProviderDTO calendarProviderDTO,
                                    @Parameter(description = "Lower boundary for search.  Format ISO 8601 (yyyy-MM-ddTHH:mm:ssZ)",
                                            example = "2025-01-01T00:00:00.00Z")
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                    @PathVariable(name = "start") LocalDateTime start,
                                    @Parameter(description = "Number of events to retrieve.", required = true)
                                    @PathVariable("numberOfEvents") int numberOfEvents,
                                    Authentication authentication, HttpServletRequest request) {
        return externalCalendarController.getExternalAppointments(authentication.getName(), start, numberOfEvents,
                calendarProviderDTO, authentication.getName());
    }
}
