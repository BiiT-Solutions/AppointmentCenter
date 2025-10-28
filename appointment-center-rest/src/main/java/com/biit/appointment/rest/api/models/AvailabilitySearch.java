package com.biit.appointment.rest.api.models;

/*-
 * #%L
 * AppointmentCenter (Rest)
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

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public class AvailabilitySearch {
    public static final int MAX_SLOTS = 20;
    public static final int MAX_SLOT_DURATION = 24 * 60;

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
