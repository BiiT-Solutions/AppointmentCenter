package com.biit.appointment.core.models;

import com.biit.server.controllers.models.ElementDTO;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public class ScheduleRangeExclusionDTO extends ElementDTO<Long> {

    @Serial
    private static final long serialVersionUID = 4605314883795662858L;

    private Long id;

    @NotNull
    private UUID user;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    public ScheduleRangeExclusionDTO() {
        super();
    }

    public ScheduleRangeExclusionDTO(UUID user, LocalDate dayOff) {
        this(user, LocalDateTime.of(dayOff, LocalTime.MIN), LocalDateTime.of(dayOff, LocalTime.MAX));
    }

    public ScheduleRangeExclusionDTO(UUID user, LocalDate startTime, LocalDate endTime) {
        this(user, LocalDateTime.of(startTime, LocalTime.MIN), LocalDateTime.of(endTime, LocalTime.MAX));
    }

    public ScheduleRangeExclusionDTO(UUID user, LocalDateTime startTime, LocalDateTime endTime) {
        this();
        setUser(user);
        setStartTime(startTime);
        setEndTime(endTime);
    }

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
}
