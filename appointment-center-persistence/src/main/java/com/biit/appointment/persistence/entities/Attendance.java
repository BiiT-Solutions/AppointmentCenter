package com.biit.appointment.persistence.entities;

/*-
 * #%L
 * AppointmentCenter (Persistence)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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

import java.io.Serial;
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

    @Serial
    private static final long serialVersionUID = -8435901561876737621L;

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
