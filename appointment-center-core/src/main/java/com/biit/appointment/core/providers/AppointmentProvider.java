package com.biit.appointment.core.providers;

import com.biit.appointment.core.exceptions.InvalidParameterException;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentStatus;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.repositories.AppointmentRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
public class AppointmentProvider extends ElementProvider<Appointment, Long, AppointmentRepository> {


    public AppointmentProvider(AppointmentRepository repository) {
        super(repository);
    }

    /**
     * Finds all appointments from an organization.
     *
     * @param organizerId the organizer of the appointment.
     * @return a list of appointments.
     */
    public List<Appointment> findByOrganizerId(Long organizerId) {
        if (organizerId == null) {
            throw new InvalidParameterException(this.getClass(), "You must select an organization!");
        }
        return getRepository().findByOrganizerId(organizerId);
    }


    /**
     * Find all appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId      the organization of the parameters (can be null for any organization).
     * @param organizerId         who must resolve the appointment (can be null for any organizer).
     * @param attendee            who attend the meeting.
     * @param examinationTypes    a collection of types of the appointment (can be null for any type).
     * @param appointmentStatuses the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary   the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary   the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted             the appointment is deleted or not.
     * @return a list of appointments.
     */
    public List<Appointment> findBy(Long organizationId, Long organizerId, Long attendee, Collection<ExaminationType> examinationTypes,
                                    Collection<AppointmentStatus> appointmentStatuses,
                                    LocalDateTime lowerTimeBoundary, LocalDateTime upperTimeBoundary, Boolean deleted) {
        return getRepository().findBy(organizationId, organizerId, attendee, examinationTypes, appointmentStatuses, lowerTimeBoundary,
                upperTimeBoundary, deleted);
    }

    /**
     * Counts the total appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId      the organization of the parameters (can be null for any organization).
     * @param organizerId         who must resolve the appointment (can be null for any organizer).
     * @param attendee            who attend the meeting.
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
        return getRepository().count(organizationId, organizerId, attendee, examinationTypes, appointmentStatuses,
                lowerTimeBoundary, upperTimeBoundary, deleted);
    }


    /**
     * Checks if the appointment overlaps with any different appointment.
     *
     * @param appointment the appointment to check
     * @return if overlaps with any other appointment apart from the input.
     */
    public boolean overlaps(Appointment appointment) {
        return getRepository().overlaps(appointment) > 0;
    }

}
