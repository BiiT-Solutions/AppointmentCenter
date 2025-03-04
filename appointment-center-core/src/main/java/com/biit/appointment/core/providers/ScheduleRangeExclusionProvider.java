package com.biit.appointment.core.providers;


import com.biit.appointment.persistence.entities.ScheduleRangeExclusion;
import com.biit.appointment.persistence.repositories.ScheduleRangeExclusionRepository;
import com.biit.server.providers.ElementProvider;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ScheduleRangeExclusionProvider extends ElementProvider<ScheduleRangeExclusion, Long, ScheduleRangeExclusionRepository> {

    public ScheduleRangeExclusionProvider(ScheduleRangeExclusionRepository repository) {
        super(repository);
    }


    public List<ScheduleRangeExclusion> findByUser(UUID userId) {
        return getRepository().findByUser(userId);
    }

    @Transactional
    public int deleteByUser(UUID userId) {
        return getRepository().deleteByUser(userId);
    }
}
