package com.biit.appointment.core.controllers.kafka;


import com.biit.appointment.core.models.RecurrenceDTO;
import com.biit.kafka.events.EventSender;
import com.biit.kafka.events.KafkaEventTemplate;
import org.springframework.stereotype.Component;

@Component
public class RecurrenceEventSender extends EventSender<RecurrenceDTO> {

    private static final String RECURRENCE_EVENT_TYPE = "recurrence";

    public RecurrenceEventSender(KafkaEventTemplate kafkaTemplate) {
        super(kafkaTemplate, RECURRENCE_EVENT_TYPE);
    }


}
