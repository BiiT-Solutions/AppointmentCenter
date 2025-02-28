package com.biit.appointment.rest.api.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class AvailabilitySearch {

    private UUID user;

    private LocalDateTime start;

    private LocalDateTime end;

    private int slotDuration;

    private int slots;

    public AvailabilitySearch() {
        super();
    }

    public AvailabilitySearch(UUID user, LocalDateTime start, LocalDateTime end, int slotDuration, int slots) {
        this();
        this.user = user;
        this.start = start;
        this.end = end;
        this.slotDuration = slotDuration;
        this.slots = slots;
    }

    public UUID getUser() {
        return user;
    }

    public void setUser(UUID user) {
        this.user = user;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public int getSlotDuration() {
        return slotDuration;
    }

    public void setSlotDuration(int slotDuration) {
        this.slotDuration = slotDuration;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }
}
