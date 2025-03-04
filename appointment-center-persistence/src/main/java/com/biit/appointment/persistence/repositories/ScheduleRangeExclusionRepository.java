package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.ScheduleRangeExclusion;
import com.biit.server.persistence.repositories.ElementRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScheduleRangeExclusionRepository extends ElementRepository<ScheduleRangeExclusion, Long> {

    List<ScheduleRangeExclusion> findByUser(UUID userId);

    int deleteByUser(UUID userId);
}
