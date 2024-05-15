package com.biit.appointment.rest.client;

import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.models.IAppointmentCenterRestClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
@Qualifier("appointmentCenterRestClient")
public class TestAppointmentCenterClient implements IAppointmentCenterRestClient {
    private static final int STARTED_TIME_PASSED = 45;
    private static final int APPOINTMENT_DURATION = 120;

    public Optional<AppointmentDTO> findByAttendeeAndTemplateCurrent(UUID userUUID, Long appointmentTemplateId) {
        final AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setAttendees(Collections.singleton(userUUID));
        appointmentDTO.setAppointmentTemplateId(appointmentTemplateId);
        appointmentDTO.setStartTime(LocalDateTime.now().minusMinutes(STARTED_TIME_PASSED));
        appointmentDTO.setEndTime(LocalDateTime.now().plusMinutes(APPOINTMENT_DURATION - STARTED_TIME_PASSED));

        return Optional.of(appointmentDTO);
    }

    public Optional<AppointmentDTO> findByAttendeeAndTemplateCurrent(UUID userUUID, String appointmentTemplateName) {
        final AppointmentDTO appointmentDTO = new AppointmentDTO();
        appointmentDTO.setAttendees(Collections.singleton(userUUID));
        appointmentDTO.setAppointmentTemplateId(1L);
        appointmentDTO.setStartTime(LocalDateTime.now().minusMinutes(STARTED_TIME_PASSED));
        appointmentDTO.setEndTime(LocalDateTime.now().plusMinutes(APPOINTMENT_DURATION - STARTED_TIME_PASSED));

        return Optional.of(appointmentDTO);
    }
}
