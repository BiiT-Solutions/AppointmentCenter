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

import com.biit.appointment.core.controllers.kafka.ScheduleEventSender;
import com.biit.appointment.core.converters.ScheduleConverter;
import com.biit.appointment.core.converters.ScheduleRangeConverter;
import com.biit.appointment.core.converters.models.ScheduleConverterRequest;
import com.biit.appointment.core.models.ScheduleDTO;
import com.biit.appointment.core.models.ScheduleRangeDTO;
import com.biit.appointment.core.providers.ScheduleProvider;
import com.biit.appointment.logger.AppointmentCenterLogger;
import com.biit.appointment.persistence.entities.Schedule;
import com.biit.appointment.persistence.repositories.ScheduleRepository;
import com.biit.kafka.controllers.KafkaElementController;
import com.biit.server.exceptions.UserNotFoundException;
import com.biit.server.exceptions.ValidateBadRequestException;
import com.biit.server.security.IUserOrganizationProvider;
import com.biit.server.security.model.IAuthenticatedUser;
import com.biit.server.security.IAuthenticatedUserProvider;
import com.biit.server.security.model.IUserOrganization;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Controller
public class ScheduleController extends KafkaElementController<Schedule, Long, ScheduleDTO, ScheduleRepository,
        ScheduleProvider, ScheduleConverterRequest, ScheduleConverter> {

    private final ScheduleRangeConverter scheduleRangeConverter;
    private final IAuthenticatedUserProvider<? extends IAuthenticatedUser> authenticatedUserProvider;

    protected ScheduleController(ScheduleProvider provider, ScheduleConverter converter,
                                 ScheduleEventSender eventSender, ScheduleRangeConverter scheduleRangeConverter,
                                 IAuthenticatedUserProvider<? extends IAuthenticatedUser> userManagerClient,
                                 List<IUserOrganizationProvider<? extends IUserOrganization>> userOrganizationProvider) {
        super(provider, converter, eventSender, userOrganizationProvider);
        this.scheduleRangeConverter = scheduleRangeConverter;
        this.authenticatedUserProvider = userManagerClient;
    }


    @Override
    protected ScheduleConverterRequest createConverterRequest(Schedule schedule) {
        return new ScheduleConverterRequest(schedule);
    }

    public ScheduleDTO get(String username) {
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));
        return get(UUID.fromString(authenticatedUser.getUID()));
    }

    public ScheduleDTO getDefault() {
        return convert(getProvider().getDefaultSchedule(null));
    }


    public ScheduleDTO get(UUID user) {
        return convert(getProvider().findByUser(user).orElse(new Schedule(user)));
    }


    public ScheduleDTO set(Collection<ScheduleRangeDTO> scheduleRanges, String username) {
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));
        return set(scheduleRanges, UUID.fromString(authenticatedUser.getUID()), username);
    }


    public ScheduleDTO update(ScheduleRangeDTO scheduleRange, String username) {
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));
        return update(scheduleRange, UUID.fromString(authenticatedUser.getUID()), username);
    }


    public ScheduleDTO set(Collection<ScheduleRangeDTO> scheduleRanges, UUID user, String createdBy) {
        AppointmentCenterLogger.info(this.getClass(), "User '{}' has added schedules '{}' from user '{}'.",
                createdBy, scheduleRanges, user);
        return convert(getProvider().set(scheduleRangeConverter.reverseAll(scheduleRanges), user));
    }


    public ScheduleDTO update(ScheduleRangeDTO scheduleRange, UUID user, String createdBy) {
        AppointmentCenterLogger.info(this.getClass(), "User '{}' is updating schedule '{}' from user '{}'.",
                createdBy, scheduleRange, user);
        return convert(getProvider().update(scheduleRangeConverter.reverse(scheduleRange), user));
    }


    public ScheduleDTO add(Collection<ScheduleRangeDTO> scheduleRanges, String username) {
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));
        return add(scheduleRanges, UUID.fromString(authenticatedUser.getUID()), username);
    }


    public ScheduleDTO add(Collection<ScheduleRangeDTO> scheduleRanges, UUID user, String createdBy) {
        ScheduleDTO scheduleDTO = null;
        for (ScheduleRangeDTO scheduleRange : scheduleRanges) {
            scheduleDTO = add(scheduleRange, user, createdBy);
        }
        return scheduleDTO;
    }


    public ScheduleDTO add(ScheduleRangeDTO scheduleRange, UUID user, String createdBy) {
        AppointmentCenterLogger.info(this.getClass(), "User '{}' has added schedule '{}' from user '{}'.",
                createdBy, scheduleRange, user);
        return convert(getProvider().add(scheduleRangeConverter.reverse(scheduleRange), user));
    }


    public void removeScheduleRange(Collection<Long> ids, UUID userUUID, String deletedBy) {
        AppointmentCenterLogger.info(this.getClass(), "User '{}' wants to remove schedules '{}' from user '{}'.",
                deletedBy, ids, userUUID);
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUID(userUUID.toString())
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with UUID '" + userUUID + "' found!"));

        for (Long id : ids) {
            getProvider().removeRange(id, UUID.fromString(authenticatedUser.getUID()));
        }
    }


    public void removeScheduleRange(Collection<Long> ids, String username) {
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));

        for (Long id : ids) {
            getProvider().removeRange(id, UUID.fromString(authenticatedUser.getUID()));
        }
    }


    public ScheduleDTO remove(Collection<ScheduleRangeDTO> scheduleRanges, String username) {
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));
        return remove(scheduleRanges, UUID.fromString(authenticatedUser.getUID()), username);
    }


    public ScheduleDTO remove(Collection<ScheduleRangeDTO> scheduleRanges, UUID user, String createdBy) {
        ScheduleDTO scheduleDTO = null;
        for (ScheduleRangeDTO scheduleRange : scheduleRanges) {
            scheduleDTO = remove(scheduleRange, user, createdBy);
        }
        return scheduleDTO;
    }


    public ScheduleDTO remove(ScheduleRangeDTO scheduleRange, UUID user, String createdBy) {
        AppointmentCenterLogger.info(this.getClass(), "User '{}' has removed schedule '{}' from user '{}'.",
                createdBy, scheduleRange, user);
        return convert(getProvider().remove(scheduleRangeConverter.reverse(scheduleRange), user));
    }


    public ScheduleDTO removeAll(String username) {
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));
        return removeAll(UUID.fromString(authenticatedUser.getUID()), username);
    }


    public ScheduleDTO removeAll(UUID user, String createdBy) {
        AppointmentCenterLogger.info(this.getClass(), "User '{}' has removed all schedule from user '{}'.",
                createdBy, user);
        return convert(getProvider().removeAll(user));
    }

    @Override
    public void validate(ScheduleDTO dto) throws ValidateBadRequestException {
        if (dto.getUser() == null) {
            throw new ValidateBadRequestException(this.getClass(), "User cannot be null");
        }
    }

}
