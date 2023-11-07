package com.biit.appointment.core.providers;


import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.CustomProperty;
import com.biit.appointment.persistence.repositories.CustomPropertyRepository;
import com.biit.server.providers.StorableObjectProvider;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomPropertyProvider extends StorableObjectProvider<CustomProperty, Long, CustomPropertyRepository> {

    public CustomPropertyProvider(CustomPropertyRepository repository) {
        super(repository);
    }

    public List<CustomProperty> findByAppointment(Appointment appointment) {
        return getRepository().findByAppointment(appointment);
    }

}
