package com.biit.appointment.rest.api.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class AvailabilitySearch {
    private static final int MAX_SLOTS = 20;
    private static final int MAX_SLOT_DURATION = 24 * 60;

    @NotNull
    private UUID user;

    private LocalDateTime start;

    private LocalDateTime end;

    //In minutes.
    @NotNull
    @Min(1)
    @Max(MAX_SLOT_DURATION)
    private int slotDuration;

    @NotNull
    @Min(1)
    @Max(MAX_SLOTS)
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
