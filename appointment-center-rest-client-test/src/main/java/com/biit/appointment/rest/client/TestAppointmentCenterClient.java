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

    private LocalDateTime startTime = LocalDateTime.now().minusMinutes(STARTED_TIME_PASSED);
    private LocalDateTime endTime = LocalDateTime.now().plusMinutes(APPOINTMENT_DURATION - STARTED_TIME_PASSED);

    public Optional<AppointmentDTO> findByAttendeeAndTemplateCurrent(UUID userUUID, Long appointmentTemplateId) {
        final AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setAttendees(Collections.singleton(userUUID));
        appointmentDTO.setAppointmentTemplateId(appointmentTemplateId);
        appointmentDTO.setStartTime(startTime);
        appointmentDTO.setEndTime(endTime);

        return Optional.of(appointmentDTO);
    }

    public Optional<AppointmentDTO> findByAttendeeAndTemplateCurrent(UUID userUUID, String appointmentTemplateName) {
        final AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setAttendees(Collections.singleton(userUUID));
        appointmentDTO.setAppointmentTemplateId(1L);
        appointmentDTO.setStartTime(startTime);
        appointmentDTO.setEndTime(endTime);

        return Optional.of(appointmentDTO);
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
}
