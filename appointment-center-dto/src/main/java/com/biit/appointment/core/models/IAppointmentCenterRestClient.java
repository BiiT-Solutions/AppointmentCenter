package com.biit.appointment.core.models;

import java.util.Optional;
import java.util.UUID;

public interface IAppointmentCenterRestClient {

    Optional<AppointmentDTO> findByAttendeeAndTemplateCurrent(UUID userUUID, Long appointmentTemplateId);

    Optional<AppointmentDTO> findByAttendeeAndTemplateCurrent(UUID userUUID, String appointmentTemplateName);
}
