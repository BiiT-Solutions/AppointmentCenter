package com.biit.appointment.core.controllers;

import com.biit.appointment.core.converters.CalendarProviderConverter;
import com.biit.appointment.core.converters.ExternalCalendarCredentialsConverter;
import com.biit.appointment.core.converters.models.ExternalCalendarCredentialsConverterRequest;
import com.biit.appointment.core.exceptions.AppointmentNotFoundException;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.models.CalendarProviderDTO;
import com.biit.appointment.core.providers.ExternalCalendarCredentialsProvider;
import com.biit.appointment.core.providers.IExternalCalendarProvider;
import com.biit.appointment.persistence.entities.ExternalCalendarCredentials;
import com.biit.server.exceptions.UserNotFoundException;
import com.biit.server.security.IAuthenticatedUser;
import com.biit.server.security.IAuthenticatedUserProvider;
import com.biit.server.security.exceptions.ActionNotAllowedException;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
public class ExternalCalendarController {

    private final List<IExternalCalendarProvider> externalCalendarProviders;
    private final ExternalCalendarCredentialsConverter externalCalendarCredentialsConverter;
    private final CalendarProviderConverter calendarProviderConverter;
    private final ExternalCalendarCredentialsProvider externalCalendarCredentialsProvider;
    private final IAuthenticatedUserProvider authenticatedUserProvider;


    public ExternalCalendarController(List<IExternalCalendarProvider> externalCalendarProviders,
                                      ExternalCalendarCredentialsConverter externalCalendarCredentialsConverter,
                                      CalendarProviderConverter calendarProviderConverter,
                                      ExternalCalendarCredentialsProvider externalCalendarCredentialsProvider,
                                      IAuthenticatedUserProvider authenticatedUserProvider) {
        this.externalCalendarProviders = externalCalendarProviders;
        this.externalCalendarCredentialsConverter = externalCalendarCredentialsConverter;
        this.calendarProviderConverter = calendarProviderConverter;
        this.externalCalendarCredentialsProvider = externalCalendarCredentialsProvider;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }


    public AppointmentDTO getExternalAppointment(String username, String externalReference, CalendarProviderDTO provider) {
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));

        for (IExternalCalendarProvider externalCalendarProvider : externalCalendarProviders) {
            if (Objects.equals(externalCalendarProvider.from(), provider)) {
                final ExternalCalendarCredentials externalCalendarCredentials = externalCalendarCredentialsProvider
                        .getByUserIdAndCalendarProvider(UUID.fromString(authenticatedUser.getUID()),
                                calendarProviderConverter.reverse(externalCalendarProvider.from()));
                if (externalCalendarCredentials != null) {
                    final AppointmentDTO appointmentDTO = externalCalendarProvider.getEvent(externalReference,
                            externalCalendarCredentialsConverter.convert(new ExternalCalendarCredentialsConverterRequest(externalCalendarCredentials)));
                    if (appointmentDTO == null) {
                        throw new AppointmentNotFoundException(this.getClass(), "No appointment found for '" + externalReference + "'.");
                    }
                    return appointmentDTO;
                }
            }
        }
        throw new ActionNotAllowedException(this.getClass(), "You are not allowed to access to provider '" + provider + "'.");
    }


    public List<AppointmentDTO> getExternalAppointments(String username, LocalDateTime rangeStartingTime, LocalDateTime rangeEndingTime) {
        final List<AppointmentDTO> appointmentsDTOs = new ArrayList<>();

        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));

        Arrays.stream(CalendarProviderDTO.values()).parallel().forEach(calendarProvider ->
                appointmentsDTOs.addAll(getExternalAppointments(authenticatedUser, rangeStartingTime, rangeEndingTime, calendarProvider)));
        return appointmentsDTOs;
    }


    public List<AppointmentDTO> getExternalAppointments(String username, LocalDate rangeStartingTime, LocalDate rangeEndingTime, CalendarProviderDTO provider) {
        return getExternalAppointments(username, rangeStartingTime.atStartOfDay(), rangeEndingTime.atTime(LocalTime.MAX), provider);
    }


    public List<AppointmentDTO> getExternalAppointments(String username, LocalDateTime rangeStartingTime, LocalDateTime rangeEndingTime,
                                                        CalendarProviderDTO provider) {
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));

        return getExternalAppointments(authenticatedUser.getUID(), rangeStartingTime, rangeEndingTime, provider);
    }


    public List<AppointmentDTO> getExternalAppointments(IAuthenticatedUser authenticatedUser, LocalDateTime rangeStartingTime, LocalDateTime rangeEndingTime,
                                                        CalendarProviderDTO provider) {
        for (IExternalCalendarProvider externalCalendarProvider : externalCalendarProviders) {
            if (Objects.equals(externalCalendarProvider.from(), provider)) {
                final ExternalCalendarCredentials externalCalendarCredentials = externalCalendarCredentialsProvider
                        .getByUserIdAndCalendarProvider(UUID.fromString(authenticatedUser.getUID()),
                                calendarProviderConverter.reverse(externalCalendarProvider.from()));
                if (externalCalendarCredentials != null) {
                    return externalCalendarProvider.getEvents(rangeStartingTime, rangeEndingTime,
                            externalCalendarCredentialsConverter.convert(new ExternalCalendarCredentialsConverterRequest(externalCalendarCredentials)));
                }
            }
        }
        throw new ActionNotAllowedException(this.getClass(), "You are not allowed to access to provider '" + provider + "'.");
    }


    public List<AppointmentDTO> getExternalAppointments(String username, LocalDateTime rangeStartingTime, int numberOfEvents) {
        final List<AppointmentDTO> appointmentsDTOs = new ArrayList<>();
        Arrays.stream(CalendarProviderDTO.values()).parallel().forEach(calendarProvider ->
                appointmentsDTOs.addAll(getExternalAppointments(username, rangeStartingTime, numberOfEvents, calendarProvider)));
        return appointmentsDTOs;
    }


    public List<AppointmentDTO> getExternalAppointments(String username, LocalDate rangeStartingTime, int numberOfEvents, CalendarProviderDTO provider) {
        return getExternalAppointments(username, rangeStartingTime.atStartOfDay(), numberOfEvents, provider);
    }


    public List<AppointmentDTO> getExternalAppointments(String username, LocalDateTime rangeStartingTime, int numberOfEvents, CalendarProviderDTO provider) {
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));

        return getExternalAppointments(authenticatedUser, rangeStartingTime, numberOfEvents, provider);
    }


    public List<AppointmentDTO> getExternalAppointments(IAuthenticatedUser authenticatedUser, LocalDateTime rangeStartingTime, int numberOfEvents, CalendarProviderDTO provider) {
        for (IExternalCalendarProvider externalCalendarProvider : externalCalendarProviders) {
            if (Objects.equals(externalCalendarProvider.from(), provider)) {
                final ExternalCalendarCredentials externalCalendarCredentials = externalCalendarCredentialsProvider
                        .getByUserIdAndCalendarProvider(UUID.fromString(authenticatedUser.getUID()),
                                calendarProviderConverter.reverse(externalCalendarProvider.from()));
                if (externalCalendarCredentials != null) {
                    return externalCalendarProvider.getEvents(numberOfEvents, rangeStartingTime,
                            externalCalendarCredentialsConverter.convert(new ExternalCalendarCredentialsConverterRequest(externalCalendarCredentials)));
                }
            }
        }
        throw new ActionNotAllowedException(this.getClass(), "You are not allowed to access to provider '" + provider + "'.");
    }

}
