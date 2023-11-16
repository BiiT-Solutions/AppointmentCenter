package com.biit.appointment.core.controllers.kafka;


import com.biit.appointment.core.controllers.kafka.payloads.AppointmentPayload;
import com.biit.appointment.core.models.AppointmentDTO;
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
public class AppointmentEventSender implements IEventSender<AppointmentDTO> {

    private static final String APPOINTMENT_EVENT_TYPE = "appointment";

    @Value("${spring.kafka.send.topic:}")
    private String sendTopic;

    @Value("${spring.application.name:#{null}}")
    private String applicationName;

    private final KafkaEventTemplate kafkaTemplate;


    public AppointmentEventSender(KafkaEventTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvents(AppointmentDTO appointment, EventSubject subject, String executedBy) {
        EventsLogger.debug(this.getClass().getName(), "Preparing for sending events...");
        if (sendTopic != null && !sendTopic.isEmpty()) {
            //Send the complete form as an event.
            kafkaTemplate.send(sendTopic, getEvent(appointment, subject, executedBy));
            EventsLogger.debug(this.getClass().getName(), "Event '{}' with appointment '{}' send by '{}'!", subject, appointment, executedBy);
        } else {
            EventsLogger.warning(this.getClass().getName(), "Send topic not defined!");
        }
    }

    public Event getEvent(AppointmentDTO appointment, EventSubject subject, String createdBy) {
        final Event event = new Event(new AppointmentPayload(appointment));
        event.setCreatedBy(createdBy);
        event.setMessageId(UUID.randomUUID());
        if (subject != null) {
            event.setSubject(subject.toString());
        }
        event.setContentType(MediaType.APPLICATION_JSON_VALUE);
        event.setCreatedAt(LocalDateTime.now());
        event.setReplyTo(applicationName);
        event.setTag("Appointment");
        event.setCustomProperty(EventCustomProperties.FACT_TYPE, APPOINTMENT_EVENT_TYPE);
        return event;
    }
}
