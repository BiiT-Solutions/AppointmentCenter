package com.biit.appointment.core.models;

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
