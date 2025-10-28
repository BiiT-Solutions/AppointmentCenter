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

import com.biit.appointment.core.controllers.ExternalCalendarCredentialsController;
import com.biit.appointment.core.converters.ExternalCalendarCredentialsConverter;
import com.biit.appointment.core.converters.models.ExternalCalendarCredentialsConverterRequest;
import com.biit.appointment.core.models.CalendarProviderDTO;
import com.biit.appointment.core.models.ExternalCalendarCredentialsDTO;
import com.biit.appointment.core.providers.ExternalCalendarCredentialsProvider;
import com.biit.appointment.persistence.entities.ExternalCalendarCredentials;
import com.biit.appointment.persistence.repositories.ExternalCalendarCredentialsRepository;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/external-calendar-credentials")
public class ExternalCalendarCredentialsServices extends ElementServices<ExternalCalendarCredentials, Long, ExternalCalendarCredentialsDTO,
        ExternalCalendarCredentialsRepository, ExternalCalendarCredentialsProvider, ExternalCalendarCredentialsConverterRequest,
        ExternalCalendarCredentialsConverter, ExternalCalendarCredentialsController> {

    public ExternalCalendarCredentialsServices(ExternalCalendarCredentialsController controller) {
        super(controller);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Allows a user to stores its own credentials.", security = {@SecurityRequirement(name = "bearerAuth")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = {"/me"}, produces = {"application/json"})
    public ExternalCalendarCredentialsDTO addOwn(@Valid @RequestBody ExternalCalendarCredentialsDTO dto,
                                                 Authentication authentication, HttpServletRequest request) {
        return this.getController().createOwn(dto, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Checks if the current user has credentials for the selected provider.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/providers/{provider}/check", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean checkOwnExternalCalendarCredentials(@Parameter(description = "Credentials provider.", required = true)
                                                       @PathVariable(name = "provider") CalendarProviderDTO calendarProviderDTO,
                                                       Authentication authentication,
                                                       HttpServletRequest request) {
        return getController().getByUserNameAndCalendarProvider(authentication.getName(), calendarProviderDTO) != null;
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets the credentials from the current user on a specific provider.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/providers/{provider}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ExternalCalendarCredentialsDTO getOwnExternalCalendarCredentials(@Parameter(description = "Credentials provider.", required = true)
                                                                            @PathVariable(name = "provider") CalendarProviderDTO calendarProviderDTO,
                                                                            Authentication authentication,
                                                                            HttpServletRequest request) {
        return getController().getByUserNameAndCalendarProvider(authentication.getName(), calendarProviderDTO);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets the credentials from a user on a specific provider.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/users/{uuid}/providers/{provider}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ExternalCalendarCredentialsDTO getExternalCalendarCredentials(@Parameter(description = "UUID of the user.", required = true)
                                                                         @PathVariable(name = "uuid") UUID uuid,
                                                                         @Parameter(description = "Credentials provider.", required = true)
                                                                         @PathVariable(name = "provider") CalendarProviderDTO calendarProviderDTO,
                                                                         Authentication authentication,
                                                                         HttpServletRequest request) {
        return getController().getByUserIdAndCalendarProvider(uuid, calendarProviderDTO, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Deletes the credencial from the current logged in user.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(value = "/providers/{provider}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteExternalProviderAuth(@Parameter(description = "Credentials provider.", required = true)
                                           @PathVariable(name = "provider") CalendarProviderDTO calendarProviderDTO,
                                           Authentication authentication, HttpServletRequest request) {
        getController().deleteToken(authentication.getName(), calendarProviderDTO);
    }
}
