package com.biit.appointment.persistence.entities;


import com.biit.server.persistence.entities.Element;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recurrence")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Recurrence extends Element<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organizer_id", nullable = false)
    private Long organizerId;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "examination_type")
    private ExaminationType examinationType;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "appointments_by_recurrence", joinColumns = @JoinColumn(name = "recurrence"), inverseJoinColumns = @JoinColumn(name = "appointment"))
    @OrderColumn(name = "appointment_index")
    private List<Appointment> appointments;

    @Column(name = "frequency", nullable = false)
    @Enumerated(EnumType.STRING)
    private RecurrenceFrequency frequency;

    @Column(name = "starts_at")
    private LocalDateTime startsAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public void addAppointment(Appointment appointment) {
        if (this.appointments == null) {
            appointments = new ArrayList<>();
            this.setExaminationType(appointment.getExaminationType());
        }
        appointments.add(appointment);
    }

    public RecurrenceFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(RecurrenceFrequency frequency) {
        this.frequency = frequency;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(LocalDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(LocalDateTime endsAt) {
        this.endsAt = endsAt;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public ExaminationType getExaminationType() {
        return examinationType;
    }

    public void setExaminationType(ExaminationType examinationType) {
        this.examinationType = examinationType;
    }

    /**
     * Returns an appointment if is already generated on the selected date.
     *
     * @param date
     * @return
     */
    public Appointment getAppointment(LocalDate date) {
        for (Appointment appointment : appointments) {
            if (appointment.getStartTime().toLocalDate().equals(date)) {
                return appointment;
            }
        }
        return null;
    }

    public List<LocalDate> getMatches(LocalDateTime lowerTimeBoundary, LocalDateTime upperTimeBoundary) {
        final List<LocalDate> matchingDates = new ArrayList<>();
        LocalDateTime checkingDate = lowerTimeBoundary;
        while (checkingDate.isBefore(upperTimeBoundary)) {
            if (hasMatch(checkingDate)) {
                matchingDates.add(checkingDate.toLocalDate());
            }
            checkingDate = checkingDate.plusDays(1);
        }
        return matchingDates;
    }

    public boolean hasMatch(LocalDateTime date) {
        if (getAppointments() == null || getAppointments().isEmpty() || date == null) {
            return false;
        }
        if (endsAt.toLocalDate().atTime(LocalTime.MAX).isBefore(date) || startsAt.isAfter(date)) {
            return false;
        }
        final LocalDateTime recurrenceStartDate = getAppointments().get(0).getStartTime();
        return getFrequency().hasRecurrence(recurrenceStartDate.toLocalDate(), date.toLocalDate());
    }
}
