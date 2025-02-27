package com.biit.appointment.core.providers;


import com.biit.appointment.persistence.entities.Schedule;
import com.biit.appointment.persistence.entities.ScheduleRange;
import com.biit.appointment.persistence.repositories.ScheduleRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
public class ScheduleProvider extends ElementProvider<Schedule, Long, ScheduleRepository> {

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


    public Schedule add(ScheduleRange scheduleRange, UUID user) {
        final Schedule userSchedule = getRepository().findByUser(user).orElse(new Schedule(user));
        userSchedule.addRange(scheduleRange);
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
}
