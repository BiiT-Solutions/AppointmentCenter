package com.biit.appointment.rest.api;

import com.biit.appointment.core.controllers.ExaminationTypeController;
import com.biit.appointment.core.converters.ExaminationTypeConverter;
import com.biit.appointment.core.converters.models.ExaminationTypeConverterRequest;
import com.biit.appointment.core.models.ExaminationTypeDTO;
import com.biit.appointment.core.providers.ExaminationTypeProvider;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.repositories.ExaminationTypeRepository;
import com.biit.server.rest.BasicServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/examinations-types")
public class ExaminationTypeServices extends BasicServices<ExaminationType, ExaminationTypeDTO, ExaminationTypeRepository,
        ExaminationTypeProvider, ExaminationTypeConverterRequest, ExaminationTypeConverter, ExaminationTypeController> {

    protected ExaminationTypeServices(ExaminationTypeController controller) {
        super(controller);
    }
}
