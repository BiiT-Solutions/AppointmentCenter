package com.biit.appointment.core.controllers.kafka;

import com.biit.kafka.consumers.EventListener;
import com.biit.kafka.events.Event;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

@EnableKafka
@Configuration
public class EventConsumerListener extends EventListener {

    @Override
    @KafkaListener(topics = "${spring.kafka.topic:null}", groupId = "${spring.kafka.group.id:null}", clientIdPrefix = "firstListener",
            containerFactory = "templateEventListenerContainerFactory")
    public void eventsListener(@Payload(required = false) Event event,
                               final @Header(KafkaHeaders.OFFSET) Integer offset,
                               final @Header(value = KafkaHeaders.KEY, required = false) String key,
                               final @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                               final @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                               final @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timeStamp) {
        super.eventsListener(event, offset, key, partition, topic, timeStamp);
    }
}
