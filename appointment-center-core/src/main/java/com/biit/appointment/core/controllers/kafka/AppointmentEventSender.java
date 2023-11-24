package com.biit.appointment.core.controllers.kafka;


import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.utils.EventUtils;
import com.biit.kafka.events.EventSender;
import com.biit.kafka.events.KafkaEventTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentEventSender extends EventSender<AppointmentDTO> {

    private static final String APPOINTMENT_EVENT_TYPE = "appointment";

    public AppointmentEventSender(@Autowired(required = false) KafkaEventTemplate kafkaTemplate) {
        super(kafkaTemplate, EventUtils.TAG, APPOINTMENT_EVENT_TYPE);
    }
}
