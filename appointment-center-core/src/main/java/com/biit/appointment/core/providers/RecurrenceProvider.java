package com.biit.appointment.core.providers;

import com.biit.appointment.core.exceptions.AppointmentNotFoundException;
import com.biit.appointment.core.exceptions.InvalidRecurrenceException;
import com.biit.appointment.core.exceptions.RecurrenceNotFoundException;
import com.biit.appointment.logger.AppointmentCenterLogger;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.entities.Recurrence;
import com.biit.appointment.persistence.repositories.AppointmentRepository;
import com.biit.appointment.persistence.repositories.RecurrenceRepository;
import com.biit.server.exceptions.BadRequestException;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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


    public Recurrence addSkipIteration(Long recurrenceId, LocalDate skipDate, String updatedBy) {
        final Recurrence recurrence = getRepository().findById(recurrenceId).orElseThrow(() ->
                new RecurrenceNotFoundException(this.getClass(), "No recurrence found with id '" + recurrenceId + "'."));

        if (!recurrence.hasMatch(skipDate)) {
            throw new InvalidRecurrenceException(this.getClass(), "Recurrence '" + recurrence + "' has no iteration on '" + skipDate + "'");
        }

        recurrence.setUpdatedBy(updatedBy);
        recurrence.addSkippedIterations(skipDate);
        AppointmentCenterLogger.info(this.getClass(), "User '" + updatedBy
                + "' has skip iteration '" + skipDate + "' on recurrence '" + recurrence + "'.");
        return getRepository().save(recurrence);
    }


    public Recurrence removeSkipIteration(Long recurrenceId, LocalDate skipDate, String updatedBy) {
        final Recurrence recurrence = getRepository().findById(recurrenceId).orElseThrow(() ->
                new RecurrenceNotFoundException(this.getClass(), "No recurrence found with id '" + recurrenceId + "'."));

        recurrence.setUpdatedBy(updatedBy);
        if (!recurrence.removeSkippedIterations(skipDate)) {
            throw new BadRequestException(this.getClass(), "No skipping date '" + skipDate + "' exists on recurrence '" + recurrence + "'.");
        }
        AppointmentCenterLogger.info(this.getClass(), "User '" + updatedBy
                + "' has removed skip iteration '" + skipDate + "' on recurrence '" + recurrence + "'.");
        return getRepository().save(recurrence);
    }


    public Recurrence addAppointmentException(Long recurrenceId, Appointment appointment, String updatedBy) {
        final Recurrence recurrence = getRepository().findById(recurrenceId).orElseThrow(() ->
                new RecurrenceNotFoundException(this.getClass(), "No recurrence found with id '" + recurrenceId + "'."));
        appointment.setUpdatedBy(updatedBy);
        appointment = appointmentRepository.save(appointment);
        recurrence.setUpdatedBy(updatedBy);
        recurrence.addAppointment(appointment);
        AppointmentCenterLogger.info(this.getClass(), "User '" + updatedBy
                + "' has added appointment exception '" + appointment + "' on recurrence '" + recurrence + "'.");
        return getRepository().save(recurrence);
    }


    public Recurrence removeAppointmentException(Long recurrenceId, Long appointmentId, String updatedBy) {
        final Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(() ->
                new AppointmentNotFoundException(this.getClass(), "No appointment found with id '" + appointmentId + "'."));
        return removeAppointmentException(recurrenceId, appointment, updatedBy);
    }


    public Recurrence removeAppointmentException(Long recurrenceId, Appointment appointment, String updatedBy) {
        final Recurrence recurrence = getRepository().findById(recurrenceId).orElseThrow(() ->
                new RecurrenceNotFoundException(this.getClass(), "No recurrence found with id '" + recurrenceId + "'."));
        recurrence.setUpdatedBy(updatedBy);
        if (!recurrence.removeAppointment(appointment)) {
            throw new BadRequestException(this.getClass(), "No appointment '" + appointment + "' exists on recurrence '" + recurrence + "'.");
        }
        AppointmentCenterLogger.info(this.getClass(), "User '" + updatedBy
                + "' has removed appointment exception '" + appointment + "' on recurrence '" + recurrence + "'.");
        return getRepository().save(recurrence);
    }


    @Override
    public void delete(Recurrence entity) {
        super.delete(entity);
        entity.getAppointments().forEach(appointment -> {
            if (entity.getAppointments().size() > 1) {
                appointmentRepository.deleteAll(entity.getAppointments().subList(1, entity.getAppointments().size()));
            }
        });
    }

    @Override
    public void deleteById(Long recurrenceId) {
        final Recurrence recurrence = getRepository().findById(recurrenceId).orElseThrow(() ->
                new RecurrenceNotFoundException(this.getClass(), "No recurrence found with id '" + recurrenceId + "'."));
        delete(recurrence);
    }

    @Override
    public void deleteAll() {
        getRepository().findAll().forEach(this::delete);
    }

    @Override
    public void deleteAll(Collection<Recurrence> entities) {
        entities.forEach(this::delete);
    }
}
