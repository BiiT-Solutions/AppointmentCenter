package com.biit.appointment.core.converters;

import com.biit.appointment.core.converters.models.AppointmentConverterRequest;
import com.biit.appointment.core.converters.models.ExaminationTypeConverterRequest;
import com.biit.appointment.core.converters.models.RecurrenceConverterRequest;
import com.biit.appointment.core.models.RecurrenceDTO;
import com.biit.appointment.persistence.entities.Recurrence;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class RecurrenceConverter extends ElementConverter<Recurrence, RecurrenceDTO, RecurrenceConverterRequest> {

    private final AppointmentConverter appointmentConverter;
    private final ExaminationTypeConverter examinationTypeConverter;

    public RecurrenceConverter(AppointmentConverter appointmentConverter,
                               ExaminationTypeConverter examinationTypeConverter) {
        this.appointmentConverter = appointmentConverter;
        this.examinationTypeConverter = examinationTypeConverter;
    }

    @Override
    protected RecurrenceDTO convertElement(RecurrenceConverterRequest from) {
        if (from.getEntity() == null) {
            return null;
        }
        final RecurrenceDTO recurrenceDTO = new RecurrenceDTO();
        BeanUtils.copyProperties(from.getEntity(), recurrenceDTO);
        recurrenceDTO.setAppointments(new HashSet<>(appointmentConverter.convertAll(from.getEntity().getAppointments().stream()
                .map(AppointmentConverterRequest::new).collect(Collectors.toList()))));
        recurrenceDTO.setExaminationType(examinationTypeConverter.convertElement(new ExaminationTypeConverterRequest(from.getEntity().getExaminationType())));
        return recurrenceDTO;
    }

    @Override
    public Recurrence reverse(RecurrenceDTO to) {
        if (to == null) {
            return null;
        }
        final Recurrence recurrence = new Recurrence();
        BeanUtils.copyProperties(to, recurrence);
        recurrence.setAppointments(new ArrayList<>(appointmentConverter.reverseAll(to.getAppointments())));
        recurrence.setExaminationType(examinationTypeConverter.reverse(to.getExaminationType()));
        return recurrence;
    }
}
