package com.biit.appointment.core.controllers.kafka;


import com.biit.appointment.core.controllers.kafka.payloads.RecurrencePayload;
import com.biit.appointment.core.models.RecurrenceDTO;
import com.biit.appointment.logger.EventsLogger;
import com.biit.kafka.events.Event;
import com.biit.kafka.events.EventCustomProperties;
import com.biit.kafka.events.EventSubject;
import com.biit.kafka.events.IEventSender;
import com.biit.kafka.events.KafkaEventTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class RecurrenceEventSender implements IEventSender<RecurrenceDTO> {

    private static final String RECURRENCE_EVENT_TYPE = "recurrence";

    @Value("${spring.kafka.send.topic:}")
    private String sendTopic;

    @Value("${spring.application.name:#{null}}")
    private String applicationName;

    private final KafkaEventTemplate kafkaTemplate;

    public RecurrenceEventSender(KafkaEventTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    @Override
    public void sendEvents(RecurrenceDTO recurrence, EventSubject subject, String executedBy) {
        EventsLogger.debug(this.getClass().getName(), "Preparing for sending events...");
        if (sendTopic != null && !sendTopic.isEmpty()) {
            //Send the complete form as an event.
            kafkaTemplate.send(sendTopic, getEvent(recurrence, subject, executedBy));
            EventsLogger.debug(this.getClass().getName(), "Event '{}' with recurrence '{}' send by '{}'!", subject, recurrence, executedBy);
        } else {
            EventsLogger.warning(this.getClass().getName(), "Send topic not defined!");
        }
    }

    private Event getEvent(RecurrenceDTO recurrence, EventSubject subject, String createdBy) {
        final Event event = new Event(new RecurrencePayload(recurrence));
        event.setCreatedBy(createdBy);
        event.setMessageId(UUID.randomUUID());
        if (subject != null) {
            event.setSubject(subject.toString());
        }
        event.setContentType(MediaType.APPLICATION_JSON_VALUE);
        event.setCreatedAt(LocalDateTime.now());
        event.setReplyTo(applicationName);
        event.setTag("Recurrence");
        event.setCustomProperty(EventCustomProperties.FACT_TYPE, RECURRENCE_EVENT_TYPE);
        return event;
    }
}
