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
     * @param organizationId    the organization of the parameters (can be null for any organization).
     * @param organizerId       who must resolve the appointment (can be null for any organizer).
     * @param examinationType   the type of the appointment (can be null for any type).
     * @param appointmentStatus the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted           the appointment is deleted or not.
     * @return a list of appointments.
     */
    public List<Appointment> findAll(Long organizationId, Long organizerId, ExaminationType examinationType,
                                     AppointmentStatus appointmentStatus, LocalDateTime lowerTimeBoundary,
                                     LocalDateTime upperTimeBoundary, Boolean deleted) {
        return getRepository().findAll(organizationId, organizerId, examinationType, appointmentStatus, lowerTimeBoundary, upperTimeBoundary, deleted);
    }


    /**
     * Find all appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId    the organization of the parameters (can be null for any organization).
     * @param organizerId       who must resolve the appointment (can be null for any organizer).
     * @param examinationTypes  a collection of types of the appointment (can be null for any type).
     * @param appointmentStatus the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted           the appointment is deleted or not.
     * @return a list of appointments.
     */
    public List<Appointment> findBy(Long organizationId, Long organizerId, Long customerId, Collection<ExaminationType> examinationTypes,
                                    AppointmentStatus appointmentStatus, LocalDateTime lowerTimeBoundary,
                                    LocalDateTime upperTimeBoundary, Boolean deleted) {
        return getRepository().findBy(organizationId, organizerId, customerId, examinationTypes, appointmentStatus, lowerTimeBoundary,
                upperTimeBoundary, deleted);
    }

    /**
     * Counts the total appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId    the organization of the parameters (can be null for any organization).
     * @param organizerId       who must resolve the appointment (can be null for any organizer).
     * @param examinationType   the type of the appointment (can be null for any type).
     * @param appointmentStatus the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted           the appointment is deleted or not.
     * @return the total number of appointments
     */
    public long count(Long organizationId, Long organizerId, ExaminationType examinationType,
                      AppointmentStatus appointmentStatus, LocalDateTime lowerTimeBoundary,
                      LocalDateTime upperTimeBoundary, Boolean deleted) {
        return getRepository().count(organizationId, organizerId, examinationType, appointmentStatus, lowerTimeBoundary, upperTimeBoundary, deleted);
    }


    /**
     * Counts the total appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId    the organization of the parameters (can be null for any organization).
     * @param organizerId       who must resolve the appointment (can be null for any organizer).
     * @param examinationTypes  a collection of the appointment (can be null for any type).
     * @param appointmentStatus the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted           the appointment is deleted or not.
     * @return the total number of appointments.
     */
    public long count(Long organizationId, Long organizerId, Long customerId, Collection<ExaminationType> examinationTypes,
                      AppointmentStatus appointmentStatus, LocalDateTime lowerTimeBoundary,
                      LocalDateTime upperTimeBoundary, Boolean deleted) {
        if (examinationTypes == null || examinationTypes.isEmpty()) {
            return getRepository().count(organizationId, organizerId, customerId, appointmentStatus, lowerTimeBoundary,
                    upperTimeBoundary, deleted);
        }
        return getRepository().countExaminationTypesIn(organizationId, organizerId, customerId, examinationTypes, appointmentStatus, lowerTimeBoundary,
                upperTimeBoundary, deleted);
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


    /**
     * Counts how many appointments has a customer.
     *
     * @param customerId the customer id card.
     * @return the total number of appointments.
     */
    public long countByCustomerId(Long customerId) {
        return getRepository().countByAttendeeIdIn(customerId);
    }


    /**
     * Gets the appointment that are previously set on time from an appointment.
     *
     * @param appointment the appointment as a reference for the search.
     * @return the list of appointments that are before the parameters on a descendent order of start time.
     */
    public List<Appointment> getPrevious(Appointment appointment) {
        return getRepository().getPrevious(appointment);
    }

    /**
     * Gets the appointments from an organization that are planned on the past.
     *
     * @param organizationId  the organization of the parameters (can be null for any organization).
     * @param examinationType the type of the appointment (can be null for any type).
     * @return a list of appointments ordered ascendant by start time.
     */
    public List<Appointment> getPrevious(Long organizationId, ExaminationType examinationType) {
        return getRepository().getPrevious(organizationId, examinationType, LocalDateTime.now());
    }


    /**
     * Gets the appointment that are afterward of the selected appointment.
     *
     * @param appointment the appointment as a reference for the search.
     * @return the list of appointments that are before the parameters on an ascendant order of the starting time.
     */
    public List<Appointment> getNext(Appointment appointment) {
        return getRepository().getNext(appointment);
    }


    /**
     * Gets the appointments from an organization that are planned on the future.
     *
     * @param organizationId  the organization of the parameters (can be null for any organization).
     * @param examinationType the type of the appointment (can be null for any type).
     * @return a list of appointments ordered ascendant by start time.
     */
    public List<Appointment> getNext(Long organizationId, ExaminationType examinationType) {
        return getRepository().getNext(organizationId, examinationType, LocalDateTime.now());
    }
}
