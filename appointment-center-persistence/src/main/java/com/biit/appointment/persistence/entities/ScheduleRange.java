package com.biit.appointment.persistence.entities;

import com.biit.server.persistence.entities.Element;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serial;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "schedule_ranges")
public class ScheduleRange extends Element<Long> implements Comparable<ScheduleRange> {
    private static final int HASH_KEY = 31;

    @Serial
    private static final long serialVersionUID = -1642402680251221768L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ScheduleRange() {
        super();
    }

    public ScheduleRange(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
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


    @Override
    public int compareTo(ScheduleRange scheduleRange) {
        if (dayOfWeek != scheduleRange.dayOfWeek) {
            return dayOfWeek.compareTo(scheduleRange.dayOfWeek);
        }
        if (startTime != scheduleRange.startTime) {
            return startTime.compareTo(scheduleRange.startTime);
        }
        return endTime.compareTo(scheduleRange.endTime);
    }


    @Override
    public String toString() {
        return "ScheduleRange{"
                + "dayOfWeek=" + dayOfWeek
                + ", startTime=" + startTime
                + ", endTime=" + endTime
                + '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof ScheduleRange that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return dayOfWeek == that.dayOfWeek && startTime.equals(that.startTime) && endTime.equals(that.endTime);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = HASH_KEY * result + dayOfWeek.hashCode();
        result = HASH_KEY * result + startTime.hashCode();
        result = HASH_KEY * result + endTime.hashCode();
        return result;
    }

    public ScheduleRange copy() {
        final ScheduleRange scheduleRange = new ScheduleRange();
        scheduleRange.setDayOfWeek(dayOfWeek);
        scheduleRange.setStartTime(startTime);
        scheduleRange.setEndTime(endTime);
        return scheduleRange;
    }
}
