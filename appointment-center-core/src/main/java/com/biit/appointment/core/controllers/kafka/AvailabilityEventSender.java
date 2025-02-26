package com.biit.appointment.core.controllers.kafka;


import com.biit.appointment.core.models.AvailabilityDTO;
import com.biit.appointment.core.utils.EventUtils;
import com.biit.kafka.events.EventSender;
import com.biit.kafka.events.KafkaEventTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AvailabilityEventSender extends EventSender<AvailabilityDTO> {

    private static final String RECURRENCE_EVENT_TYPE = "availability";

    public AvailabilityEventSender(@Autowired(required = false) KafkaEventTemplate kafkaTemplate) {
        super(kafkaTemplate, EventUtils.TAG, RECURRENCE_EVENT_TYPE);
    }


}
