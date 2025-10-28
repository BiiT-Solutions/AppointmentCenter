package com.biit.appointment.core.models;

/*-
 * #%L
 * AppointmentCenter (DTO)
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

import com.biit.server.controllers.models.ElementDTO;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScheduleDTO extends ElementDTO<Long> {

    @Serial
    private static final long serialVersionUID = -33032948107713785L;

    private Long id;

    @NotNull
    private UUID user;

    private List<ScheduleRangeDTO> ranges;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUser() {
        return user;
    }

    public void setUser(UUID user) {
        this.user = user;
    }

    public List<ScheduleRangeDTO> getRanges() {
        if (this.ranges == null) {
            return new ArrayList<>();
        }
        return ranges;
    }

    public void setRanges(List<ScheduleRangeDTO> ranges) {
        this.ranges = ranges;
    }

    public void addRange(ScheduleRangeDTO range) {
        if (this.ranges == null) {
            this.ranges = new ArrayList<>();
        }
        this.ranges.add(range);
    }
}
