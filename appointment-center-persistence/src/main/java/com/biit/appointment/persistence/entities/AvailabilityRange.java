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

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "availability_ranges")
public class AvailabilityRange extends Element<Long> implements Comparable<AvailabilityRange> {
    private static final int HASH_KEY = 31;

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

    public AvailabilityRange() {
        super();
    }

    public AvailabilityRange(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
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
    public int compareTo(AvailabilityRange availabilityRange) {
        if (dayOfWeek != availabilityRange.dayOfWeek) {
            return dayOfWeek.compareTo(availabilityRange.dayOfWeek);
        }
        if (startTime != availabilityRange.startTime) {
            return startTime.compareTo(availabilityRange.startTime);
        }
        return endTime.compareTo(availabilityRange.endTime);
    }


    @Override
    public String toString() {
        return "AvailabilityRange{"
                + "dayOfWeek=" + dayOfWeek
                + ", startTime=" + startTime
                + ", endTime=" + endTime
                + '}';
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof AvailabilityRange that)) {
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

    public AvailabilityRange copy() {
        final AvailabilityRange availabilityRange = new AvailabilityRange();
        availabilityRange.setDayOfWeek(dayOfWeek);
        availabilityRange.setStartTime(startTime);
        availabilityRange.setEndTime(endTime);
        return availabilityRange;
    }
}
