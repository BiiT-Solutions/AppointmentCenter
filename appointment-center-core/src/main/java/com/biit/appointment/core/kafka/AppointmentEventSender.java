package com.biit.appointment.core.kafka;

import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.kafka.events.EventSender;
import com.biit.kafka.events.KafkaEventTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentEventSender extends EventSender<AppointmentDTO> {

    private static final String EVENT_TYPE = "CREATED";


    public AppointmentEventSender(@Autowired(required = false) KafkaEventTemplate kafkaTemplate) {
        super(kafkaTemplate, EVENT_TYPE, EventTags.APPOINTMENT.name());
    }
}
