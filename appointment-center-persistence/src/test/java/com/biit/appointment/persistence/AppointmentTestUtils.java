package com.biit.appointment.persistence;

import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.ExaminationType;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class AppointmentTestUtils {
    public static final long END_TIME_INCREMENT = 300;

    private static final int FORM_VERSION = 1;
    private static final long ORGANIZATION_ID = 456l;
    private static final String CATEGORY_NAME = "Biometrics";
    private static final String QUESTION_NAME = "Height";
    private static final String QUESTION_VALUE = "60";

    private static final double EXAMINATION_COST = 30;

    public static Appointment createAppointment(long organizerId, long organizationId, LocalDateTime startAt, ExaminationType examinationType, Long customerId) {
        return createAppointment(organizerId, organizationId, startAt, startAt != null ? startAt.plusSeconds(END_TIME_INCREMENT) : null,
                examinationType, customerId);
    }

    public static Appointment createAppointment(long organizerId, long organizationId, LocalDateTime startAt, LocalDateTime endsAt,
                                                ExaminationType examinationType, Long attendeeId) {
        Appointment appointment = new Appointment();

        appointment.setOrganizerId(organizerId);
        appointment.setOrganizationId(organizationId);
        appointment.setStartTime(startAt);
        appointment.setEndTime(endsAt);
        appointment.setExaminationType(examinationType);
        appointment.addAttendee(attendeeId);
        return appointment;
    }
}
