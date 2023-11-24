package com.biit.appointment.core.controllers.kafka;


import com.biit.appointment.core.models.RecurrenceDTO;
import com.biit.appointment.core.utils.EventUtils;
import com.biit.kafka.events.EventSender;
import com.biit.kafka.events.KafkaEventTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecurrenceEventSender extends EventSender<RecurrenceDTO> {

    private static final String RECURRENCE_EVENT_TYPE = "recurrence";

    public RecurrenceEventSender(@Autowired(required = false) KafkaEventTemplate kafkaTemplate) {
        super(kafkaTemplate, EventUtils.TAG, RECURRENCE_EVENT_TYPE);
    }


}
