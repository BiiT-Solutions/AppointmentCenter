package com.biit.appointment.persistence.entities;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Test(groups = "availability")
public class AvailabilityTests {


    @Test
    public void testContinuousRanges() {
        final Availability availability = new Availability();
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(13, 30)));
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 0)));
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(17, 0)));

        Assert.assertEquals(availability.getRanges().size(), 2);
        Assert.assertEquals(availability.getRanges().get(0).getStartTime(), LocalTime.of(8, 30));
        Assert.assertEquals(availability.getRanges().get(0).getEndTime(), LocalTime.of(13, 30));
        Assert.assertEquals(availability.getRanges().get(1).getStartTime(), LocalTime.of(15, 0));
        Assert.assertEquals(availability.getRanges().get(1).getEndTime(), LocalTime.of(17, 0));
    }


    @Test
    public void testSimpleOverlapRanges() {
        final Availability availability = new Availability();
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(13, 30)));
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 30)));
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(17, 0)));

        Assert.assertEquals(availability.getRanges().size(), 2);
        Assert.assertEquals(availability.getRanges().get(0).getStartTime(), LocalTime.of(8, 30));
        Assert.assertEquals(availability.getRanges().get(0).getEndTime(), LocalTime.of(13, 30));
        Assert.assertEquals(availability.getRanges().get(1).getStartTime(), LocalTime.of(15, 0));
        Assert.assertEquals(availability.getRanges().get(1).getEndTime(), LocalTime.of(17, 0));
    }


    @Test
    public void testMultipleOverlapRanges() {
        final Availability availability = new Availability();
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(15, 30)));
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 30)));
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(17, 0)));

        Assert.assertEquals(availability.getRanges().size(), 1);
        Assert.assertEquals(availability.getRanges().get(0).getStartTime(), LocalTime.of(8, 30));
        Assert.assertEquals(availability.getRanges().get(0).getEndTime(), LocalTime.of(17, 0));
    }


    @Test
    public void testMultipleOverlapRanges2() {
        final Availability availability = new Availability();
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(11, 30)));
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(9, 30), LocalTime.of(15, 0)));
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 30)));
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(17, 0)));

        Assert.assertEquals(availability.getRanges().size(), 1);
        Assert.assertEquals(availability.getRanges().get(0).getStartTime(), LocalTime.of(8, 30));
        Assert.assertEquals(availability.getRanges().get(0).getEndTime(), LocalTime.of(17, 0));
    }


    @Test
    public void testTwoOverlapsRanges() {
        final Availability availability = new Availability();
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(11, 30)));
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(9, 30), LocalTime.of(13, 0)));
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 1)));
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(17, 0)));

        Assert.assertEquals(availability.getRanges().size(), 2);
        Assert.assertEquals(availability.getRanges().get(0).getStartTime(), LocalTime.of(8, 30));
        Assert.assertEquals(availability.getRanges().get(0).getEndTime(), LocalTime.of(13, 0));
        Assert.assertEquals(availability.getRanges().get(1).getStartTime(), LocalTime.of(15, 0));
        Assert.assertEquals(availability.getRanges().get(1).getEndTime(), LocalTime.of(17, 0));
    }


    @Test
    public void testRemoveRanges() {
        final Availability availability = new Availability();
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(13, 0)));
        availability.removeRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0)));
        availability.removeRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(11, 30), LocalTime.of(12, 30)));

        Assert.assertEquals(availability.getRanges().size(), 3);
        Assert.assertEquals(availability.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(availability.getRanges().get(0).getEndTime(), LocalTime.of(10, 0));
        Assert.assertEquals(availability.getRanges().get(1).getStartTime(), LocalTime.of(11, 0));
        Assert.assertEquals(availability.getRanges().get(1).getEndTime(), LocalTime.of(11, 30));
        Assert.assertEquals(availability.getRanges().get(2).getStartTime(), LocalTime.of(12, 30));
        Assert.assertEquals(availability.getRanges().get(2).getEndTime(), LocalTime.of(13, 0));
    }

    @Test
    public void testRemoveRangeAfter() {
        final Availability availability = new Availability();
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(13, 0)));
        availability.removeRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(12, 0), LocalTime.of(15, 0)));

        Assert.assertEquals(availability.getRanges().size(), 1);
        Assert.assertEquals(availability.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(availability.getRanges().get(0).getEndTime(), LocalTime.of(12, 0));
    }

    @Test
    public void testRemoveRangeBefore() {
        final Availability availability = new Availability();
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(13, 0)));
        availability.removeRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(7, 0), LocalTime.of(11, 0)));

        Assert.assertEquals(availability.getRanges().size(), 1);
        Assert.assertEquals(availability.getRanges().get(0).getStartTime(), LocalTime.of(11, 0));
        Assert.assertEquals(availability.getRanges().get(0).getEndTime(), LocalTime.of(13, 0));
    }

    @Test
    public void testRemoveRangeInvalid() {
        final Availability availability = new Availability();
        availability.addRange(new AvailabilityRange(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(13, 0)));
        availability.removeRange(new AvailabilityRange(DayOfWeek.THURSDAY, LocalTime.of(7, 0), LocalTime.of(11, 0)));

        Assert.assertEquals(availability.getRanges().size(), 1);
        Assert.assertEquals(availability.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(availability.getRanges().get(0).getEndTime(), LocalTime.of(13, 0));
    }
}
