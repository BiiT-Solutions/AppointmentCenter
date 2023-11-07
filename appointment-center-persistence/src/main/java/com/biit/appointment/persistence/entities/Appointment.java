package com.biit.appointment.persistence.entities;

import com.biit.server.persistence.entities.Element;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.BeanUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "appointments")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Appointment extends Element<Long> implements Comparable<Appointment> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organizer_id", nullable = false)
    private Long organizerId;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "organization_id", nullable = true)
    private Long organizationId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "examination_type")
    private ExaminationType examinationType;

    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "attendee_id", nullable = false)
    private Set<Long> attendees;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.NOT_STARTED;

    private Double cost;

    private boolean deleted = false;

    @Column(name = "finished_time")
    private LocalDateTime finishedTime = null;

    @OneToMany(mappedBy = "appointment", orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Collection<CustomProperty> customProperties;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recurrence")
    private Recurrence recurrence;

    public Appointment() {
        super();
        customProperties = new ArrayList<>();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long physiotherapistId) {
        this.organizerId = physiotherapistId;
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

    public boolean isStarted() {
        return status.isStatusPassed(AppointmentStatus.STARTED);
    }

    @Override
    public String toString() {
        return "Appointment{"
                + "id=" + id
                + ", organizerId=" + organizerId
                + ", startTime=" + startTime
                + ", endTime=" + endTime
                + ", organizationId=" + organizationId
                + ", status=" + status
                + '}';
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isRejected() {
        return organizerId == null;

    }

    public void setFinishedTime() {
        if (finishedTime == null) {
            finishedTime = LocalDateTime.now();
        }
    }

    public Duration getDuration() {
        if (finishedTime == null) {
            return null;
        }
        return Duration.between(startTime, finishedTime);
    }

    public Set<Long> getAttendees() {
        return attendees;
    }

    public void setAttendees(Set<Long> attendees) {
        this.attendees = attendees;
    }

    public void addAttendee(Long attendee) {
        if (this.attendees == null) {
            this.attendees = new HashSet<>();
        }
        this.attendees.add(attendee);
    }

    public Recurrence getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(Recurrence recurrence) {
        this.recurrence = recurrence;
    }

    public LocalDateTime getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(LocalDateTime finishedTime) {
        this.finishedTime = finishedTime;
    }

    public Collection<CustomProperty> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Collection<CustomProperty> customProperties) {
        this.customProperties = customProperties;
    }

    @Override
    public int compareTo(Appointment appointment) {
        // This comparator is used in the order for FMS charts and BMI.
        return getStartTime().compareTo(appointment.getStartTime());
    }

    public boolean isUpdated() {
        if (getUpdatedAt() == null) {
            return false;
        }
        return getUpdatedAt().truncatedTo(ChronoUnit.SECONDS).isAfter(getCreatedAt().truncatedTo(ChronoUnit.SECONDS));
    }

    public static Appointment copy(Appointment sourceAppointment) {
        final Appointment appointment = new Appointment();
        BeanUtils.copyProperties(sourceAppointment, appointment);
        appointment.setExaminationType(sourceAppointment.getExaminationType());
        appointment.setAttendees(new HashSet<>(sourceAppointment.getAttendees()));
        appointment.setStatus(sourceAppointment.getStatus());
        appointment.setId(null);
        final List<CustomProperty> customProperties = appointment.getCustomProperties().stream().map(CustomProperty::copy).collect(Collectors.toList());
        customProperties.forEach(customProperty -> customProperty.setAppointment(appointment));
        appointment.setCustomProperties(customProperties);
        return appointment;
    }

    public static Appointment of(Appointment sourceAppointment, LocalDateTime onDate) {
        final Appointment appointment = Appointment.copy(sourceAppointment);
        final LocalDateTime appointmentStartTime = appointment.getStartTime();
        appointment.setStartTime(onDate.toLocalDate().atTime(sourceAppointment.getStartTime().toLocalTime()));
        appointment.setEndTime(appointment.getStartTime().plus(Duration.between(appointmentStartTime, sourceAppointment.getEndTime())));
        return appointment;
    }

}
