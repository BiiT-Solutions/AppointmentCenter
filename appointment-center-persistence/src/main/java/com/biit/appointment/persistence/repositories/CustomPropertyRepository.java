package com.biit.appointment.persistence.repositories;


import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.CustomProperty;
import com.biit.server.persistence.repositories.StorableObjectRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomPropertyRepository extends StorableObjectRepository<CustomProperty, Long> {

    List<CustomProperty> findByAppointment(Appointment appointment);

}
