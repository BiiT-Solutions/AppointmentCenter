package com.biit.appointment.core.controllers;

import com.biit.appointment.core.converters.CalendarProviderConverter;
import com.biit.appointment.core.converters.ExternalCalendarCredentialsConverter;
import com.biit.appointment.core.converters.models.ExternalCalendarCredentialsConverterRequest;
import com.biit.appointment.core.models.CalendarProviderDTO;
import com.biit.appointment.core.models.ExternalCalendarCredentialsDTO;
import com.biit.appointment.core.providers.ExternalCalendarCredentialsProvider;
import com.biit.appointment.core.providers.ExternalCalendarCredentialsProxy;
import com.biit.appointment.logger.AppointmentCenterLogger;
import com.biit.appointment.persistence.entities.ExternalCalendarCredentials;
import com.biit.appointment.persistence.repositories.ExternalCalendarCredentialsRepository;
import com.biit.server.controller.ElementController;
import com.biit.server.exceptions.UserNotFoundException;
import com.biit.server.security.IAuthenticatedUser;
import com.biit.server.security.IAuthenticatedUserProvider;
import com.biit.server.security.exceptions.ActionNotAllowedException;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
public class ExternalCalendarCredentialsController extends ElementController<ExternalCalendarCredentials, Long, ExternalCalendarCredentialsDTO,
        ExternalCalendarCredentialsRepository, ExternalCalendarCredentialsProvider, ExternalCalendarCredentialsConverterRequest,
        ExternalCalendarCredentialsConverter> {

    private final CalendarProviderConverter calendarProviderConverter;
    private final IAuthenticatedUserProvider authenticatedUserProvider;
    private final ExternalCalendarCredentialsProxy externalProviderCalendarCredentials;

    protected ExternalCalendarCredentialsController(ExternalCalendarCredentialsProvider provider, ExternalCalendarCredentialsConverter converter,
                                                    CalendarProviderConverter calendarProviderConverter, IAuthenticatedUserProvider authenticatedUserProvider,
                                                    ExternalCalendarCredentialsProxy externalProviderCalendarCredentials) {
        super(provider, converter);
        this.calendarProviderConverter = calendarProviderConverter;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.externalProviderCalendarCredentials = externalProviderCalendarCredentials;
    }


    @Override
    protected ExternalCalendarCredentialsConverterRequest createConverterRequest(ExternalCalendarCredentials schedule) {
        return new ExternalCalendarCredentialsConverterRequest(schedule);
    }

    public ExternalCalendarCredentialsDTO getByUserNameAndCalendarProvider(String username, CalendarProviderDTO calendarProvider) {
        final IAuthenticatedUser user = authenticatedUserProvider.findByUsername(username).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "No user found with username '" + username + "'."));
        return convert(getProvider().getByUserIdAndCalendarProvider(UUID.fromString(user.getUID()),
                calendarProviderConverter.reverse(calendarProvider)));
    }


    public ExternalCalendarCredentialsDTO getByUserIdAndCalendarProvider(UUID userId, CalendarProviderDTO calendarProvider, String requestedBy) {
        AppointmentCenterLogger.debug(this.getClass(), "User '{}' is requesting the credentials from user '{}' on '{}'.",
                requestedBy, userId, calendarProvider);
        return getProvider().refreshIfExpired(convert(getProvider()
                .getByUserIdAndCalendarProvider(userId, calendarProviderConverter.reverse(calendarProvider))));
    }

    public List<ExternalCalendarCredentialsDTO> findByCreatedAtBefore(LocalDateTime expiresAt) {
        return convertAll(getProvider().findByCreatedAtBefore(expiresAt));
    }

    public void deleteToken(String username, CalendarProviderDTO calendarProvider) {
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));
        deleteToken(UUID.fromString(authenticatedUser.getUID()), calendarProvider);
    }

    public void deleteToken(UUID userUUID, CalendarProviderDTO calendarProvider) {
        externalProviderCalendarCredentials.delete(userUUID, calendarProvider);
    }


    public ExternalCalendarCredentialsDTO createOwn(ExternalCalendarCredentialsDTO dto, String username) {
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));

        if (dto.getUserId() == null || !Objects.equals(dto.getUserId().toString(), authenticatedUser.getUID())) {
            throw new ActionNotAllowedException(this.getClass(), "The credentials are not assigned to logged in user.");
        }

        return super.create(dto, username);
    }
}
