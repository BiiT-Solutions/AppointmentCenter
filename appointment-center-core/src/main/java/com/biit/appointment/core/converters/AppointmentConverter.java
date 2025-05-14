package com.biit.appointment.core.converters;

import com.biit.appointment.core.converters.models.AppointmentConverterRequest;
import com.biit.appointment.core.converters.models.CustomPropertyConverterRequest;
import com.biit.appointment.core.converters.models.ExaminationTypeConverterRequest;
import com.biit.appointment.core.exceptions.InvalidParameterException;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.models.AppointmentStatus;
import com.biit.appointment.core.models.CustomPropertyDTO;
import com.biit.appointment.core.providers.CustomPropertyProvider;
import com.biit.appointment.core.providers.RecurrenceProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.repositories.AppointmentTemplateRepository;
import com.biit.server.controller.converters.ElementConverter;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

@Component
public class AppointmentConverter extends ElementConverter<Appointment, AppointmentDTO, AppointmentConverterRequest> {

    private final ExaminationTypeConverter examinationTypeConverter;
    private final CustomPropertyConverter customPropertyConverter;
    private final CustomPropertyProvider customPropertyProvider;
    private final RecurrenceProvider recurrenceProvider;
    private final AppointmentTemplateRepository appointmentTemplateRepository;

    public AppointmentConverter(ExaminationTypeConverter examinationTypeConverter,
                                CustomPropertyConverter customPropertyConverter,
                                CustomPropertyProvider customPropertyProvider,
                                RecurrenceProvider recurrenceProvider, AppointmentTemplateRepository appointmentTemplateRepository) {
        this.examinationTypeConverter = examinationTypeConverter;
        this.customPropertyConverter = customPropertyConverter;
        this.customPropertyProvider = customPropertyProvider;
        this.recurrenceProvider = recurrenceProvider;
        this.appointmentTemplateRepository = appointmentTemplateRepository;
    }

    @Override
    public List<AppointmentDTO> convertAll(Collection<AppointmentConverterRequest> from) {
        if (from == null) {
            return new ArrayList<>();
        }
        return from.stream().map(this::convert).sorted(Comparator.comparing(AppointmentDTO::getStartTime,
                Comparator.nullsFirst(Comparator.naturalOrder()))).toList();
    }

    @Override
    protected AppointmentDTO convertElement(AppointmentConverterRequest from) {
        if (from.getEntity() == null) {
            return null;
        }
        final AppointmentDTO appointmentDTO = new AppointmentDTO();
        BeanUtils.copyProperties(from.getEntity(), appointmentDTO);
        appointmentDTO.setExaminationType(examinationTypeConverter.convert(new ExaminationTypeConverterRequest(from.getEntity().getExaminationType())));
        if (from.getEntity().getAttendees() != null) {
            appointmentDTO.setAttendees(new HashSet<>(from.getEntity().getAttendees()));
        }
        if (from.getEntity().getSpeakers() != null) {
            appointmentDTO.setSpeakers(new HashSet<>(from.getEntity().getSpeakers()));
        }
        appointmentDTO.setStatus(AppointmentStatus.valueOf(from.getEntity().getStatus().name()));

        Collection<CustomPropertyDTO> customProperties;
        try {
            if (from.getCustomProperties() != null) {
                customProperties = customPropertyConverter.convertAll(from.getCustomProperties().stream()
                        .map(CustomPropertyConverterRequest::new).toList());
            } else {
                if (from.getEntity().getCustomProperties() != null) {
                    customProperties = customPropertyConverter.convertAll(from.getEntity().getCustomProperties().stream()
                            .map(CustomPropertyConverterRequest::new).toList());
                } else {
                    customProperties = new ArrayList<>();
                }
            }
        } catch (LazyInitializationException e) {
            customProperties = customPropertyConverter.convertAll(customPropertyProvider.findByAppointment(from.getEntity()).stream()
                    .map(CustomPropertyConverterRequest::new).toList());
        }

        appointmentDTO.setCustomProperties(customProperties);
        if (from.getEntity().getRecurrence() != null) {
            appointmentDTO.setRecurrence(from.getEntity().getRecurrence().getId());
        }

        if (from.getEntity().getAppointmentTemplate() != null) {
            appointmentDTO.setAppointmentTemplateId(from.getEntity().getAppointmentTemplate().getId());
        }

        //If no infographic template is defined, user title.
        if (from.getEntity().getInfographicTemplate() == null) {
            appointmentDTO.setInfographicTemplate(from.getEntity().getTitle());
        }

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
        if (to.getAttendees() != null) {
            appointment.setAttendees(new HashSet<>(to.getAttendees()));
        }
        appointment.setStatus(com.biit.appointment.persistence.entities.AppointmentStatus.valueOf(to.getStatus().name()));
        if (to.getSpeakers() != null) {
            appointment.setSpeakers(new HashSet<>(to.getSpeakers()));
        }
        appointment.setCustomProperties(customPropertyConverter.reverseAll(to.getCustomProperties()));
        if (to.getRecurrence() != null) {
            appointment.setRecurrence(recurrenceProvider.get(to.getRecurrence()).orElse(null));
        }
        if (to.getAppointmentTemplateId() != null) {
            appointment.setAppointmentTemplate(appointmentTemplateRepository.findById(to.getAppointmentTemplateId()).orElseThrow(() ->
                    new InvalidParameterException(this.getClass(), "Does not exists an appointment template with id '"
                            + to.getAppointmentTemplateId() + "'")));
        }

        return appointment;
    }
}
