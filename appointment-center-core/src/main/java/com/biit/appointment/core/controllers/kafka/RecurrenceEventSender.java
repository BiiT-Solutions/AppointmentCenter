package com.biit.appointment.core.controllers.kafka;

/*-
 * #%L
 * AppointmentCenter (Core)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


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
