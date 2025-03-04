package com.biit.appointment.core.controllers;

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
import com.biit.server.security.IAuthenticatedUser;
import com.biit.server.security.IAuthenticatedUserProvider;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.UUID;

@Controller
public class ScheduleController extends KafkaElementController<Schedule, Long, ScheduleDTO, ScheduleRepository,
        ScheduleProvider, ScheduleConverterRequest, ScheduleConverter> {

    private final ScheduleRangeConverter scheduleRangeConverter;
    private final IAuthenticatedUserProvider authenticatedUserProvider;

    protected ScheduleController(ScheduleProvider provider, ScheduleConverter converter,
                                 ScheduleEventSender eventSender, ScheduleRangeConverter scheduleRangeConverter,
                                 IAuthenticatedUserProvider userManagerClient) {
        super(provider, converter, eventSender);
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


    public ScheduleDTO set(Collection<ScheduleRangeDTO> scheduleRanges, UUID user, String createdBy) {
        AppointmentCenterLogger.info(this.getClass(), "User '{}' has added schedules '{}' from user '{}'.",
                createdBy, scheduleRanges, user);
        return convert(getProvider().set(scheduleRangeConverter.reverseAll(scheduleRanges), user));
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
