package com.biit.appointment.core.test;

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


import com.biit.appointment.core.controllers.UserAvailabilityController;
import com.biit.appointment.core.models.UserAvailabilityDTO;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.core.providers.ScheduleRangeExclusionProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.entities.Schedule;
import com.biit.appointment.persistence.entities.ScheduleRange;
import com.biit.appointment.persistence.entities.ScheduleRangeExclusion;
import com.biit.appointment.persistence.repositories.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Test(groups = {"userAvailability"})
public class UserAvailabilityTests extends AbstractTestNGSpringContextTests {

    private static final String ORGANIZATION_ID = "THE ORGANIZATION";

    @Autowired
    private AppointmentProvider appointmentProvider;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    public UserAvailabilityController userAvailabilityController;

    @Autowired
    private ScheduleRangeExclusionProvider scheduleRangeExclusionProvider;

    private final UUID user = UUID.randomUUID();

    //Monday
    private final LocalDate today = LocalDate.of(2025, 2, 24);


    public static ExaminationType generateExaminationType(String name, AppointmentType appointmentType) {
        return new ExaminationType(name, ORGANIZATION_ID, appointmentType);
    }

    private Appointment generateAppointment(LocalDateTime onDate, UUID organizer, Set<UUID> attendees) {
        final Appointment appointment = new Appointment();
        appointment.setOrganizer(organizer);
        appointment.setOrganizationId(ORGANIZATION_ID);
        appointment.setAttendees(attendees);
        appointment.setCost(50D);

        appointment.setStartTime(onDate);
        appointment.setEndTime(appointment.getStartTime().plusHours(2));

        return appointmentProvider.save(appointment);
    }

