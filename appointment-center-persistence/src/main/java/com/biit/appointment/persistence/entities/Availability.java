package com.biit.appointment.persistence.entities;


import com.biit.appointment.persistence.exceptions.InvalidAvailabilityException;
import com.biit.server.persistence.entities.Element;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "availability")
public class Availability extends Element<Long> {

    @Serial
    private static final long serialVersionUID = 3461399669106878590L;

    @Id
    private Long id;

    @Column(name = "user", nullable = false, unique = true)
    private UUID user;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "ranges", joinColumns = @JoinColumn(name = "range_id"), inverseJoinColumns = @JoinColumn(name = "availability_id"))
    @OrderColumn(name = "range_index")
    private List<AvailabilityRange> ranges;

    public Availability() {
        ranges = new ArrayList<>();
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

    public List<AvailabilityRange> getRanges() {
        if (this.ranges == null) {
            return new ArrayList<>();
        }
        return ranges;
    }


    public void addRange(AvailabilityRange range) {
        if (range == null) {
            return;
        }
        if (ranges == null) {
            ranges = new ArrayList<>();
        }
        ranges.add(range);
        this.ranges.sort(AvailabilityRange::compareTo);
        simplifyRanges();
    }


    public void setRanges(List<AvailabilityRange> ranges) {
        if (this.ranges != null) {
            this.ranges.clear();
            this.ranges.addAll(ranges);
        } else {
            this.ranges = ranges;
        }
        this.ranges.sort(AvailabilityRange::compareTo);
        simplifyRanges();
    }


    public void simplifyRanges() {
        if (this.ranges.size() < 2) {
            return;
        }

        final List<AvailabilityRange> finalRanges = new ArrayList<>();
        final Iterator<AvailabilityRange> comparing = this.ranges.iterator();
        AvailabilityRange compare1 = comparing.next();
        while (comparing.hasNext()) {
            final List<AvailabilityRange> simplifiedRanges = simplify(compare1, comparing.next());
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


    public void removeRange(AvailabilityRange range) {
        if (range == null) {
            return;
        }
        if (ranges == null) {
            ranges = new ArrayList<>();
        }
        //Remove any time range that overlaps with the desired one.
        for (AvailabilityRange availabilityRange : new ArrayList<>(ranges)) {
            final AvailabilityRange originalRange = availabilityRange.copy();
            if (Objects.equals(availabilityRange, range)) {
                ranges.remove(availabilityRange);
                break;
            }
            if (availabilityRange.getDayOfWeek().compareTo(range.getDayOfWeek()) == 0) {
                if (availabilityRange.getStartTime().isBefore(range.getStartTime())
                        && availabilityRange.getEndTime().isAfter(range.getStartTime())) {
                    //  availability time        /----------/
                    //  removed time                    /------/
                    if (availabilityRange.getEndTime().isBefore(range.getEndTime())
                            || availabilityRange.getEndTime().equals(range.getEndTime())) {
                        availabilityRange.setEndTime(range.getStartTime());
                        //  result               /-----/
                        continue;
                    }
                    //  availability time        /--------------------/
                    //  removed time                    /------/
                    if (availabilityRange.getEndTime().isAfter(range.getEndTime())) {
                        availabilityRange.setEndTime(range.getStartTime());
                        //  result               /-----/       /------/
                        ranges.add(new AvailabilityRange(availabilityRange.getDayOfWeek(), range.getEndTime(), originalRange.getEndTime()));
                    }
                }
                if (availabilityRange.getStartTime().isAfter(range.getStartTime()) || availabilityRange.getStartTime().equals(range.getStartTime())
                        && availabilityRange.getStartTime().isBefore(range.getEndTime())) {
                    //  availability time                  /----------/
                    //  removed time                    /------/
                    if (availabilityRange.getEndTime().isAfter(range.getEndTime())
                            || availabilityRange.getEndTime().equals(range.getEndTime())) {
                        //  result                             /------/
                        availabilityRange.setStartTime(range.getEndTime());
                        continue;
                    }
                    //  availability time                  /---/
                    //  removed time                    /------/
                    if (availabilityRange.getEndTime().isBefore(range.getEndTime())
                            || availabilityRange.getEndTime().equals(range.getEndTime())) {
                        //  result                       <<empty>>
                        ranges.remove(availabilityRange);
                    }
                }
                //Out of boundaries, Do nothing.
            }
        }
    }


    private List<AvailabilityRange> simplify(AvailabilityRange range1, AvailabilityRange range2) {
        final List<AvailabilityRange> finalRanges = new ArrayList<>();
        if (range1 != null && range2 != null) {
            //On different days.
            if (range1.getDayOfWeek().compareTo(range2.getDayOfWeek()) == 0) {
                //Must be sorted!
                if (range1.getStartTime().isAfter(range2.getStartTime())) {
                    throw new InvalidAvailabilityException(this.getClass(), "Ranges must be sorted before simplifying!");
                }
                //Overlaps!
                if (Objects.equals(range1.getEndTime(), range2.getStartTime()) || range1.getEndTime().isAfter(range2.getStartTime())) {
                    finalRanges.add(new AvailabilityRange(range1.getDayOfWeek(), range1.getStartTime(), range2.getEndTime()));
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
        return finalRanges;
    }

    @Override
    public String toString() {
        return "Availability{"
                + "id=" + id
                + ", user=" + user
                + ", ranges=" + ranges
                + '}';
    }
}
