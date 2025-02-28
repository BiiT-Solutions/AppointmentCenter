package com.biit.appointment.core.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserAvailabilityDTO {

    private UUID user;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    public UserAvailabilityDTO() {
        super();
    }

    public UserAvailabilityDTO(UUID user, LocalDateTime startTime, LocalDateTime endTime) {
        this();
        this.user = user;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public UUID getUser() {
        return user;
    }

    public void setUser(UUID user) {
        this.user = user;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "UserAvailabilityDTO{"
                + "user=" + user
                + ", startTime=" + startTime
                + ", endTime=" + endTime
                + '}';
    }
}
