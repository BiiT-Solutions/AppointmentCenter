package com.biit.appointment.core.models;

import com.biit.server.controllers.models.ElementDTO;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AvailabilityDTO extends ElementDTO<Long> {

    @Serial
    private static final long serialVersionUID = -33032948107713785L;

    private Long id;

    private UUID user;

    private List<AvailabilityRangeDTO> ranges;

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

    public List<AvailabilityRangeDTO> getRanges() {
        if (this.ranges == null) {
            return new ArrayList<>();
        }
        return ranges;
    }

    public void setRanges(List<AvailabilityRangeDTO> ranges) {
        this.ranges = ranges;
    }

    public void addRange(AvailabilityRangeDTO range) {
        if (this.ranges == null) {
            this.ranges = new ArrayList<>();
        }
        this.ranges.add(range);
    }
}
