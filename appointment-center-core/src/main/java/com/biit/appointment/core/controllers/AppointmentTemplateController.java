package com.biit.appointment.core.controllers;

import com.biit.appointment.core.converters.AppointmentTemplateConverter;
import com.biit.appointment.core.converters.models.AppointmentTemplateConverterRequest;
import com.biit.appointment.core.models.AppointmentTemplateAvailabilityDTO;
import com.biit.appointment.core.models.AppointmentTemplateDTO;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.core.providers.AppointmentTemplateProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentTemplate;
import com.biit.appointment.persistence.repositories.AppointmentTemplateRepository;
import com.biit.server.controller.ElementController;
import com.biit.utils.date.range.LocalDateTimeRange;
import com.biit.utils.date.range.LocalDateTimeRangeUtils;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
public class AppointmentTemplateController extends ElementController<AppointmentTemplate, Long, AppointmentTemplateDTO, AppointmentTemplateRepository,
        AppointmentTemplateProvider, AppointmentTemplateConverterRequest, AppointmentTemplateConverter> {

    private final AppointmentProvider appointmentProvider;


    protected AppointmentTemplateController(AppointmentTemplateProvider provider, AppointmentTemplateConverter converter,
                                            AppointmentProvider appointmentProvider) {
        super(provider, converter);
        this.appointmentProvider = appointmentProvider;
    }

    @Override
    protected AppointmentTemplateConverterRequest createConverterRequest(AppointmentTemplate appointmentTemplate) {
        return new AppointmentTemplateConverterRequest(appointmentTemplate);
    }

    public List<AppointmentTemplateDTO> findByOrganizationId(String organizationId) {
        return convertAll(getProvider().findByOrganizationId(organizationId));
    }

    public List<AppointmentTemplateDTO> findByAttendeeOnAppointment(UUID attendeeUUID) {
        return convertAll(getProvider().findByAttendeeOnAppointment(attendeeUUID));
    }


    @Override
    public void delete(AppointmentTemplateDTO entity, String deletedBy) {
        //Remove relation in appointments.
        final List<Appointment> appointments = appointmentProvider.findByAppointmentTemplatesIdsIn(Collections.singletonList(entity.getId()));
        appointments.forEach(appointment -> appointment.setAppointmentTemplate(null));
        appointmentProvider.saveAll(appointments);

        super.delete(entity, deletedBy);
    }


    @Override
    public void deleteAll(String deletedBy) {
        //Remove relation in appointments.
        final List<Appointment> appointments = appointmentProvider.findByAppointmentTemplateNotNull();
        appointments.forEach(appointment -> appointment.setAppointmentTemplate(null));
        appointmentProvider.saveAll(appointments);

        super.deleteAll(deletedBy);
    }


    @Override
    public void deleteById(Long id, String deletedBy) {
        //Remove relation in appointments.
        final List<Appointment> appointments = appointmentProvider.findByAppointmentTemplatesIdsIn(Collections.singletonList(id));
        appointments.forEach(appointment -> appointment.setAppointmentTemplate(null));
        appointmentProvider.saveAll(appointments);

        super.deleteById(id, deletedBy);
    }


    public List<AppointmentTemplateAvailabilityDTO> availability(LocalDateTime lowerTimeBoundary, LocalDateTime upperTimeBoundary, Long[] templatesId) {
        final List<AppointmentTemplateAvailabilityDTO> availability = new ArrayList<>();
        for (final Long templateId : templatesId) {
            final Optional<AppointmentTemplate> template = getProvider().findById(templateId);
            if (template.isPresent()) {
                final AppointmentTemplateAvailabilityDTO appointmentTemplateAvailabilityDTO = new AppointmentTemplateAvailabilityDTO(convert(template.get()));
                appointmentTemplateAvailabilityDTO.setAvailability(calculateAvailability(new LocalDateTimeRange(lowerTimeBoundary, upperTimeBoundary),
                        appointmentTemplateAvailabilityDTO.getAppointmentTemplate()));
                availability.add(appointmentTemplateAvailabilityDTO);
            }
        }
        return availability;
    }

    private List<LocalDateTimeRange> calculateAvailability(LocalDateTimeRange range, AppointmentTemplateDTO appointmentTemplateDTO) {
        List<LocalDateTimeRange> ranges = new ArrayList<>();
        ranges.add(range);
        //Get office hours.

        //Limit by speakers' availability.
        final List<Appointment> appointmentsWithSpeakers = appointmentProvider.findBySpeakers(appointmentTemplateDTO.getSpeakers());
        for (Appointment appointment : appointmentsWithSpeakers) {
            ranges = LocalDateTimeRangeUtils.removeRange(ranges, new LocalDateTimeRange(appointment.getStartTime(), appointment.getEndTime()));
        }

        //Limit by room's availability.

        //Remove already hours used by different appointments.

        //No appointments on the past.
        LocalDateTimeRangeUtils.removeRange(ranges, new LocalDateTimeRange(LocalDateTime.of(0, 1, 1, 0, 0, 1),
                LocalDateTime.now()));

        return ranges;
    }
}

