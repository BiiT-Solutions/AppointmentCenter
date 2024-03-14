package com.biit.appointment.core.controllers;

import com.biit.appointment.core.converters.AppointmentTemplateConverter;
import com.biit.appointment.core.converters.models.AppointmentTemplateConverterRequest;
import com.biit.appointment.core.models.AppointmentTemplateAvailabilityDTO;
import com.biit.appointment.core.models.AppointmentTemplateDTO;
import com.biit.appointment.core.providers.AppointmentTemplateProvider;
import com.biit.appointment.persistence.entities.AppointmentTemplate;
import com.biit.appointment.persistence.repositories.AppointmentTemplateRepository;
import com.biit.server.controller.ElementController;
import com.biit.utils.date.range.LocalDateTimeRange;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class AppointmentTemplateController extends ElementController<AppointmentTemplate, Long, AppointmentTemplateDTO, AppointmentTemplateRepository,
        AppointmentTemplateProvider, AppointmentTemplateConverterRequest, AppointmentTemplateConverter> {


    protected AppointmentTemplateController(AppointmentTemplateProvider provider, AppointmentTemplateConverter converter) {
        super(provider, converter);
    }

    @Override
    protected AppointmentTemplateConverterRequest createConverterRequest(AppointmentTemplate appointmentTemplate) {
        return new AppointmentTemplateConverterRequest(appointmentTemplate);
    }

    public List<AppointmentTemplateDTO> findByOrganizationId(Long organizationId) {
        return convertAll(getProvider().findByOrganizationId(organizationId));
    }

    public List<AppointmentTemplateAvailabilityDTO> availability(LocalDateTime lowerTimeBoundary, LocalDateTime upperTimeBoundary, Long[] templatesId) {
        final List<AppointmentTemplateAvailabilityDTO> availability = new ArrayList<>();
        for (final Long templateId : templatesId) {
            Optional<AppointmentTemplate> template = getProvider().findById(templateId);
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
        final List<LocalDateTimeRange> ranges = new ArrayList<>();
        ranges.add(range);
        //Get office hours.
        //From duration of the template. Must fit on the last hours.
        //Get speakers availability.
        //Remove already hours used by different appointments.
        return ranges;
    }
}

