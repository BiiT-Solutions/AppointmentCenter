package com.biit.appointment.rest.client;

import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.models.IAppointmentCenterRestClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Primary
@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
@Qualifier("appointmentCenterRestClient")
public class TestAppointmentCenterClient implements IAppointmentCenterRestClient {
    private static final int STARTED_TIME_PASSED = 45;
    private static final int APPOINTMENT_DURATION = 120;

    private int statedTimePassed = STARTED_TIME_PASSED;
    private int appointmentDuration = APPOINTMENT_DURATION;

    public Optional<AppointmentDTO> findByAttendeeAndTemplateCurrent(UUID userUUID, Long appointmentTemplateId) {
        final AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setAttendees(Collections.singleton(userUUID));
        appointmentDTO.setAppointmentTemplateId(appointmentTemplateId);
        appointmentDTO.setStartTime(LocalDateTime.now().minusMinutes(statedTimePassed));
        appointmentDTO.setEndTime(LocalDateTime.now().plusMinutes(appointmentDuration - statedTimePassed));

        return Optional.of(appointmentDTO);
    }

    public Optional<AppointmentDTO> findByAttendeeAndTemplateCurrent(UUID userUUID, String appointmentTemplateName) {
        final AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setAttendees(Collections.singleton(userUUID));
        appointmentDTO.setAppointmentTemplateId(1L);
        appointmentDTO.setStartTime(LocalDateTime.now().minusMinutes(statedTimePassed));
        appointmentDTO.setEndTime(LocalDateTime.now().plusMinutes(appointmentDuration - statedTimePassed));

        return Optional.of(appointmentDTO);
    }

    public int getStatedTimePassed() {
        return statedTimePassed;
    }

    public void setStatedTimePassed(int statedTimePassed) {
        this.statedTimePassed = statedTimePassed;
    }

    public int getAppointmentDuration() {
        return appointmentDuration;
    }

    public void setAppointmentDuration(int appointmentDuration) {
        this.appointmentDuration = appointmentDuration;
    }
}
