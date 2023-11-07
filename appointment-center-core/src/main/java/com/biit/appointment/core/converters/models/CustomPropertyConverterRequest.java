package com.biit.appointment.core.converters.models;


import com.biit.appointment.persistence.entities.CustomProperty;
import com.biit.server.converters.models.ConverterRequest;

public class CustomPropertyConverterRequest extends ConverterRequest<CustomProperty> {
    public CustomPropertyConverterRequest(CustomProperty entity) {
        super(entity);
    }
}
