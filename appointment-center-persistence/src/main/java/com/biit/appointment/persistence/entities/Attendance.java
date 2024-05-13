package com.biit.appointment.persistence.entities;

import com.biit.server.persistence.entities.Element;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.UUID;


@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "attendance", uniqueConstraints = {@UniqueConstraint(columnNames = {"attendee", "appointment"})},
        indexes = {
                @Index(name = "ind_attendee", columnList = "attendee"),
                @Index(name = "ind_appointment", columnList = "appointment")
        })
public class Attendance extends Element<Long> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attendee", nullable = false)
    private UUID attendee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "appointment")
    private Appointment appointment;

    public Attendance() {
        super();
    }


    public Attendance(UUID attendee, Appointment appointment) {
        this();
        setAttendee(attendee);
        setAppointment(appointment);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getAttendee() {
        return attendee;
    }

    public void setAttendee(UUID attendee) {
        this.attendee = attendee;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public static Attendance copy(Attendance source) {
        final Attendance attendance = new Attendance();
        BeanUtils.copyProperties(source, attendance);
        attendance.setAttendee(source.getAttendee());
        attendance.setId(null);
        attendance.setAppointment(source.getAppointment());
        return attendance;
    }
}
