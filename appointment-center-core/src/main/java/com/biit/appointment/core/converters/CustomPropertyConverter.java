package com.biit.appointment.core.converters;


import com.biit.appointment.core.converters.models.CustomPropertyConverterRequest;
import com.biit.appointment.core.models.CustomPropertyDTO;
import com.biit.appointment.persistence.entities.CustomProperty;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class CustomPropertyConverter extends ElementConverter<CustomProperty, CustomPropertyDTO, CustomPropertyConverterRequest> {


    @Override
    protected CustomPropertyDTO convertElement(CustomPropertyConverterRequest from) {
        final CustomPropertyDTO customPropertyDTO = new CustomPropertyDTO();
        BeanUtils.copyProperties(from.getEntity(), customPropertyDTO);
        return customPropertyDTO;
    }

    @Override
    public CustomProperty reverse(CustomPropertyDTO to) {
        final CustomProperty customProperty = new CustomProperty();
        BeanUtils.copyProperties(to, customProperty);
        return customProperty;
    }
}
