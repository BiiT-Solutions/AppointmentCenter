package com.biit.appointment.persistence.entities;

import com.biit.database.encryption.BooleanCryptoConverter;
import com.biit.database.encryption.DoubleCryptoConverter;
import com.biit.database.encryption.LocalDateTimeCryptoConverter;
import com.biit.database.encryption.StringCryptoConverter;
import com.biit.server.persistence.entities.Element;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "appointments")
public class Appointment extends Element<Long> implements Comparable<Appointment> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    @Convert(converter = StringCryptoConverter.class)
    private String title;

    @Lob
    @Column(name = "description")
    @Convert(converter = StringCryptoConverter.class)
    private String description;

    @Column(name = "address")
    @Convert(converter = StringCryptoConverter.class)
    private String address;

    // who must resolve the appointment (can be null for any organizer).
    @Column(name = "organizer")
    private UUID organizer;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "organization_id")
    private String organizationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "examination_type")
    private ExaminationType examinationType;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "appointment_speakers")
    @Fetch(value = FetchMode.SUBSELECT)
    @Column(name = "speaker_id")
    private Set<UUID> speakers;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "appointment_attendees")
    @Fetch(value = FetchMode.SUBSELECT)
    @Column(name = "attendee_id")
    private Set<UUID> attendees;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "appointment_attendance", joinColumns = @JoinColumn(name = "appointment_id"), inverseJoinColumns = @JoinColumn(name = "attendance_id"))
    private Set<Attendance> attendances;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "appointment_status")
    private AppointmentStatus status = AppointmentStatus.NOT_STARTED;

    @Convert(converter = DoubleCryptoConverter.class)
    @Column(name = "cost")
    private Double cost;

    @Column(nullable = false, name = "is_deleted")
    private boolean deleted = false;

    @Column(name = "finished_time")
    @Convert(converter = LocalDateTimeCryptoConverter.class)
    private LocalDateTime finishedTime = null;

    @OneToMany(mappedBy = "appointment", orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Collection<CustomProperty> customProperties;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recurrence")
    private Recurrence recurrence;

    @Column(name = "all_day", nullable = false)
    @Convert(converter = BooleanCryptoConverter.class)
    private boolean allDay = false;

    //Is a code from the UX to store the color theme for the template.
    @Convert(converter = StringCryptoConverter.class)
    @Column(name = "color_theme")
    private String colorTheme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_template")
    private AppointmentTemplate appointmentTemplate;

    @Convert(converter = StringCryptoConverter.class)
    @Column(name = "infographic_template")
    private String infographicTemplate;

    public Appointment() {
        super();
        customProperties = new ArrayList<>();
        attendances = new HashSet<>();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public UUID getOrganizer() {
        return organizer;
    }

    public void setOrganizer(UUID organizer) {
        this.organizer = organizer;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public ExaminationType getExaminationType() {
        return examinationType;
    }

    public void setExaminationType(ExaminationType examinationType) {
        this.examinationType = examinationType;
    }

    public AppointmentTemplate getAppointmentTemplate() {
        return appointmentTemplate;
    }

    public void setAppointmentTemplate(AppointmentTemplate appointmentTemplate) {
        this.appointmentTemplate = appointmentTemplate;
    }

    public boolean isStarted() {
        return status.isStatusPassed(AppointmentStatus.STARTED);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Appointment{"
                + "id=" + id
                + ", organizer=" + organizer
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
        return organizer == null;

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

    public Set<UUID> getAttendees() {
        return attendees;
    }

    public void setAttendees(Set<UUID> attendees) {
        this.attendees = attendees;
    }

    public void addAttendee(UUID attendee) {
        if (this.attendees == null) {
            this.attendees = new HashSet<>();
        }
        this.attendees.add(attendee);
    }

    public Set<UUID> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(Set<UUID> speakers) {
        this.speakers = speakers;
    }

    public void addSpeaker(UUID speaker) {
        if (this.speakers == null) {
            this.speakers = new HashSet<>();
        }
        this.speakers.add(speaker);
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

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public Set<Attendance> getAttendances() {
        return attendances;
    }

    public void setAttendancesByAttendee(Set<UUID> attendees) {
        if (this.attendances == null) {
            this.attendances = new HashSet<>();
        }
        this.attendances.clear();
        attendees.forEach(attendee ->
                this.attendances.add(new Attendance(attendee, this)));
    }

    public void setAttendances(Set<Attendance> attendances) {
        if (this.attendances == null) {
            this.attendances = new HashSet<>();
        }
        this.attendances.clear();
        this.attendances.addAll(attendances);
    }

    public String getInfographicTemplate() {
        return infographicTemplate;
    }

    public void setInfographicTemplate(String infographicTemplate) {
        this.infographicTemplate = infographicTemplate;
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

        appointment.setId(null);
        appointment.setTitle(sourceAppointment.getTitle());
        appointment.setDescription(sourceAppointment.getDescription());
        appointment.setCost(sourceAppointment.getCost());
        appointment.setStatus(sourceAppointment.getStatus());
        appointment.setOrganizer(sourceAppointment.getOrganizer());
        appointment.setStartTime(sourceAppointment.getStartTime());
        appointment.setEndTime(sourceAppointment.getEndTime());
        appointment.setOrganizationId(sourceAppointment.getOrganizationId());
        appointment.setDeleted(sourceAppointment.isDeleted());
        appointment.setFinishedTime(sourceAppointment.getFinishedTime());
        appointment.setAllDay(sourceAppointment.isAllDay());
        appointment.setColorTheme(sourceAppointment.getColorTheme());
        appointment.setRecurrence(sourceAppointment.getRecurrence());

        appointment.setExaminationType(sourceAppointment.getExaminationType());
        if (sourceAppointment.getAttendees() != null) {
            appointment.setAttendees(new HashSet<>(sourceAppointment.getAttendees()));
        }
        if (sourceAppointment.getSpeakers() != null) {
            appointment.setSpeakers(new HashSet<>(sourceAppointment.getSpeakers()));
        }

        final List<CustomProperty> customProperties = appointment.getCustomProperties().stream().map(CustomProperty::copy).collect(Collectors.toList());
        customProperties.forEach(customProperty -> customProperty.setAppointment(appointment));
        appointment.setCustomProperties(customProperties);
        final Set<Attendance> attendances = appointment.getAttendances().stream().map(Attendance::copy).collect(Collectors.toSet());
        attendances.forEach(attendance -> attendance.setAppointment(appointment));
        appointment.setAttendances(attendances);
        return appointment;
    }


    public static Appointment of(Appointment sourceAppointment, LocalDateTime onDate) {
        final Appointment appointment = Appointment.copy(sourceAppointment);
        final LocalDateTime appointmentStartTime = appointment.getStartTime();
        if (onDate != null) {
            appointment.setStartTime(onDate.toLocalDate().atTime(sourceAppointment.getStartTime().toLocalTime()));
            appointment.setEndTime(appointment.getStartTime().plus(Duration.between(appointmentStartTime, sourceAppointment.getEndTime())));
        }
        return appointment;
    }

    public String getColorTheme() {
        return colorTheme;
    }

    public void setColorTheme(String colorTheme) {
        this.colorTheme = colorTheme;
    }
}
