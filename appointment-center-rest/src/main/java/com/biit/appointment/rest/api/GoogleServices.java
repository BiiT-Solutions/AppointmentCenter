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
import com.biit.appointment.core.models.CalendarProviderDTO;
import com.biit.appointment.core.models.ExternalCalendarCredentialsDTO;
import com.biit.appointment.google.client.GoogleCalendarService;
import com.biit.appointment.google.logger.GoogleCalDAVLogger;
import com.biit.appointment.rest.api.exceptions.InvalidGoogleCredentialsException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/external-calendar-credentials/google")
@ConditionalOnClass(name = "com.biit.appointment.google.client.GoogleCalendarService")
public class GoogleServices {

    private final GoogleCalendarService googleCalendarService;
    private final ExternalCalendarCredentialsController externalCalendarCredentialsController;

    public GoogleServices(GoogleCalendarService googleCalendarService,
                          ExternalCalendarCredentialsController externalCalendarCredentialsController) {
        this.googleCalendarService = googleCalendarService;
        this.externalCalendarCredentialsController = externalCalendarCredentialsController;
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Request the credentials from a user from google.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/oauth/code/{code}/state/{state}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ExternalCalendarCredentialsDTO getGoogleAuth(@Parameter(description = "Google Auth code.", required = true)
                                                        @PathVariable(name = "code") String code,
                                                        @Parameter(description = "State that matches the code of the requester.", required = true)
                                                        @PathVariable(name = "state") String state,
                                                        Authentication authentication,
                                                        HttpServletRequest request) {
        final ExternalCalendarCredentialsDTO externalCalendarCredentialsDTO = googleCalendarService
                .exchangeCodeForToken(authentication.getName(), code, state);
        //Delete any previous credential.
        externalCalendarCredentialsController.deleteToken(externalCalendarCredentialsDTO.getUserId(), CalendarProviderDTO.GOOGLE);
        return externalCalendarCredentialsController.create(externalCalendarCredentialsDTO, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Request the credentials from a user from google.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/oauth", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ExternalCalendarCredentialsDTO getGoogleAuthByRequestParams(@RequestParam(name = "code", required = false) Optional<String> code,
                                                                       @RequestParam(name = "state", required = false) Optional<String> state,
                                                                       @RequestParam(name = "error", required = false) Optional<String> error,
                                                                       Authentication authentication,
                                                                       HttpServletRequest request) {
        if (error.isPresent()) {
            throw new InvalidGoogleCredentialsException(this.getClass(), "Invalid google credentials. Error issued: " + error.get());
        }
        GoogleCalDAVLogger.debug(this.getClass(), "Received code '{}' and state '{}'.",
                code.orElse(null),
                state.orElse(null));
        //Delete any previous credential.
        externalCalendarCredentialsController.deleteToken(authentication.getName(), CalendarProviderDTO.GOOGLE);
        final ExternalCalendarCredentialsDTO externalCalendarCredentialsDTO = googleCalendarService
                .exchangeCodeForToken(authentication.getName(), code.orElse(null), state.orElse(null));
        return externalCalendarCredentialsController.create(externalCalendarCredentialsDTO, authentication.getName());
    }

}