    @BeforeMethod
    public void generateSchedule() {
        final Schedule schedule = new Schedule();
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(13, 30)));
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(20, 0)));
        schedule.addRange(new ScheduleRange(DayOfWeek.FRIDAY, LocalTime.of(12, 0), LocalTime.of(20, 0)));
        schedule.setUser(user);

        scheduleRepository.save(schedule);
    }

    @Test
    public void getSlotsBetweenAppointments() {
        generateAppointment(LocalDateTime.of(today, LocalTime.of(12, 30)), user, new HashSet<>());
        generateAppointment(LocalDateTime.of(today, LocalTime.of(14, 15)), user, new HashSet<>());
        //1 hour free!
        generateAppointment(LocalDateTime.of(today, LocalTime.of(17, 15)), UUID.randomUUID(), Set.of(user));

        List<UserAvailabilityDTO> availabilities = userAvailabilityController.getAvailability(
                user, LocalDateTime.of(today, LocalTime.of(12, 20)), LocalDateTime.of(today, LocalTime.of(20, 0)),
                30, 3);

        Assert.assertEquals(availabilities.size(), 3);
        Assert.assertEquals(availabilities.get(0).getStartTime(), LocalDateTime.of(today, LocalTime.of(16, 15)));
        Assert.assertEquals(availabilities.get(1).getStartTime(), LocalDateTime.of(today, LocalTime.of(16, 45)));
        Assert.assertEquals(availabilities.get(2).getStartTime(), LocalDateTime.of(today, LocalTime.of(19, 15)));
    }


    @Test
    public void getSlotsBetweenTwoDays() {
        generateAppointment(LocalDateTime.of(today, LocalTime.of(17, 15)), UUID.randomUUID(), Set.of(user));

        //Search on Monday late. So only one slot is available, the other 2 on friday.
        List<UserAvailabilityDTO> availabilities = userAvailabilityController.getAvailability(
                user, LocalDateTime.of(today, LocalTime.of(18, 0)), LocalDateTime.of(today.plusDays(7), LocalTime.of(20, 0)),
                30, 3);

        Assert.assertEquals(availabilities.size(), 3);
        Assert.assertEquals(availabilities.get(0).getStartTime(), LocalDateTime.of(today, LocalTime.of(19, 15)));
        Assert.assertEquals(availabilities.get(1).getStartTime(), LocalDateTime.of(today.plusDays(4), LocalTime.of(12, 0)));
        Assert.assertEquals(availabilities.get(2).getStartTime(), LocalDateTime.of(today.plusDays(4), LocalTime.of(12, 30)));
    }


    @Test
    public void getSlotsBetweenTreeDaysAndTwoWeeks() {
        generateAppointment(LocalDateTime.of(today, LocalTime.of(17, 15)), UUID.randomUUID(), Set.of(user));
        generateAppointment(LocalDateTime.of(today.plusDays(4), LocalTime.of(11, 15)), UUID.randomUUID(), Set.of(user));
        generateAppointment(LocalDateTime.of(today.plusDays(4), LocalTime.of(13, 0)), UUID.randomUUID(), Set.of(user));
        generateAppointment(LocalDateTime.of(today.plusDays(4), LocalTime.of(15, 20)), UUID.randomUUID(), Set.of(user));
        generateAppointment(LocalDateTime.of(today.plusDays(4), LocalTime.of(17, 30)), UUID.randomUUID(), Set.of(user));

        //Search on Monday late. So only one slot is available, the other 2 on friday.
        List<UserAvailabilityDTO> availabilities = userAvailabilityController.getAvailability(
                user, LocalDateTime.of(today, LocalTime.of(18, 0)), LocalDateTime.of(today.plusDays(7), LocalTime.of(20, 0)),
                30, 3);

        Assert.assertEquals(availabilities.size(), 3);
        Assert.assertEquals(availabilities.get(0).getStartTime(), LocalDateTime.of(today, LocalTime.of(19, 15)));
        Assert.assertEquals(availabilities.get(1).getStartTime(), LocalDateTime.of(today.plusDays(4), LocalTime.of(19, 30)));
        Assert.assertEquals(availabilities.get(2).getStartTime(), LocalDateTime.of(today.plusDays(7), LocalTime.of(8, 30)));
    }


    @Test
    public void getSlotsBetweenTwoDaysInvalidExclusion() {
        generateAppointment(LocalDateTime.of(today, LocalTime.of(17, 15)), UUID.randomUUID(), Set.of(user));

        //Monday is holidayed!
        scheduleRangeExclusionProvider.save(new ScheduleRangeExclusion(user, today.plusDays(7)));

        //Search on Monday late. So only one slot is available, the other 2 on next monday.
        List<UserAvailabilityDTO> availabilities = userAvailabilityController.getAvailability(
                user, LocalDateTime.of(today, LocalTime.of(18, 0)), LocalDateTime.of(today.plusDays(7), LocalTime.of(20, 0)),
                30, 3);

        Assert.assertEquals(availabilities.size(), 3);
        Assert.assertEquals(availabilities.get(0).getStartTime(), LocalDateTime.of(today, LocalTime.of(19, 15)));
        Assert.assertEquals(availabilities.get(1).getStartTime(), LocalDateTime.of(today.plusDays(4), LocalTime.of(12, 0)));
        Assert.assertEquals(availabilities.get(2).getStartTime(), LocalDateTime.of(today.plusDays(4), LocalTime.of(12, 30)));
    }


    @Test
    public void getSlotsBetweenTwoDaysWithExclusions() {
        generateAppointment(LocalDateTime.of(today, LocalTime.of(17, 15)), UUID.randomUUID(), Set.of(user));

        //Friday is holidayed!
        scheduleRangeExclusionProvider.save(new ScheduleRangeExclusion(user, today.plusDays(4)));

        //Search on Monday late. So only one slot is available, the other 2 on next monday.
        List<UserAvailabilityDTO> availabilities = userAvailabilityController.getAvailability(
                user, LocalDateTime.of(today, LocalTime.of(18, 0)), LocalDateTime.of(today.plusDays(7), LocalTime.of(20, 0)),
                30, 3);

        Assert.assertEquals(availabilities.size(), 3);
        Assert.assertEquals(availabilities.get(0).getStartTime(), LocalDateTime.of(today, LocalTime.of(19, 15)));
        Assert.assertEquals(availabilities.get(1).getStartTime(), LocalDateTime.of(today.plusDays(7), LocalTime.of(8, 30)));
        Assert.assertEquals(availabilities.get(2).getStartTime(), LocalDateTime.of(today.plusDays(7), LocalTime.of(9, 0)));
    }


    @Test
    public void getSlotsForDefaultSchedule() {
        final UUID user = UUID.randomUUID();
        //Search from Sunday. Return Monday after default hour (8:00)
        List<UserAvailabilityDTO> availabilities = userAvailabilityController.getAvailability(
                user, LocalDateTime.of(today.minusDays(1), LocalTime.of(12, 20)), LocalDateTime.of(today, LocalTime.of(20, 0)),
                30, 3);

        Assert.assertEquals(availabilities.size(), 3);
        Assert.assertEquals(availabilities.get(0).getStartTime(), LocalDateTime.of(today, LocalTime.of(8, 0)));
        Assert.assertEquals(availabilities.get(1).getStartTime(), LocalDateTime.of(today, LocalTime.of(8, 30)));
        Assert.assertEquals(availabilities.get(2).getStartTime(), LocalDateTime.of(today, LocalTime.of(9, 0)));
    }

}
