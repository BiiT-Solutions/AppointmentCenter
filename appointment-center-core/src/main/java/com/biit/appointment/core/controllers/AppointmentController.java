package com.biit.appointment.core.controllers;

import com.biit.appointment.core.converters.AppointmentConverter;
import com.biit.appointment.core.converters.models.AppointmentConverterRequest;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.core.providers.ExaminationTypeProvider;
import com.biit.appointment.core.providers.RecurrenceProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentStatus;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.entities.Recurrence;
import com.biit.appointment.persistence.repositories.AppointmentRepository;
import com.biit.server.controller.ElementController;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Controller
public class AppointmentController extends ElementController<Appointment, Long, AppointmentDTO, AppointmentRepository,
        AppointmentProvider, AppointmentConverterRequest, AppointmentConverter> {

    private final ExaminationTypeProvider examinationTypeProvider;
    private final RecurrenceProvider recurrenceProvider;

    protected AppointmentController(AppointmentProvider provider, AppointmentConverter converter,
                                    ExaminationTypeProvider examinationTypeProvider, RecurrenceProvider recurrenceProvider) {
        super(provider, converter);
        this.examinationTypeProvider = examinationTypeProvider;
        this.recurrenceProvider = recurrenceProvider;
    }

    @Override
    protected AppointmentConverterRequest createConverterRequest(Appointment appointment) {
        return new AppointmentConverterRequest(appointment);
    }

    /**
     * Find all appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId       the organization of the parameters (can be null for any organization).
     * @param organizerId          who must resolve the appointment (can be null for any organizer).
     * @param attendee             the id of one attendee.
     * @param examinationTypeNames a collection of name of types.
     * @param appointmentStatuses  the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary    the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary    the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted              the appointment is deleted or not.
     * @return a list of appointments.
     */
    public List<AppointmentDTO> findByWithExaminationTypeNames(Long organizationId, Long organizerId, Long attendee, Collection<String> examinationTypeNames,
                                                               Collection<AppointmentStatus> appointmentStatuses,
                                                               LocalDateTime lowerTimeBoundary, LocalDateTime upperTimeBoundary, Boolean deleted) {
        final List<ExaminationType> examinationTypes = examinationTypeProvider.findByNameAndDeleted(examinationTypeNames, false);
        return findBy(organizationId, organizerId, attendee, examinationTypes, appointmentStatuses, lowerTimeBoundary,
                upperTimeBoundary, deleted);
    }


    /**
     * Find all appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId      the organization of the parameters (can be null for any organization).
     * @param organizerId         who must resolve the appointment (can be null for any organizer).
     * @param attendee            the id of one attendee.
     * @param examinationTypes    a collection of types of the appointment (can be null for any type).
     * @param appointmentStatuses the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary   the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary   the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted             the appointment is deleted or not.
     * @return a list of appointments.
     */
    public List<AppointmentDTO> findBy(Long organizationId, Long organizerId, Long attendee, Collection<ExaminationType> examinationTypes,
                                       Collection<AppointmentStatus> appointmentStatuses,
                                       LocalDateTime lowerTimeBoundary, LocalDateTime upperTimeBoundary, Boolean deleted) {
        final List<Recurrence> recurrences = recurrenceProvider.findAll(organizationId, organizerId, examinationTypes, lowerTimeBoundary, upperTimeBoundary);
        final List<Appointment> appointments = getProvider().findBy(organizationId, organizerId, attendee, examinationTypes, appointmentStatuses,
                lowerTimeBoundary, upperTimeBoundary, deleted);

        //Add appointments generated by recurrence.
        recurrences.forEach(recurrence ->
                //Gets all matching dates.
                recurrence.getMatches(lowerTimeBoundary, upperTimeBoundary).forEach(matchingDate ->
                        //Generate a new appointment for each matching date.
                        appointments.add(Appointment.of(recurrence.getAppointments().get(0),
                                matchingDate.atTime(recurrence.getAppointments().get(0).getStartTime().toLocalTime())))));

        Collections.sort(appointments);
        return convertAll(appointments);
    }

    /**
     * Counts the total appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId       the organization of the parameters (can be null for any organization).
     * @param organizerId          who must resolve the appointment (can be null for any organizer).
     * @param attendee             the id of one attendee.
     * @param examinationTypeNames a collection of name of types.
     * @param appointmentStatuses  the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary    the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary    the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted              the appointment is deleted or not.
     * @return the total number of appointments
     */
    public long countByWithExaminationTypeNames(Long organizationId, Long organizerId, Long attendee, Collection<String> examinationTypeNames,
                                                Collection<AppointmentStatus> appointmentStatuses,
                                                LocalDateTime lowerTimeBoundary, LocalDateTime upperTimeBoundary, Boolean deleted) {
        final List<ExaminationType> examinationTypes = examinationTypeProvider.findByNameAndDeleted(examinationTypeNames, false);
        return getProvider().count(organizationId, organizerId, attendee, examinationTypes, appointmentStatuses,
                lowerTimeBoundary, upperTimeBoundary, deleted);
    }


    /**
     * Counts the total appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId      the organization of the parameters (can be null for any organization).
     * @param organizerId         who must resolve the appointment (can be null for any organizer).
     * @param attendee            the id of one attendee.
     * @param examinationTypes    a collection of types of the appointment (can be null for any type).
     * @param appointmentStatuses the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary   the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary   the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted             the appointment is deleted or not.
     * @return the total number of appointments
     */
    public long count(Long organizationId, Long organizerId, Long attendee, Collection<ExaminationType> examinationTypes,
                      Collection<AppointmentStatus> appointmentStatuses,
                      LocalDateTime lowerTimeBoundary, LocalDateTime upperTimeBoundary, Boolean deleted) {
        final List<Recurrence> recurrences = recurrenceProvider.findAll(organizationId, organizerId, examinationTypes, lowerTimeBoundary, upperTimeBoundary);
        return getProvider().count(organizationId, organizerId, attendee, examinationTypes, appointmentStatuses,
                lowerTimeBoundary, upperTimeBoundary, deleted);
    }


    /**
     * Checks if the appointment overlaps with any different appointment.
     *
     * @param appointment the appointment to check
     * @return if overlaps with any other appointment apart from the input.
     */
    public boolean overlaps(AppointmentDTO appointment) {
        return getProvider().overlaps(reverse(appointment));
    }
}

