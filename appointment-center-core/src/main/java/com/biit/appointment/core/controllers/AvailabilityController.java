package com.biit.appointment.core.controllers;

import com.biit.appointment.core.controllers.kafka.AvailabilityEventSender;
import com.biit.appointment.core.converters.AvailabilityConverter;
import com.biit.appointment.core.converters.AvailabilityRangeConverter;
import com.biit.appointment.core.converters.models.AvailabilityConverterRequest;
import com.biit.appointment.core.models.AvailabilityDTO;
import com.biit.appointment.core.models.AvailabilityRangeDTO;
import com.biit.appointment.core.providers.AvailabilityProvider;
import com.biit.appointment.logger.AppointmentCenterLogger;
import com.biit.appointment.persistence.entities.Availability;
import com.biit.appointment.persistence.repositories.AvailabilityRepository;
import com.biit.kafka.controllers.KafkaElementController;
import com.biit.server.exceptions.UserNotFoundException;
import com.biit.server.exceptions.ValidateBadRequestException;
import com.biit.server.security.IAuthenticatedUser;
import com.biit.usermanager.client.providers.UserManagerClient;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.UUID;

@Controller
public class AvailabilityController extends KafkaElementController<Availability, Long, AvailabilityDTO, AvailabilityRepository,
        AvailabilityProvider, AvailabilityConverterRequest, AvailabilityConverter> {

    private final AvailabilityRangeConverter availabilityRangeConverter;
    private final UserManagerClient userManagerClient;

    protected AvailabilityController(AvailabilityProvider provider, AvailabilityConverter converter,
                                     AvailabilityEventSender eventSender, AvailabilityRangeConverter availabilityRangeConverter,
                                     UserManagerClient userManagerClient) {
        super(provider, converter, eventSender);
        this.availabilityRangeConverter = availabilityRangeConverter;
        this.userManagerClient = userManagerClient;
    }


    @Override
    protected AvailabilityConverterRequest createConverterRequest(Availability availability) {
        return new AvailabilityConverterRequest(availability);
    }

    public AvailabilityDTO get(String username) {
        final IAuthenticatedUser authenticatedUser = userManagerClient.findByUsername(username).orElseThrow(() -> new UserNotFoundException(this.getClass(),
                "No user with username '" + username + "' found!"));
        return get(UUID.fromString(authenticatedUser.getUID()));
    }


    public AvailabilityDTO get(UUID user) {
        return convert(getProvider().findByUser(user).orElse(new Availability(user)));
    }


    public AvailabilityDTO set(Collection<AvailabilityRangeDTO> availabilityRanges, String username) {
        final IAuthenticatedUser authenticatedUser = userManagerClient.findByUsername(username).orElseThrow(() -> new UserNotFoundException(this.getClass(),
                "No user with username '" + username + "' found!"));
        return set(availabilityRanges, UUID.fromString(authenticatedUser.getUID()), username);
    }


    public AvailabilityDTO set(Collection<AvailabilityRangeDTO> availabilityRanges, UUID user, String createdBy) {
        AppointmentCenterLogger.info(this.getClass(), "User '{}' has added availabilities '{}' from user '{}'.",
                createdBy, availabilityRanges, user);
        return convert(getProvider().set(availabilityRangeConverter.reverseAll(availabilityRanges), user));
    }

    public AvailabilityDTO add(Collection<AvailabilityRangeDTO> availabilityRanges, String username) {
        final IAuthenticatedUser authenticatedUser = userManagerClient.findByUsername(username).orElseThrow(() -> new UserNotFoundException(this.getClass(),
                "No user with username '" + username + "' found!"));
        return add(availabilityRanges, UUID.fromString(authenticatedUser.getUID()), username);
    }


    public AvailabilityDTO add(Collection<AvailabilityRangeDTO> availabilityRanges, UUID user, String createdBy) {
        AvailabilityDTO availabilityDTO = null;
        for (AvailabilityRangeDTO availabilityRange : availabilityRanges) {
            availabilityDTO = add(availabilityRange, user, createdBy);
        }
        return availabilityDTO;
    }


    public AvailabilityDTO add(AvailabilityRangeDTO availabilityRange, UUID user, String createdBy) {
        AppointmentCenterLogger.info(this.getClass(), "User '{}' has added availability '{}' from user '{}'.",
                createdBy, availabilityRange, user);
        return convert(getProvider().add(availabilityRangeConverter.reverse(availabilityRange), user));
    }


    public AvailabilityDTO remove(Collection<AvailabilityRangeDTO> availabilityRanges, String username) {
        final IAuthenticatedUser authenticatedUser = userManagerClient.findByUsername(username).orElseThrow(() -> new UserNotFoundException(this.getClass(),
                "No user with username '" + username + "' found!"));
        return remove(availabilityRanges, UUID.fromString(authenticatedUser.getUID()), username);
    }


    public AvailabilityDTO remove(Collection<AvailabilityRangeDTO> availabilityRanges, UUID user, String createdBy) {
        AvailabilityDTO availabilityDTO = null;
        for (AvailabilityRangeDTO availabilityRange : availabilityRanges) {
            availabilityDTO = remove(availabilityRange, user, createdBy);
        }
        return availabilityDTO;
    }


    public AvailabilityDTO remove(AvailabilityRangeDTO availabilityRange, UUID user, String createdBy) {
        AppointmentCenterLogger.info(this.getClass(), "User '{}' has removed availability '{}' from user '{}'.",
                createdBy, availabilityRange, user);
        return convert(getProvider().remove(availabilityRangeConverter.reverse(availabilityRange), user));
    }


    public AvailabilityDTO removeAll(String username) {
        final IAuthenticatedUser authenticatedUser = userManagerClient.findByUsername(username).orElseThrow(() -> new UserNotFoundException(this.getClass(),
                "No user with username '" + username + "' found!"));
        return removeAll(UUID.fromString(authenticatedUser.getUID()), username);
    }


    public AvailabilityDTO removeAll(UUID user, String createdBy) {
        AppointmentCenterLogger.info(this.getClass(), "User '{}' has removed all availability from user '{}'.",
                createdBy, user);
        return convert(getProvider().removeAll(user));
    }

    @Override
    public void validate(AvailabilityDTO dto) throws ValidateBadRequestException {
        if (dto.getUser() == null) {
            throw new ValidateBadRequestException(this.getClass(), "User cannot be null");
        }
    }

}
