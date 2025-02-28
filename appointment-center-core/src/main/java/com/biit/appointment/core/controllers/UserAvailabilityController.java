package com.biit.appointment.core.controllers;

import com.biit.appointment.core.models.UserAvailabilityDTO;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.core.providers.ScheduleProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.Schedule;
import com.biit.appointment.persistence.entities.ScheduleRange;
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
    private final ScheduleProvider scheduleProvider;
    private final IAuthenticatedUserProvider authenticatedUserProvider;

    public UserAvailabilityController(AppointmentProvider appointmentProvider, ScheduleProvider scheduleProvider,
                                      IAuthenticatedUserProvider authenticatedUserProvider) {
        this.appointmentProvider = appointmentProvider;
        this.scheduleProvider = scheduleProvider;
        this.authenticatedUserProvider = authenticatedUserProvider;
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
        final Schedule userSchedule = scheduleProvider.findByUser(userUUID).orElse(new Schedule(userUUID));

        //Gets any appointment where the user is involved this specific dates.
        final List<Appointment> appointments = appointmentProvider.findNextByOrganizer(userUUID, start.with(LocalTime.MIN), end.with(LocalTime.MAX));
        appointments.addAll(appointmentProvider.findNextBySpeakersIn(List.of(userUUID), start.with(LocalTime.MIN), end.with(LocalTime.MAX)));
        appointments.addAll(appointmentProvider.findNextByAttendeesIn(List.of(userUUID), start.with(LocalTime.MIN), end.with(LocalTime.MAX)));
        Collections.sort(appointments);

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
                        //Check if the slot already is occupied by an appointment.
                        final Appointment collision = collides(appointments, slot);
                        if (collision == null) {
                            userAvailabilityDTOS.add(slot);
                            slot = new UserAvailabilityDTO(userUUID, slot.getEndTime(),
                                    slot.getEndTime().plusMinutes(slotDuration));
                        } else {
                            slot = new UserAvailabilityDTO(userUUID, collision.getEndTime(),
                                    collision.getEndTime().plusMinutes(slotDuration));
                        }
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

    private Appointment collides(List<Appointment> appointments, UserAvailabilityDTO slot) {
        return appointments.stream().filter(
                appointment -> ((slot.getStartTime().isAfter(appointment.getStartTime()) || slot.getStartTime().equals(appointment.getStartTime()))
                        && slot.getStartTime().isBefore(appointment.getEndTime()))
                        || ((slot.getEndTime().isAfter(appointment.getStartTime()))
                        && slot.getEndTime().isBefore(appointment.getEndTime()))
        ).findAny().orElse(null);
    }
}
