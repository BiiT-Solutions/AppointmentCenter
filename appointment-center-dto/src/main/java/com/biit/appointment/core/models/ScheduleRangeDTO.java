package com.biit.appointment.core.models;

import com.biit.server.controllers.models.ElementDTO;

import java.io.Serial;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class ScheduleRangeDTO extends ElementDTO<Long> {

    @Serial
    private static final long serialVersionUID = -323630447006629107L;


    private Long id;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ScheduleRangeDTO() {
        super();
    }

    public ScheduleRangeDTO(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this();
        setDayOfWeek(dayOfWeek);
        setStartTime(startTime);
        setEndTime(endTime);
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
