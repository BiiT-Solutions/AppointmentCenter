package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.Schedule;
import com.biit.appointment.persistence.entities.ScheduleRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@SpringBootTest
@Test(groups = {"schedule"})
public class ScheduleRepositoryTests extends AbstractTestNGSpringContextTests {

    @Autowired
    private ScheduleRepository scheduleRepository;


    @Test
    public void testContinuousRanges() {
        final Schedule schedule = new Schedule();
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(13, 30)));
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(16, 0)));
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(16, 0), LocalTime.of(17, 0)));
        schedule.setUser(UUID.randomUUID());

        Assert.assertEquals(schedule.getRanges().size(), 2);
        Assert.assertEquals(schedule.getRanges().get(0).getStartTime(), LocalTime.of(8, 30));
        Assert.assertEquals(schedule.getRanges().get(0).getEndTime(), LocalTime.of(13, 30));
        Assert.assertEquals(schedule.getRanges().get(1).getStartTime(), LocalTime.of(15, 0));
        Assert.assertEquals(schedule.getRanges().get(1).getEndTime(), LocalTime.of(17, 0));

        scheduleRepository.save(schedule);
    }
}
