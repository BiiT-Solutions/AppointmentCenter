package com.biit.appointment.core.providers;


import com.biit.appointment.persistence.entities.Schedule;
import com.biit.appointment.persistence.entities.ScheduleRange;
import com.biit.appointment.persistence.repositories.ScheduleRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class ScheduleProvider extends ElementProvider<Schedule, Long, ScheduleRepository> {

    @Value("${default.schedule.starting.time.hour:8}")
    private int defaultScheduleStartingTimeHours;

    @Value("${default.schedule.ending.time.hour:19}")
    private int defaultScheduleEndingTimeHours;

    @Value("${default.schedule.week.days:MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY}")
    private DayOfWeek[] defaultScheduleDaysOfWeek;

    public ScheduleProvider(ScheduleRepository repository) {
        super(repository);
    }


    public Optional<Schedule> findByUser(UUID userId) {
        return getRepository().findByUser(userId);
    }


    public Schedule set(Collection<ScheduleRange> scheduleRanges, UUID user) {
        final Schedule userSchedule = getRepository().findByUser(user).orElse(new Schedule(user));
        userSchedule.setRanges(new ArrayList<>(scheduleRanges));
        return getRepository().save(userSchedule);
    }


    public Schedule update(ScheduleRange scheduleRange, UUID user) {
        final Schedule userSchedule = getRepository().findByUser(user).orElse(new Schedule(user));
        userSchedule.updateRange(scheduleRange);
        return getRepository().save(userSchedule);
    }


    public Schedule add(ScheduleRange scheduleRange, UUID user) {
        Schedule userSchedule = getRepository().findByUser(user).orElse(new Schedule(user));
        if (userSchedule.getId() == null) {
            //Ensuring is saved.
            userSchedule = getRepository().save(userSchedule);
        }
        userSchedule.addRange(scheduleRange);
        return getRepository().save(userSchedule);
    }


    public Schedule removeRange(Long scheduleRangeId, UUID user) {
        final Schedule userSchedule = getRepository().findByUser(user).orElse(new Schedule(user));
        //No schedule defined. Nothing to remove.
        if (userSchedule.getId() == null) {
            return null;
        }
        for (ScheduleRange scheduleRange : new ArrayList<>(userSchedule.getRanges())) {
            if (Objects.equals(scheduleRange.getId(), scheduleRangeId)) {
                userSchedule.removeRange(scheduleRange);
            }
        }
        return getRepository().save(userSchedule);
    }


    public Schedule remove(ScheduleRange scheduleRange, UUID user) {
        final Schedule userSchedule = getRepository().findByUser(user).orElse(new Schedule(user));
        //No schedule defined. Nothing to remove.
        if (userSchedule.getId() == null) {
            return null;
        }
        userSchedule.removeRange(scheduleRange);
        return getRepository().save(userSchedule);
    }


    public Schedule removeAll(UUID user) {
        final Schedule userSchedule = getRepository().findByUser(user).orElse(new Schedule(user));
        //No schedule defined. Nothing to remove.
        if (userSchedule.getId() == null) {
            return null;
        }
        userSchedule.getRanges().clear();
        return getRepository().save(userSchedule);
    }


    public Schedule getDefaultSchedule(UUID user) {
        final Schedule userSchedule = new Schedule(user);
        for (DayOfWeek dayOfWeek : defaultScheduleDaysOfWeek) {
            userSchedule.addRange(new ScheduleRange(dayOfWeek, LocalTime.MIN.plusHours(defaultScheduleStartingTimeHours),
                    LocalTime.MIN.plusHours(defaultScheduleEndingTimeHours)));
        }
        return userSchedule;
    }
}
