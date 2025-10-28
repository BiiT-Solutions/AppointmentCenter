package com.biit.appointment.core.providers;

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
