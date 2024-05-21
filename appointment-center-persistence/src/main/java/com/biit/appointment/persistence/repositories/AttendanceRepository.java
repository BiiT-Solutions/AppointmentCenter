package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.Attendance;
import com.biit.server.persistence.repositories.ElementRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends ElementRepository<Attendance, Long> {

    Set<Attendance> findByAppointment(Appointment appointment);

    Set<Attendance> findByAttendee(UUID attendee);

    Optional<Attendance> findByAttendeeAndAppointment(UUID attendee, Appointment appointment);
}
