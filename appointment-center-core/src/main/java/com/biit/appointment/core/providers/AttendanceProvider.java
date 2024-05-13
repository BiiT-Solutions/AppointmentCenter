package com.biit.appointment.core.providers;

import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.Attendance;
import com.biit.appointment.persistence.repositories.AttendanceRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AttendanceProvider extends ElementProvider<Attendance, Long, AttendanceRepository> {

    public AttendanceProvider(AttendanceRepository repository) {
        super(repository);
    }

    public Set<Attendance> findByAppointment(Appointment appointment) {
        return getRepository().findByAppointment(appointment);
    }
}
