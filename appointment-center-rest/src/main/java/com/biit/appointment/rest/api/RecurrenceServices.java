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

import com.biit.appointment.core.controllers.RecurrenceController;
import com.biit.appointment.core.converters.RecurrenceConverter;
import com.biit.appointment.core.converters.models.RecurrenceConverterRequest;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.models.RecurrenceDTO;
import com.biit.appointment.core.providers.RecurrenceProvider;
import com.biit.appointment.persistence.entities.Recurrence;
import com.biit.appointment.persistence.repositories.RecurrenceRepository;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/recurrences")
public class RecurrenceServices extends ElementServices<Recurrence, Long, RecurrenceDTO, RecurrenceRepository,
        RecurrenceProvider, RecurrenceConverterRequest, RecurrenceConverter, RecurrenceController> {

    public RecurrenceServices(RecurrenceController controller) {
        super(controller);
    }

    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Force recurrence to skip a date.", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(value = "/{id}/skip/{skipDate}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RecurrenceDTO addSkipDate(@Parameter(description = "Id of the recurrence series.")
                                     @PathVariable(name = "id") Long id,
                                     @Parameter(description = "Date to skip on the series.")
                                     @PathVariable(name = "skipDate") LocalDate skipDate,
                                     Authentication authentication,
                                     HttpServletRequest request) {
        return getController().addSkipIteration(id, skipDate, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Remove a recurrence' skip date.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(value = "/{id}/skip/{skipDate}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RecurrenceDTO removeSkipDate(@Parameter(description = "Id of the recurrence series.")
                                        @PathVariable(name = "id") Long id,
                                        @Parameter(description = "Date to skip on the series that will be removed.")
                                        @PathVariable(name = "skipDate") LocalDate skipDate,
                                        Authentication authentication,
                                        HttpServletRequest request) {
        return getController().removeSkipIteration(id, skipDate, authentication.getName());
    }

    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Force recurrence to have an appointment exception.", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(value = "/{id}/appointments", produces = MediaType.APPLICATION_JSON_VALUE)
    public RecurrenceDTO addAppointmentException(@Parameter(description = "Id of the recurrence series.")
                                                 @PathVariable(name = "id") Long id,
                                                 @Valid @RequestBody AppointmentDTO appointmentDTO,
                                                 Authentication authentication,
                                                 HttpServletRequest request) {
        return getController().addAppointmentException(id, appointmentDTO, authentication.getName());
    }

    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Removes an appointment exception from a recurrence.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/{id}/appointments", produces = MediaType.APPLICATION_JSON_VALUE)
    public RecurrenceDTO removeAppointmentException(@Parameter(description = "Id of the recurrence series.")
                                                    @PathVariable(name = "id") Long id,
                                                    @Valid @RequestBody AppointmentDTO appointmentDTO,
                                                    Authentication authentication,
                                                    HttpServletRequest request) {
        return getController().removeAppointmentException(id, appointmentDTO, authentication.getName());
    }

    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Removes an appointment exception from a recurrence.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(value = "/{id}/appointments/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public RecurrenceDTO removeAppointmentException(@Parameter(description = "Id of the recurrence series.")
                                                    @PathVariable(name = "id") Long id,
                                                    @Parameter(description = "Appointment ID to be removed from the exceptions.")
                                                    @PathVariable(name = "appointmentId") Long appointmentId,
                                                    Authentication authentication,
                                                    HttpServletRequest request) {
        return getController().removeAppointmentException(id, appointmentId, authentication.getName());
    }
}
