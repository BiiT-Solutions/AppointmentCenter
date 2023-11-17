package com.biit.appointment.core.controllers.kafka;

import com.biit.kafka.consumers.EventListener;
import com.biit.kafka.events.Event;
import com.biit.kafka.logger.EventsLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;


@Controller
public class EventController {
    private static final String ORGANIZATION = "organization";
    private static final String PROCESS = "process";


    public EventController(@Autowired(required = false) EventListener eventListener) {

        //Listen to topic
        if (eventListener != null) {
            eventListener.addListener((event, offset, key, partition, topic, timeStamp) ->
                    eventHandler(event, key, partition, topic, timeStamp));
        }
    }

    public void eventHandler(Event event, String key, int partition, String topic, long timeStamp) {
        EventsLogger.debug(this.getClass(), "Received event '{}' on topic '{}', key '{}', partition '{}' at '{}'",
                event, topic, key, partition, LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp),
                        TimeZone.getDefault().toZoneId()));

//        final String createdBy = event.getCustomProperties().get(EventCustomProperties.ISSUER.getTag()) != null
//                ? event.getCustomProperties().get(EventCustomProperties.ISSUER.getTag())
//                : event.getCreatedBy();
    }
}
