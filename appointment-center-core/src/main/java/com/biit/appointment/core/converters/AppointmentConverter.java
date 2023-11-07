package com.biit.appointment.core.converters;

import com.biit.appointment.core.converters.models.AppointmentConverterRequest;
import com.biit.appointment.core.converters.models.CustomPropertyConverterRequest;
import com.biit.appointment.core.converters.models.ExaminationTypeConverterRequest;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.models.CustomPropertyDTO;
import com.biit.appointment.core.providers.CustomPropertyProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.server.controller.converters.ElementConverter;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class AppointmentConverter extends ElementConverter<Appointment, AppointmentDTO, AppointmentConverterRequest> {

    private final ExaminationTypeConverter examinationTypeConverter;
    private final CustomPropertyConverter customPropertyConverter;

    private final CustomPropertyProvider customPropertyProvider;

    public AppointmentConverter(ExaminationTypeConverter examinationTypeConverter,
                                CustomPropertyConverter customPropertyConverter,
                                CustomPropertyProvider customPropertyProvider) {
        this.examinationTypeConverter = examinationTypeConverter;
        this.customPropertyConverter = customPropertyConverter;
        this.customPropertyProvider = customPropertyProvider;
    }

    @Override
    protected AppointmentDTO convertElement(AppointmentConverterRequest from) {
        final AppointmentDTO appointmentDTO = new AppointmentDTO();
        BeanUtils.copyProperties(from.getEntity(), appointmentDTO);
        appointmentDTO.setExaminationType(examinationTypeConverter.convert(new ExaminationTypeConverterRequest(from.getEntity().getExaminationType())));
        appointmentDTO.setAttendees(new HashSet<>(from.getEntity().getAttendees()));
        appointmentDTO.setStatus(from.getEntity().getStatus());

        Collection<CustomPropertyDTO> customProperties;
        try {
            if (from.getEntity().getCustomProperties() != null) {
                customProperties = customPropertyConverter.convertAll(from.getCustomProperties().stream()
                        .map(CustomPropertyConverterRequest::new).collect(Collectors.toList()));
            } else {
                customProperties = customPropertyConverter.convertAll(from.getEntity().getCustomProperties().stream()
                        .map(CustomPropertyConverterRequest::new).collect(Collectors.toList()));
            }
        } catch (LazyInitializationException e) {
            customProperties = customPropertyConverter.convertAll(customPropertyProvider.findByAppointment(from.getEntity()).stream()
                    .map(CustomPropertyConverterRequest::new).collect(Collectors.toList()));
        }

        appointmentDTO.setCustomProperties(customProperties);

        return appointmentDTO;
    }

    @Override
    public Appointment reverse(AppointmentDTO to) {
        if (to == null) {
            return null;
        }
        final Appointment appointment = new Appointment();
        BeanUtils.copyProperties(to, appointment);
        appointment.setExaminationType(examinationTypeConverter.reverse(to.getExaminationType()));
        return appointment;
    }
}
