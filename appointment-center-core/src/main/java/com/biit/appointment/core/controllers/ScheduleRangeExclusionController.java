package com.biit.appointment.core.controllers;

/*-
 * #%L
 * AppointmentCenter (Core)
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

import com.biit.appointment.core.converters.ScheduleRangeExclusionConverter;
import com.biit.appointment.core.converters.models.ScheduleRangeExclusionConverterRequest;
import com.biit.appointment.core.models.ScheduleRangeExclusionDTO;
import com.biit.appointment.core.providers.ScheduleRangeExclusionProvider;
import com.biit.appointment.logger.AppointmentCenterLogger;
import com.biit.appointment.persistence.entities.ScheduleRangeExclusion;
import com.biit.appointment.persistence.repositories.ScheduleRangeExclusionRepository;
import com.biit.server.controller.ElementController;
import com.biit.server.exceptions.UserNotFoundException;
import com.biit.server.security.IUserOrganizationProvider;
import com.biit.server.security.model.IAuthenticatedUser;
import com.biit.server.security.IAuthenticatedUserProvider;
import com.biit.server.security.model.IUserOrganization;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Controller
public class ScheduleRangeExclusionController extends ElementController<ScheduleRangeExclusion, Long, ScheduleRangeExclusionDTO,
        ScheduleRangeExclusionRepository, ScheduleRangeExclusionProvider, ScheduleRangeExclusionConverterRequest, ScheduleRangeExclusionConverter> {

    private final IAuthenticatedUserProvider<? extends IAuthenticatedUser> authenticatedUserProvider;


    protected ScheduleRangeExclusionController(ScheduleRangeExclusionProvider provider, ScheduleRangeExclusionConverter converter,
                                               IAuthenticatedUserProvider<? extends IAuthenticatedUser> authenticatedUserProvider,
                                               List<IUserOrganizationProvider<? extends IUserOrganization>> userOrganizationProvider) {
        super(provider, converter, userOrganizationProvider);
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    @Override
    protected ScheduleRangeExclusionConverterRequest createConverterRequest(ScheduleRangeExclusion scheduleRangeExclusion) {
        return new ScheduleRangeExclusionConverterRequest(scheduleRangeExclusion);
    }


    public List<ScheduleRangeExclusionDTO> getFromUser(String username) {
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));
        return getFromUser(UUID.fromString(authenticatedUser.getUID()));
    }


    public List<ScheduleRangeExclusionDTO> getFromUser(UUID userUUID) {
        return convertAll(getProvider().findByUser(userUUID));
    }


    public void add(Collection<ScheduleRangeExclusionDTO> scheduleRanges, String username) {
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));
        add(scheduleRanges, UUID.fromString(authenticatedUser.getUID()), username);
    }


    public void add(Collection<ScheduleRangeExclusionDTO> scheduleRangeExclusionDTOS, UUID user, String createdBy) {
        for (ScheduleRangeExclusionDTO scheduleRangeExclusionDTO : scheduleRangeExclusionDTOS) {
            scheduleRangeExclusionDTO.setUser(user);
            add(scheduleRangeExclusionDTO, user, createdBy);
        }
    }


    public void add(ScheduleRangeExclusionDTO scheduleRangeExclusionDTO, UUID user, String createdBy) {
        AppointmentCenterLogger.info(this.getClass(), "User '{}' has added an availability exception '{}' from user '{}'.",
                createdBy, scheduleRangeExclusionDTO, user);
        convert(getProvider().save(reverse(scheduleRangeExclusionDTO)));
    }


    public void set(Collection<ScheduleRangeExclusionDTO> scheduleRangeExclusionDTOS, String username) {
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));
        set(scheduleRangeExclusionDTOS, UUID.fromString(authenticatedUser.getUID()), username);
    }


    public void set(Collection<ScheduleRangeExclusionDTO> scheduleRanges, UUID user, String createdBy) {
        AppointmentCenterLogger.info(this.getClass(), "User '{}' has set the availability exception '{}' from user '{}'.",
                createdBy, scheduleRanges, user);
        getProvider().deleteByUser(user);
        scheduleRanges.forEach(scheduleRangeExclusionDTO -> scheduleRangeExclusionDTO.setUser(user));
        getProvider().saveAll(reverseAll(scheduleRanges));
    }


    public void delete(String username) {
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));
        delete(UUID.fromString(authenticatedUser.getUID()), username);
    }


    public void delete(UUID user, String deletedBy) {
        final int deletedRecords = getProvider().deleteByUser(user);
        AppointmentCenterLogger.info(this.getClass(), "'{}' schedule range exclusions deleted from user '{}' by '{}'.",
                deletedRecords, user, deletedBy);
    }
}
