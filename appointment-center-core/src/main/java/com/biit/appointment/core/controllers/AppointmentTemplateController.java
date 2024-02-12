package com.biit.appointment.core.controllers;

import com.biit.appointment.core.converters.AppointmentTemplateConverter;
import com.biit.appointment.core.converters.models.AppointmentTemplateConverterRequest;
import com.biit.appointment.core.models.AppointmentTemplateDTO;
import com.biit.appointment.core.providers.AppointmentTemplateProvider;
import com.biit.appointment.persistence.entities.AppointmentTemplate;
import com.biit.appointment.persistence.repositories.AppointmentTemplateRepository;
import com.biit.server.controller.ElementController;
import org.springframework.stereotype.Controller;

import java.util.List;

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

}

