package com.biit.appointment.core.converters.models;

import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.server.converters.models.ConverterRequest;

public class ExaminationTypeConverterRequest extends ConverterRequest<ExaminationType> {
    public ExaminationTypeConverterRequest(ExaminationType examinationType) {
        super(examinationType);
    }
}
