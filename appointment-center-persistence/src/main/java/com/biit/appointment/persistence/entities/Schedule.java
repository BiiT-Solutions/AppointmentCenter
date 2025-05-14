package com.biit.appointment.persistence.entities;


import com.biit.appointment.persistence.exceptions.InvalidScheduleException;
import com.biit.server.persistence.entities.Element;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serial;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "schedule")
public class Schedule extends Element<Long> {

    private static final LocalTime DEFAULT_START_TIME = LocalTime.of(9, 0);
    private static final LocalTime DEFAULT_END_TIME = LocalTime.of(18, 0);

    @Serial
    private static final long serialVersionUID = 3461399669106878590L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_uuid", nullable = false, unique = true)
    private UUID user;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "ranges", joinColumns = @JoinColumn(name = "range_id"), inverseJoinColumns = @JoinColumn(name = "schedule_id"))
    @OrderColumn(name = "range_index")
    private List<ScheduleRange> ranges;

    public Schedule() {
        ranges = new ArrayList<>();
    }

    public Schedule(UUID user) {
        this();
        setUser(user);
    }

    public Schedule(UUID user, List<ScheduleRange> scheduleRanges) {
        this();
        setUser(user);
        setRanges(scheduleRanges);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public UUID getUser() {
        return user;
    }

    public void setUser(UUID user) {
        this.user = user;
    }

    public List<ScheduleRange> getRanges() {
        if (this.ranges == null) {
            return new ArrayList<>();
        }
        return ranges;
    }


    public void addRange(ScheduleRange range) {
        if (range == null) {
            return;
        }
        if (ranges == null) {
            ranges = new ArrayList<>();
        }
        ranges.add(range);
        simplifyRanges();
    }


    public void updateRange(ScheduleRange range) {
        final List<ScheduleRange> oldRanges = new ArrayList<>(this.ranges);
        this.ranges.clear();
        this.ranges.addAll(oldRanges.stream().filter(s -> !Objects.equals(s.getId(), range.getId())).toList());
        ranges.add(range);
        simplifyRanges();
    }


    public void setRanges(List<ScheduleRange> ranges) {
        if (this.ranges != null) {
            this.ranges.clear();
            this.ranges.addAll(ranges);
        } else {
            this.ranges = ranges;
        }
        simplifyRanges();
    }


    public void simplifyRanges() {
        if (this.ranges.size() < 2) {
            return;
        }

        ranges.sort(ScheduleRange::compareTo);

        final List<ScheduleRange> finalRanges = new ArrayList<>();
        final Iterator<ScheduleRange> comparing = this.ranges.iterator();
        ScheduleRange compare1 = comparing.next();
        while (comparing.hasNext()) {
            final List<ScheduleRange> simplifiedRanges = simplify(compare1, comparing.next());
            //Has been simplified?
            if (simplifiedRanges.size() == 1) {
                //Use simplified one.
                compare1 = simplifiedRanges.get(0);
                //Only store if no more comparisons are left, as can be simplified again.
                if (!comparing.hasNext()) {
                    finalRanges.add(compare1);
                }
            } else {
                //Store the first one.
                finalRanges.add(simplifiedRanges.get(0));
                //Add the last one if no comparison is left.
                if (!comparing.hasNext()) {
                    finalRanges.add(simplifiedRanges.get(1));
                }
                //Use the last one for the next comparison.
                compare1 = simplifiedRanges.get(1);
            }
        }
        this.ranges.clear();
        this.ranges.addAll(finalRanges);
    }


    public void removeRange(ScheduleRange range) {
        if (range == null) {
            return;
        }
        if (ranges == null) {
            ranges = new ArrayList<>();
        }
        //Remove any time range that overlaps with the desired one.
        for (ScheduleRange scheduleRange : new ArrayList<>(ranges)) {
            if (Objects.equals(range.getId(), scheduleRange.getId())) {
                ranges.remove(scheduleRange);
                break;
            }
            final ScheduleRange originalRange = scheduleRange.copy();
            if (Objects.equals(scheduleRange, range)) {
                ranges.remove(scheduleRange);
                break;
            }
            if (scheduleRange.getDayOfWeek().compareTo(range.getDayOfWeek()) == 0) {
                if (scheduleRange.getStartTime().isBefore(range.getStartTime())
                        && scheduleRange.getEndTime().isAfter(range.getStartTime())) {
                    //  schedule time        /----------/
                    //  removed time                    /------/
                    if (scheduleRange.getEndTime().isBefore(range.getEndTime())
                            || scheduleRange.getEndTime().equals(range.getEndTime())) {
                        scheduleRange.setEndTime(range.getStartTime());
                        //  result               /-----/
                        continue;
                    }
                    //  schedule time        /--------------------/
                    //  removed time                    /------/
                    if (scheduleRange.getEndTime().isAfter(range.getEndTime())) {
                        scheduleRange.setEndTime(range.getStartTime());
                        //  result               /-----/       /------/
                        ranges.add(new ScheduleRange(scheduleRange.getDayOfWeek(), range.getEndTime(), originalRange.getEndTime()));
                    }
                }
                if ((scheduleRange.getStartTime().isAfter(range.getStartTime()) || scheduleRange.getStartTime().equals(range.getStartTime()))
                        && scheduleRange.getStartTime().isBefore(range.getEndTime())) {
                    //  schedule time                  /----------/
                    //  removed time                    /------/
                    if (scheduleRange.getEndTime().isAfter(range.getEndTime())
                            || scheduleRange.getEndTime().equals(range.getEndTime())) {
                        //  result                             /------/
                        scheduleRange.setStartTime(range.getEndTime());
                        continue;
                    }
                    //  schedule time                  /---/
                    //  removed time                    /------/
                    if (scheduleRange.getEndTime().isBefore(range.getEndTime())
                            || scheduleRange.getEndTime().equals(range.getEndTime())) {
                        //  result                       <<empty>>
                        ranges.remove(scheduleRange);
                    }
                }
                //Out of boundaries, Do nothing.
            }
        }
        removeNoDurationRanges();
    }


    private List<ScheduleRange> simplify(ScheduleRange range1, ScheduleRange range2) {
        final List<ScheduleRange> finalRanges = new ArrayList<>();
        if (range1 != null && range2 != null) {
            //On same day.
            if (range1.getDayOfWeek().compareTo(range2.getDayOfWeek()) == 0) {
                //Must be sorted!
                if (range1.getStartTime().isAfter(range2.getStartTime())) {
                    throw new InvalidScheduleException(this.getClass(), "Ranges must be sorted before simplifying!");
                }
                //Overlaps!
                if (Objects.equals(range1.getEndTime(), range2.getStartTime()) || range1.getEndTime().isAfter(range2.getStartTime())) {
                    finalRanges.add(new ScheduleRange(range1.getDayOfWeek(), range1.getStartTime(),
                            //The latest one is used.
                            (range1.getEndTime().isAfter(range2.getEndTime())) ? range1.getEndTime() : range2.getEndTime()));
                    return finalRanges;
                }
            }
            finalRanges.add(range1);
            finalRanges.add(range2);
            return finalRanges;
        } else if (range1 != null) {
            finalRanges.add(range1);
        } else {
            finalRanges.add(range2);
        }
        removeNoDurationRanges();
        return finalRanges;
    }

    private void removeNoDurationRanges() {
        ranges.removeIf(scheduleRange -> Objects.equals(scheduleRange.getStartTime(), scheduleRange.getEndTime()));
    }

    @Override
    public String toString() {
        return "Schedule{"
                + "id=" + id
                + ", user=" + user
                + ", ranges=" + ranges
                + '}';
    }

    public List<ScheduleRange> getRange(DayOfWeek dayOfWeek) {
        if (this.ranges != null && !this.ranges.isEmpty()) {
            return this.ranges.stream().filter(scheduleRange -> scheduleRange.getDayOfWeek().equals(dayOfWeek)).toList();
        }
        return List.of(new ScheduleRange(dayOfWeek, DEFAULT_START_TIME, DEFAULT_END_TIME));
    }
}
