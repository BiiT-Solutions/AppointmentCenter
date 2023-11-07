package com.biit.appointment.core.providers;

import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.entities.Recurrence;
import com.biit.appointment.persistence.repositories.AppointmentRepository;
import com.biit.appointment.persistence.repositories.RecurrenceRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
public class RecurrenceProvider extends ElementProvider<Recurrence, Long, RecurrenceRepository> {

    private final AppointmentRepository appointmentRepository;

    public RecurrenceProvider(RecurrenceRepository repository, AppointmentRepository appointmentRepository) {
        super(repository);
        this.appointmentRepository = appointmentRepository;
    }

    public List<Recurrence> findAll(Long organizationId, Long organizerId, Collection<ExaminationType> examinationType,
                                    LocalDateTime lowerTimeBoundary,
                                    LocalDateTime upperTimeBoundary) {
        return getRepository().findBy(organizationId, organizerId, examinationType, lowerTimeBoundary, upperTimeBoundary);
    }

    @Override
    public Recurrence save(Recurrence recurrence) {
        final Recurrence savedRecurrence = super.save(recurrence);
        savedRecurrence.getAppointments().forEach(appointment -> {
            appointment.setRecurrence(recurrence);
            appointmentRepository.save(appointment);
        });
        return savedRecurrence;
    }

}
