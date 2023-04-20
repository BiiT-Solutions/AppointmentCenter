package com.biit.appointment.core.controllers;

import com.biit.appointment.core.converters.AppointmentConverter;
import com.biit.appointment.core.converters.models.AppointmentConverterRequest;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.repositories.AppointmentRepository;
import com.biit.server.controller.BasicInsertableController;
import org.springframework.stereotype.Controller;

@Controller
public class AppointmentController extends BasicInsertableController<Appointment, AppointmentDTO, AppointmentRepository,
        AppointmentProvider, AppointmentConverterRequest, AppointmentConverter> {

    protected AppointmentController(AppointmentProvider provider, AppointmentConverter converter) {
        super(provider, converter);
    }

    @Override
    protected AppointmentConverterRequest createConverterRequest(Appointment appointment) {
        return new AppointmentConverterRequest(appointment);
    }
}
