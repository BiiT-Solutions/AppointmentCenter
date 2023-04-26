package com.biit.appointment.rest.api;

import com.biit.appointment.core.controllers.AppointmentTypeController;
import com.biit.appointment.core.converters.AppointmentTypeConverter;
import com.biit.appointment.core.converters.models.AppointmentTypeConverterRequest;
import com.biit.appointment.core.models.AppointmentTypeDTO;
import com.biit.appointment.core.providers.AppointmentTypeProvider;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.repositories.AppointmentTypeRepository;
import com.biit.server.rest.BasicServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appointments-types")
public class AppointmentTypeServices extends BasicServices<AppointmentType, AppointmentTypeDTO, AppointmentTypeRepository,
        AppointmentTypeProvider, AppointmentTypeConverterRequest, AppointmentTypeConverter, AppointmentTypeController> {

    protected AppointmentTypeServices(AppointmentTypeController controller) {
        super(controller);
    }
}
