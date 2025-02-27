package com.biit.appointment.persistence.entities;

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
