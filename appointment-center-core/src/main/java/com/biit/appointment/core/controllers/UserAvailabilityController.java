package com.biit.appointment.core.controllers;

import com.biit.appointment.core.converters.AppointmentConverter;
import com.biit.appointment.core.converters.CalendarProviderConverter;
import com.biit.appointment.core.converters.ExternalCalendarCredentialsConverter;
import com.biit.appointment.core.converters.models.ExternalCalendarCredentialsConverterRequest;
import com.biit.appointment.core.exceptions.ExternalCalendarActionException;
import com.biit.appointment.core.exceptions.ExternalCalendarNotFoundException;
import com.biit.appointment.core.models.UserAvailabilityDTO;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.core.providers.ExternalCalendarCredentialsProvider;
import com.biit.appointment.core.providers.ScheduleProvider;
import com.biit.appointment.core.providers.ScheduleRangeExclusionProvider;
import com.biit.appointment.core.services.IExternalProviderCalendarService;
import com.biit.appointment.logger.AppointmentCenterLogger;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.ExternalCalendarCredentials;
import com.biit.appointment.persistence.entities.Schedule;
import com.biit.appointment.persistence.entities.ScheduleRange;
import com.biit.appointment.persistence.entities.ScheduleRangeExclusion;
import com.biit.server.exceptions.UserNotFoundException;
import com.biit.server.security.IAuthenticatedUser;
import com.biit.server.security.IAuthenticatedUserProvider;
import org.springframework.stereotype.Controller;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Controller
public class UserAvailabilityController {

    private static final int DAYS_OF_WEEK = 7;
    private static final int MAX_DAYS_CHECK = 30;

    private final AppointmentProvider appointmentProvider;
    private final AppointmentConverter appointmentConverter;
    private final CalendarProviderConverter calendarProviderConverter;
    private final ScheduleProvider scheduleProvider;
    private final IAuthenticatedUserProvider authenticatedUserProvider;
    private final ScheduleRangeExclusionProvider scheduleRangeExclusionProvider;

    private final List<IExternalProviderCalendarService> externalCalendarControllers;
    private final ExternalCalendarCredentialsController externalCalendarCredentialsController;
    private final ExternalCalendarCredentialsProvider externalCalendarCredentialsProvider;
    private final ExternalCalendarCredentialsConverter externalCalendarCredentialsConverter;

    public UserAvailabilityController(AppointmentProvider appointmentProvider, AppointmentConverter appointmentConverter,
                                      CalendarProviderConverter calendarProviderConverter,
                                      ScheduleProvider scheduleProvider, IAuthenticatedUserProvider authenticatedUserProvider,
                                      ScheduleRangeExclusionProvider scheduleRangeExclusionProvider,
                                      List<IExternalProviderCalendarService> externalCalendarControllers,
                                      ExternalCalendarCredentialsController externalCalendarCredentialsController,
                                      ExternalCalendarCredentialsProvider externalCalendarCredentialsProvider,
                                      ExternalCalendarCredentialsConverter externalCalendarCredentialsConverter) {
        this.appointmentProvider = appointmentProvider;
        this.appointmentConverter = appointmentConverter;
        this.calendarProviderConverter = calendarProviderConverter;
        this.scheduleProvider = scheduleProvider;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.scheduleRangeExclusionProvider = scheduleRangeExclusionProvider;
        this.externalCalendarControllers = externalCalendarControllers;
        this.externalCalendarCredentialsController = externalCalendarCredentialsController;
        this.externalCalendarCredentialsProvider = externalCalendarCredentialsProvider;
        this.externalCalendarCredentialsConverter = externalCalendarCredentialsConverter;
    }

