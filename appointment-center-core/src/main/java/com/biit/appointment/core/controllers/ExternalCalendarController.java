package com.biit.appointment.core.controllers;

import com.biit.appointment.core.converters.CalendarProviderConverter;
import com.biit.appointment.core.converters.ExternalCalendarCredentialsConverter;
import com.biit.appointment.core.converters.models.ExternalCalendarCredentialsConverterRequest;
import com.biit.appointment.core.exceptions.AppointmentNotFoundException;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.models.CalendarProviderDTO;
import com.biit.appointment.core.models.ExternalCalendarCredentialsDTO;
import com.biit.appointment.core.providers.ExternalCalendarCredentialsProvider;
import com.biit.appointment.core.services.IExternalProviderCalendarService;
import com.biit.appointment.logger.AppointmentCenterLogger;
import com.biit.appointment.persistence.entities.ExternalCalendarCredentials;
import com.biit.server.exceptions.UserNotFoundException;
import com.biit.server.security.IAuthenticatedUser;
import com.biit.server.security.IAuthenticatedUserProvider;
import com.biit.server.security.exceptions.ActionNotAllowedException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class ExternalCalendarController {
    private static final int REFRESHING_TOKEN_INTERVAL = 24 * 60 * 60;

    private final List<IExternalProviderCalendarService> externalCalendarControllers;
    private final ExternalCalendarCredentialsProvider externalCalendarCredentialsProvider;
    private final IAuthenticatedUserProvider authenticatedUserProvider;
    private final ExternalCalendarCredentialsConverter externalCalendarCredentialsConverter;
    private final CalendarProviderConverter calendarProviderConverter;


    public ExternalCalendarController(List<IExternalProviderCalendarService> externalCalendarControllers,
                                      ExternalCalendarCredentialsProvider externalCalendarCredentialsProvider,
                                      IAuthenticatedUserProvider authenticatedUserProvider,
                                      ExternalCalendarCredentialsConverter externalCalendarCredentialsConverter,
                                      CalendarProviderConverter calendarProviderConverter) {
        this.externalCalendarControllers = externalCalendarControllers;
        this.externalCalendarCredentialsProvider = externalCalendarCredentialsProvider;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.externalCalendarCredentialsConverter = externalCalendarCredentialsConverter;
        this.calendarProviderConverter = calendarProviderConverter;
    }


    public AppointmentDTO getExternalAppointment(String username, String externalReference, CalendarProviderDTO provider, String requestedBy) {
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));

        for (IExternalProviderCalendarService externalCalendarController : externalCalendarControllers) {
            if (Objects.equals(externalCalendarController.from(), provider)) {
                final ExternalCalendarCredentials externalCalendarCredentials = externalCalendarCredentialsProvider
                        .getByUserIdAndCalendarProvider(UUID.fromString(authenticatedUser.getUID()),
                                calendarProviderConverter.reverse(externalCalendarController.from()));
                if (externalCalendarCredentials != null) {
                    final AppointmentDTO appointmentDTO = externalCalendarController.getEvent(externalReference, externalCalendarCredentialsConverter
                            .convert(new ExternalCalendarCredentialsConverterRequest(externalCalendarCredentials)));
                    if (appointmentDTO == null) {
                        throw new AppointmentNotFoundException(this.getClass(), "No appointment found for '" + externalReference + "'.");
                    }
                    return appointmentDTO;
                }
            }
        }
        throw new ActionNotAllowedException(this.getClass(), "You are not allowed to access to provider '" + provider + "'.");
    }

    public List<AppointmentDTO> getExternalAppointments(final String username,
                                                        final LocalDateTime rangeStartingTime,
                                                        final LocalDateTime rangeEndingTime,
                                                        final String requestedBy) {
        final List<AppointmentDTO> appointmentsDTOs = new ArrayList<>();

        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(), "No user with username '" + username + "' found!"));
        externalCalendarCredentialsProvider.getByUserId(UUID.fromString(authenticatedUser.getUID())).parallelStream()
                .forEach(externalCalendarCredentials -> {
                    try {

                        final Collection<AppointmentDTO> appointments =
                                getExternalAppointments(authenticatedUser, rangeStartingTime, rangeEndingTime,
                                        calendarProviderConverter.convertElement(externalCalendarCredentials.getCalendarProvider()), requestedBy);
                        appointmentsDTOs.addAll(appointments);
                    } catch (Exception e) {
                        AppointmentCenterLogger.debug(this.getClass(), "Error while fetching appointments from provider '{}': '{}' for user '{}'",
                                externalCalendarCredentials, e.getMessage(), authenticatedUser.getUID());
                        AppointmentCenterLogger.errorMessage(this.getClass(), e);
                    }
                });
        return appointmentsDTOs;
    }


    public List<AppointmentDTO> getExternalAppointments(String username, LocalDate rangeStartingTime, LocalDate rangeEndingTime, CalendarProviderDTO provider,
                                                        String requestedBy) {
        return getExternalAppointments(username, rangeStartingTime.atStartOfDay(), rangeEndingTime.atTime(LocalTime.MAX), provider, requestedBy);
    }


    public List<AppointmentDTO> getExternalAppointments(String username, LocalDateTime rangeStartingTime, LocalDateTime rangeEndingTime,
                                                        CalendarProviderDTO provider, String requestedBy) {
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));

        return getExternalAppointments(authenticatedUser, rangeStartingTime, rangeEndingTime, provider, requestedBy);
    }


    public List<AppointmentDTO> getExternalAppointments(IAuthenticatedUser authenticatedUser, LocalDateTime rangeStartingTime, LocalDateTime rangeEndingTime,
                                                        CalendarProviderDTO provider, String requestedBy) {
        for (IExternalProviderCalendarService externalCalendarController : externalCalendarControllers) {
            if (Objects.equals(externalCalendarController.from(), provider)) {
                final ExternalCalendarCredentialsDTO externalCalendarCredentials =
                        externalCalendarCredentialsConverter.convert(
                                new ExternalCalendarCredentialsConverterRequest(externalCalendarCredentialsProvider
                                        .getByUserIdAndCalendarProvider(UUID.fromString(authenticatedUser.getUID()),
                                                calendarProviderConverter.reverse(externalCalendarController.from()))));
                if (externalCalendarCredentials != null) {
                    return externalCalendarController.getEvents(rangeStartingTime, rangeEndingTime, externalCalendarCredentials);
                }
            }
        }
        throw new ActionNotAllowedException(this.getClass(), "You are not allowed to access to provider '" + provider + "'.");
    }


    public List<AppointmentDTO> getExternalAppointments(String username, LocalDateTime rangeStartingTime, int numberOfEvents, String requestedBy) {
        final List<AppointmentDTO> appointmentsDTOs = new ArrayList<>();
        Arrays.stream(CalendarProviderDTO.values()).parallel().forEach(calendarProvider ->
                appointmentsDTOs.addAll(getExternalAppointments(username, rangeStartingTime, numberOfEvents, calendarProvider, requestedBy)));
        return appointmentsDTOs;
    }


    public List<AppointmentDTO> getExternalAppointments(String username, LocalDate rangeStartingTime, int numberOfEvents, CalendarProviderDTO provider,
                                                        String requestedBy) {
        return getExternalAppointments(username, rangeStartingTime.atStartOfDay(), numberOfEvents, provider, requestedBy);
    }


    public List<AppointmentDTO> getExternalAppointments(String username, LocalDateTime rangeStartingTime, int numberOfEvents, CalendarProviderDTO provider,
                                                        String requestedBy) {
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));

        return getExternalAppointments(authenticatedUser, rangeStartingTime, numberOfEvents, provider, requestedBy);
    }


    public List<AppointmentDTO> getExternalAppointments(IAuthenticatedUser authenticatedUser, LocalDateTime rangeStartingTime, int numberOfEvents,
                                                        CalendarProviderDTO provider, String requestedBy) {
        for (IExternalProviderCalendarService externalCalendarController : externalCalendarControllers) {
            if (Objects.equals(externalCalendarController.from(), provider)) {
                final ExternalCalendarCredentialsDTO externalCalendarCredentials = externalCalendarCredentialsConverter.convert(
                        new ExternalCalendarCredentialsConverterRequest(externalCalendarCredentialsProvider
                                .getByUserIdAndCalendarProvider(UUID.fromString(authenticatedUser.getUID()),
                                        calendarProviderConverter.reverse(externalCalendarController.from()))));
                if (externalCalendarCredentials != null) {
                    return externalCalendarController.getEvents(numberOfEvents, rangeStartingTime,
                            externalCalendarCredentials);
                }
            }
        }
        throw new ActionNotAllowedException(this.getClass(), "You are not allowed to access to provider '" + provider + "'.");
    }


    private void updateExternalCalendarControllerThatExpires(LocalDateTime expiresBefore) {
        final List<ExternalCalendarCredentialsDTO> credentialsToExpire = externalCalendarCredentialsConverter.convertAll(
                externalCalendarCredentialsProvider.findByForceRefreshAtBefore(expiresBefore).stream().map(this::createConverterRequest)
                        .collect(Collectors.toCollection(ArrayList::new)));
        if (!credentialsToExpire.isEmpty()) {
            AppointmentCenterLogger.info(this.getClass(), "Updating '{}' tokens.", credentialsToExpire.size());
            credentialsToExpire.parallelStream().forEach(externalCalendarCredentialsDTO ->
                    externalCalendarCredentialsProvider.refreshExternalCredentials(externalCalendarCredentialsConverter
                            .reverse(externalCalendarCredentialsDTO)));
        }
    }

    private ExternalCalendarCredentialsConverterRequest createConverterRequest(ExternalCalendarCredentials externalCalendarCredentials) {
        return new ExternalCalendarCredentialsConverterRequest(externalCalendarCredentials);
    }

    @Scheduled(fixedRate = REFRESHING_TOKEN_INTERVAL, timeUnit = TimeUnit.SECONDS, initialDelay = 0)
    public void scheduleRefreshTokens() {
        AppointmentCenterLogger.info(this.getClass(), "Refreshing external calendar tokens...");
        updateExternalCalendarControllerThatExpires(LocalDateTime.now());
    }
}
