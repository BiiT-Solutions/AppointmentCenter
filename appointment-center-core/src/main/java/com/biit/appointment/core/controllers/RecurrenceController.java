package com.biit.appointment.core.controllers;

import com.biit.appointment.core.controllers.kafka.RecurrenceEventSender;
import com.biit.appointment.core.converters.AppointmentConverter;
import com.biit.appointment.core.converters.RecurrenceConverter;
import com.biit.appointment.core.converters.models.RecurrenceConverterRequest;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.models.RecurrenceDTO;
import com.biit.appointment.core.providers.RecurrenceProvider;
import com.biit.appointment.persistence.entities.Recurrence;
import com.biit.appointment.persistence.repositories.RecurrenceRepository;
import com.biit.kafka.controllers.KafkaElementController;
import com.biit.server.security.IUserOrganizationProvider;
import com.biit.server.security.model.IUserOrganization;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;

@Controller
public class RecurrenceController extends KafkaElementController<Recurrence, Long, RecurrenceDTO, RecurrenceRepository,
        RecurrenceProvider, RecurrenceConverterRequest, RecurrenceConverter> {

    private final AppointmentConverter appointmentConverter;


    protected RecurrenceController(RecurrenceProvider provider, RecurrenceConverter converter, AppointmentConverter appointmentConverter,
                                   RecurrenceEventSender eventSender,
                                   List<IUserOrganizationProvider<? extends IUserOrganization>> userOrganizationProvider) {
        super(provider, converter, eventSender, userOrganizationProvider);
        this.appointmentConverter = appointmentConverter;
    }

    @Override
    protected RecurrenceConverterRequest createConverterRequest(Recurrence recurrence) {
        return new RecurrenceConverterRequest(recurrence);
    }


    public RecurrenceDTO addSkipIteration(Long recurrenceId, LocalDate skipTime, String updatedBy) {
        return convert(getProvider().addSkipIteration(recurrenceId, skipTime, updatedBy));
    }

    public RecurrenceDTO removeSkipIteration(Long recurrenceId, LocalDate skipTime, String updatedBy) {
        return convert(getProvider().removeSkipIteration(recurrenceId, skipTime, updatedBy));
    }

    public RecurrenceDTO addAppointmentException(Long recurrenceId, AppointmentDTO appointment, String updatedBy) {
        return convert(getProvider().addAppointmentException(recurrenceId,
                appointmentConverter.reverse(appointment), updatedBy));
    }


    public RecurrenceDTO removeAppointmentException(Long recurrenceId, AppointmentDTO appointment, String updatedBy) {
        return convert(getProvider().removeAppointmentException(recurrenceId,
                appointmentConverter.reverse(appointment), updatedBy));
    }

    public RecurrenceDTO removeAppointmentException(Long recurrenceId, Long appointmentId, String updatedBy) {
        return convert(getProvider().removeAppointmentException(recurrenceId,
                appointmentId, updatedBy));
    }

}
