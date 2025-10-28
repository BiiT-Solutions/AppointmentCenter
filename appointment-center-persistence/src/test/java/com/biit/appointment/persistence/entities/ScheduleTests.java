package com.biit.appointment.persistence.entities;

/*-
 * #%L
 * AppointmentCenter (Persistence)
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

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Test(groups = "schedule")
public class ScheduleTests {


    @Test
    public void testContinuousRanges() {
        final Schedule schedule = new Schedule();
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(13, 30)));
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 0)));
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(17, 0)));

        Assert.assertEquals(schedule.getRanges().size(), 2);
        Assert.assertEquals(schedule.getRanges().get(0).getStartTime(), LocalTime.of(8, 30));
        Assert.assertEquals(schedule.getRanges().get(0).getEndTime(), LocalTime.of(13, 30));
        Assert.assertEquals(schedule.getRanges().get(1).getStartTime(), LocalTime.of(15, 0));
        Assert.assertEquals(schedule.getRanges().get(1).getEndTime(), LocalTime.of(17, 0));
    }


    @Test
    public void testSimpleOverlapRanges() {
        final Schedule schedule = new Schedule();
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(13, 30)));
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 30)));
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(17, 0)));

        Assert.assertEquals(schedule.getRanges().size(), 2);
        Assert.assertEquals(schedule.getRanges().get(0).getStartTime(), LocalTime.of(8, 30));
        Assert.assertEquals(schedule.getRanges().get(0).getEndTime(), LocalTime.of(13, 30));
        Assert.assertEquals(schedule.getRanges().get(1).getStartTime(), LocalTime.of(15, 0));
        Assert.assertEquals(schedule.getRanges().get(1).getEndTime(), LocalTime.of(17, 0));
    }


    @Test
    public void testMultipleOverlapRanges() {
        final Schedule schedule = new Schedule();
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(15, 30)));
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 30)));
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(17, 0)));

        Assert.assertEquals(schedule.getRanges().size(), 1);
        Assert.assertEquals(schedule.getRanges().get(0).getStartTime(), LocalTime.of(8, 30));
        Assert.assertEquals(schedule.getRanges().get(0).getEndTime(), LocalTime.of(17, 0));
    }


    @Test
    public void testMultipleOverlapRanges2() {
        final Schedule schedule = new Schedule();
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(11, 30)));
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(9, 30), LocalTime.of(15, 0)));
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 30)));
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(17, 0)));

        Assert.assertEquals(schedule.getRanges().size(), 1);
        Assert.assertEquals(schedule.getRanges().get(0).getStartTime(), LocalTime.of(8, 30));
        Assert.assertEquals(schedule.getRanges().get(0).getEndTime(), LocalTime.of(17, 0));
    }


    @Test
    public void testTwoOverlapsRanges() {
        final Schedule schedule = new Schedule();
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(11, 30)));
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(9, 30), LocalTime.of(13, 0)));
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 1)));
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(17, 0)));

        Assert.assertEquals(schedule.getRanges().size(), 2);
        Assert.assertEquals(schedule.getRanges().get(0).getStartTime(), LocalTime.of(8, 30));
        Assert.assertEquals(schedule.getRanges().get(0).getEndTime(), LocalTime.of(13, 0));
        Assert.assertEquals(schedule.getRanges().get(1).getStartTime(), LocalTime.of(15, 0));
        Assert.assertEquals(schedule.getRanges().get(1).getEndTime(), LocalTime.of(17, 0));
    }


    @Test
    public void testRemoveRanges() {
        final Schedule schedule = new Schedule();
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(13, 0)));
        schedule.removeRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0)));
        schedule.removeRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(11, 30), LocalTime.of(12, 30)));

        Assert.assertEquals(schedule.getRanges().size(), 3);
        Assert.assertEquals(schedule.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(schedule.getRanges().get(0).getEndTime(), LocalTime.of(10, 0));
        Assert.assertEquals(schedule.getRanges().get(1).getStartTime(), LocalTime.of(11, 0));
        Assert.assertEquals(schedule.getRanges().get(1).getEndTime(), LocalTime.of(11, 30));
        Assert.assertEquals(schedule.getRanges().get(2).getStartTime(), LocalTime.of(12, 30));
        Assert.assertEquals(schedule.getRanges().get(2).getEndTime(), LocalTime.of(13, 0));
    }

    @Test
    public void testRemoveRangeAfter() {
        final Schedule schedule = new Schedule();
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(13, 0)));
        schedule.removeRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(12, 0), LocalTime.of(15, 0)));

        Assert.assertEquals(schedule.getRanges().size(), 1);
        Assert.assertEquals(schedule.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(schedule.getRanges().get(0).getEndTime(), LocalTime.of(12, 0));
    }

    @Test
    public void testRemoveRangeBefore() {
        final Schedule schedule = new Schedule();
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(13, 0)));
        schedule.removeRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(7, 0), LocalTime.of(11, 0)));

        Assert.assertEquals(schedule.getRanges().size(), 1);
        Assert.assertEquals(schedule.getRanges().get(0).getStartTime(), LocalTime.of(11, 0));
        Assert.assertEquals(schedule.getRanges().get(0).getEndTime(), LocalTime.of(13, 0));
    }

    @Test
    public void testRemoveRangeInvalid() {
        final Schedule schedule = new Schedule();
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(13, 0)));
        schedule.removeRange(new ScheduleRange(DayOfWeek.THURSDAY, LocalTime.of(7, 0), LocalTime.of(11, 0)));

        Assert.assertEquals(schedule.getRanges().size(), 1);
        Assert.assertEquals(schedule.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(schedule.getRanges().get(0).getEndTime(), LocalTime.of(13, 0));
    }
}
