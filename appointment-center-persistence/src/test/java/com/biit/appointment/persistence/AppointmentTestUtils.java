package com.biit.appointment.persistence;

import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.ExaminationType;

import java.time.LocalDateTime;
import java.util.UUID;

public class AppointmentTestUtils {
    public static final long END_TIME_INCREMENT = 300;

    public static Appointment createAppointment(UUID organizer, String organizationId, LocalDateTime startAt, ExaminationType examinationType, UUID attendeeId) {
        return createAppointment(organizer, organizationId, startAt, startAt != null ? startAt.plusSeconds(END_TIME_INCREMENT) : null,
                examinationType, attendeeId);
    }

    public static Appointment createAppointment(UUID organizer, String organizationId, LocalDateTime startAt, LocalDateTime endsAt,
                                                ExaminationType examinationType, UUID attendeeId) {
        Appointment appointment = new Appointment();

        appointment.setOrganizer(organizer);
        appointment.setOrganizationId(organizationId);
        appointment.setStartTime(startAt);
        appointment.setEndTime(endsAt);
        appointment.setExaminationType(examinationType);
        appointment.addAttendee(attendeeId);
        return appointment;
    }
}
