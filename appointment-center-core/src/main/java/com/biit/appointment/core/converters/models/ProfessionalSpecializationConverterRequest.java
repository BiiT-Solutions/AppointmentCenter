package com.biit.appointment.core.converters.models;

import com.biit.appointment.persistence.entities.ProfessionalSpecialization;
import com.biit.server.converters.models.ConverterRequest;

public class ProfessionalSpecializationConverterRequest extends ConverterRequest<ProfessionalSpecialization> {
    public ProfessionalSpecializationConverterRequest(ProfessionalSpecialization professionalSpecialization) {
        super(professionalSpecialization);
    }
}
