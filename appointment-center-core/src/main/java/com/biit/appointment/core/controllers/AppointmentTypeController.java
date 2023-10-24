package com.biit.appointment.core.controllers;

import com.biit.server.controller.BasicElementController;
import com.biit.appointment.core.converters.AppointmentTypeConverter;
import com.biit.appointment.core.converters.models.AppointmentTypeConverterRequest;
import com.biit.appointment.core.models.AppointmentTypeDTO;
import com.biit.appointment.core.providers.AppointmentTypeProvider;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.repositories.AppointmentTypeRepository;
import org.springframework.stereotype.Controller;

@Controller
public class AppointmentTypeController extends BasicElementController<AppointmentType, Long, AppointmentTypeDTO, AppointmentTypeRepository,
        AppointmentTypeProvider, AppointmentTypeConverterRequest, AppointmentTypeConverter> {

    protected AppointmentTypeController(AppointmentTypeProvider provider, AppointmentTypeConverter converter) {
        super(provider, converter);
    }

    @Override
    protected AppointmentTypeConverterRequest createConverterRequest(AppointmentType appointmentType) {
        return new AppointmentTypeConverterRequest(appointmentType);
    }

    public AppointmentTypeDTO findByNameAndOrganizationId(String name, Long organizationId) {
        return convert(getProvider().findByNameAndOrganizationId(name, organizationId));
    }
}