    public List<UserAvailabilityDTO> getAvailability(String username, LocalDateTime start, LocalDateTime end, int slotDuration, int numberOfOptions) {
        final IAuthenticatedUser authenticatedUser = authenticatedUserProvider.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(this.getClass(),
                        "No user with username '" + username + "' found!"));
        return getAvailability(UUID.fromString(authenticatedUser.getUID()), start, end, slotDuration, numberOfOptions);
    }


    public List<UserAvailabilityDTO> getAvailability(UUID userUUID, LocalDateTime start, LocalDateTime end, int slotDuration, int numberOfOptions) {
        if (start == null) {
            start = LocalDateTime.now();
        }
        if (end == null) {
            end = LocalDateTime.now().plusMonths(1);
        }
        //Gets user availability.
        final Schedule userSchedule = scheduleProvider.findByUser(userUUID).orElse(scheduleProvider.getDefaultSchedule(userUUID));

        //Gets any appointment where the user is involved in these specific dates.
        final List<Appointment> appointments = appointmentProvider.findNextByOrganizer(userUUID, start.with(LocalTime.MIN), end.with(LocalTime.MAX));
        appointments.addAll(appointmentProvider.findNextBySpeakersIn(List.of(userUUID), start.with(LocalTime.MIN), end.with(LocalTime.MAX)));
        appointments.addAll(appointmentProvider.findNextByAttendeesIn(List.of(userUUID), start.with(LocalTime.MIN), end.with(LocalTime.MAX)));
        Collections.sort(appointments);

        //Get appointments from external sources.
        appointments.addAll(getExternalCalendarAppointments(userUUID, start, end));

        //Get any schedule exclusions to avoid them.
        final List<ScheduleRangeExclusion> scheduleRangeExclusions = scheduleRangeExclusionProvider.findByUser(userUUID);

        final List<UserAvailabilityDTO> userAvailabilityDTOS = new ArrayList<>();

        int days = 0;
        //From starting time, get the first N slots available.
        do {
            for (ScheduleRange scheduleRange : userSchedule.getRange(DayOfWeek.of((start.getDayOfWeek().getValue() + days)
                    //Days of the week are between 1-7.
                    - (((start.getDayOfWeek().getValue() + days - 1) / DAYS_OF_WEEK) * DAYS_OF_WEEK)))) {
                //Time to check if fits or not.
                UserAvailabilityDTO slot = new UserAvailabilityDTO(userUUID,
                        LocalDateTime.of(start.toLocalDate().plusDays(days), scheduleRange.getStartTime()),
                        LocalDateTime.of(start.toLocalDate().plusDays(days), scheduleRange.getStartTime().plusMinutes(slotDuration)));
                do {
                    //Convert scheduleRange to LocalDateTime.
                    final LocalDateTime scheduleRangeStartTimeTheDay = LocalDateTime.of(start.toLocalDate().plusDays(days)
                            .with(TemporalAdjusters.nextOrSame(scheduleRange.getDayOfWeek())), scheduleRange.getStartTime());
                    final LocalDateTime scheduleRangeEndTimeTheDay = LocalDateTime.of(start.toLocalDate().plusDays(days)
                            .with(TemporalAdjusters.nextOrSame(scheduleRange.getDayOfWeek())), scheduleRange.getEndTime());
                    //Merge schedule with the search boundaries.
                    final LocalDateTime lowerBoundary = start.isBefore(scheduleRangeStartTimeTheDay) ? scheduleRangeStartTimeTheDay : start;
                    final LocalDateTime upperBoundary = end.isAfter(scheduleRangeEndTimeTheDay) ? scheduleRangeEndTimeTheDay : end;
                    //Check if the selected slot fits on the availability.
                    if ((slot.getStartTime().isAfter(lowerBoundary) || slot.getStartTime().equals(lowerBoundary))
                            && (slot.getEndTime().isBefore(upperBoundary) || slot.getEndTime().equals(upperBoundary))
                            && userAvailabilityDTOS.size() < numberOfOptions) {

                        //Check if the slot already is occupied by an exclusion.
                        final ScheduleRangeExclusion exclusion = collidesWithExclusion(scheduleRangeExclusions, slot);
                        if (exclusion != null) {
                            slot = new UserAvailabilityDTO(userUUID, exclusion.getEndTime(),
                                    exclusion.getEndTime().plusMinutes(slotDuration));
                            continue;
                        }

                        //Check if the slot already is occupied by an appointment.
                        final Appointment collision = collidesWithAppointment(appointments, slot);
                        if (collision != null) {
                            slot = new UserAvailabilityDTO(userUUID, collision.getEndTime(),
                                    collision.getEndTime().plusMinutes(slotDuration));
                            continue;
                        }

                        userAvailabilityDTOS.add(slot);
                        slot = new UserAvailabilityDTO(userUUID, slot.getEndTime(),
                                slot.getEndTime().plusMinutes(slotDuration));
                    } else {
                        //Advance the slot
                        slot = new UserAvailabilityDTO(userUUID, slot.getEndTime(),
                                slot.getEndTime().plusMinutes(slotDuration));
                    }
                    if (slot.getEndTime().isAfter(upperBoundary)) {
                        break;
                    }
                } while (true);
            }
            days++;
        } while (userAvailabilityDTOS.size() < numberOfOptions && days < MAX_DAYS_CHECK);
        return userAvailabilityDTOS;
    }


    private List<Appointment> getExternalCalendarAppointments(UUID userUUID, final LocalDateTime start, final LocalDateTime end) {
        final List<Appointment> appointments = new ArrayList<>();
        externalCalendarControllers.parallelStream().forEach(provider -> {
            final ExternalCalendarCredentials externalCalendarCredentials = externalCalendarCredentialsProvider
                            .getByUserIdAndCalendarProvider(userUUID, calendarProviderConverter.reverse(provider.from()));
            if (externalCalendarCredentials != null) {
                try {
                    final List<Appointment> externalAppointments = appointmentConverter.reverseAll(provider.getEvents(start, end,
                            externalCalendarCredentialsConverter.convert(new ExternalCalendarCredentialsConverterRequest(externalCalendarCredentials))));
                    appointments.addAll(externalAppointments.stream().filter(appointmentDTO -> !appointmentDTO.isDeleted()).toList());
                } catch (ExternalCalendarActionException | ExternalCalendarNotFoundException e) {
                    AppointmentCenterLogger.errorMessage(this.getClass(), e);
                }
            } else {
                AppointmentCenterLogger.debug(this.getClass(), "No external calendar provider found for '{}'.", provider.from());
            }
        });
        return appointments;
    }


    private Appointment collidesWithAppointment(List<Appointment> appointments, UserAvailabilityDTO slot) {
        return appointments.stream().filter(
                appointment -> ((slot.getStartTime().isAfter(appointment.getStartTime()) || slot.getStartTime().equals(appointment.getStartTime()))
                        && slot.getStartTime().isBefore(appointment.getEndTime()))
                        || ((slot.getEndTime().isAfter(appointment.getStartTime()))
                        && slot.getEndTime().isBefore(appointment.getEndTime()))
        ).findAny().orElse(null);
    }

    private ScheduleRangeExclusion collidesWithExclusion(List<ScheduleRangeExclusion> exclusions, UserAvailabilityDTO slot) {
        return exclusions.stream().filter(
                exclusion -> ((slot.getStartTime().isAfter(exclusion.getStartTime()) || slot.getStartTime().equals(exclusion.getStartTime()))
                        && slot.getStartTime().isBefore(exclusion.getEndTime()))
                        || ((slot.getEndTime().isAfter(exclusion.getStartTime()))
                        && slot.getEndTime().isBefore(exclusion.getEndTime()))
        ).findAny().orElse(null);
    }
}
