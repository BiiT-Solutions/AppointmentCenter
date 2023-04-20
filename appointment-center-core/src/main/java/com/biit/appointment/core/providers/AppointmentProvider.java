package com.biit.appointment.core.providers;

import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.repositories.AppointmentRepository;
import com.biit.server.providers.CrudProvider;
import org.springframework.stereotype.Service;

@Service
public class AppointmentProvider extends CrudProvider<Appointment, Long, AppointmentRepository> {


    public AppointmentProvider(AppointmentRepository repository) {
        super(repository);
    }
}
