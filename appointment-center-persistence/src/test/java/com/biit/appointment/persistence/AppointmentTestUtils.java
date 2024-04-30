package com.biit.appointment.persistence;

import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.ExaminationType;

import java.time.LocalDateTime;
import java.util.UUID;

public class AppointmentTestUtils {
    public static final long END_TIME_INCREMENT = 300;

    private static final int FORM_VERSION = 1;
    private static final long ORGANIZATION_ID = 456l;
    private static final String CATEGORY_NAME = "Biometrics";
    private static final String QUESTION_NAME = "Height";
    private static final String QUESTION_VALUE = "60";

    private static final double EXAMINATION_COST = 30;

    public static Appointment createAppointment(UUID organizer, long organizationId, LocalDateTime startAt, ExaminationType examinationType, UUID attendeeId) {
        return createAppointment(organizer, organizationId, startAt, startAt != null ? startAt.plusSeconds(END_TIME_INCREMENT) : null,
                examinationType, attendeeId);
    }

    public static Appointment createAppointment(UUID organizer, long organizationId, LocalDateTime startAt, LocalDateTime endsAt,
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
