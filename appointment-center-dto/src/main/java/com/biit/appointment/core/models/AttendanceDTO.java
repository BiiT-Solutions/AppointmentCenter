package com.biit.appointment.core.models;

import com.biit.server.controllers.models.ElementDTO;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class AttendanceDTO extends ElementDTO<Long> {

    private Long id;

    @NotNull
    private UUID attendee;

    @NotNull
    private Long appointmentId;

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

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }
}
