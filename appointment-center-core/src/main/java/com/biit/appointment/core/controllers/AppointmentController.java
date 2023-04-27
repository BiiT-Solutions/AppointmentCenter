package com.biit.appointment.core.controllers;

import com.biit.appointment.core.converters.AppointmentConverter;
import com.biit.appointment.core.converters.ExaminationTypeConverter;
import com.biit.appointment.core.converters.models.AppointmentConverterRequest;
import com.biit.appointment.core.exceptions.InvalidParameterException;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.core.providers.ExaminationTypeProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentStatus;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.repositories.AppointmentRepository;
import com.biit.server.controller.BasicInsertableController;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Controller
public class AppointmentController extends BasicInsertableController<Appointment, AppointmentDTO, AppointmentRepository,
        AppointmentProvider, AppointmentConverterRequest, AppointmentConverter> {

    private final ExaminationTypeProvider examinationTypeProvider;
    private final ExaminationTypeConverter examinationTypeConverter;

    protected AppointmentController(AppointmentProvider provider, AppointmentConverter converter,
                                    ExaminationTypeProvider examinationTypeProvider, ExaminationTypeConverter examinationTypeConverter) {
        super(provider, converter);
        this.examinationTypeProvider = examinationTypeProvider;
        this.examinationTypeConverter = examinationTypeConverter;

    }

    @Override
    protected AppointmentConverterRequest createConverterRequest(Appointment appointment) {
        return new AppointmentConverterRequest(appointment);
    }

    /**
     * Finds all appointments from an organization.
     *
     * @param organizerId the organizer of the appointment.
     * @return a list of appointments.
     */
    public List<AppointmentDTO> findByOrganizerId(Long organizerId) {
        if (organizerId == null) {
            throw new InvalidParameterException(this.getClass(), "You must select an organization!");
        }
        return convertAll(provider.findByOrganizerId(organizerId));
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
    public List<AppointmentDTO> findAll(Long organizationId, Long organizerId, ExaminationType examinationType,
                                        AppointmentStatus appointmentStatus, LocalDateTime lowerTimeBoundary,
                                        LocalDateTime upperTimeBoundary, Boolean deleted) {
        return convertAll(provider.findAll(organizationId, organizerId, examinationType, appointmentStatus, lowerTimeBoundary, upperTimeBoundary, deleted));
    }


    /**
     * Find all appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId    the organization of the parameters (can be null for any organization).
     * @param organizerId       who must resolve the appointment (can be null for any organizer).
     * @param customerId        the customer if you want to filter by customer.
     * @param examinationTypes  a collection of types of the appointment (can be null for any type).
     * @param appointmentStatus the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted           the appointment is deleted or not.
     * @return a list of appointments.
     */
    public List<AppointmentDTO> findBy(Long organizationId, Long organizerId, Long customerId, Collection<ExaminationType> examinationTypes,
                                       AppointmentStatus appointmentStatus, LocalDateTime lowerTimeBoundary,
                                       LocalDateTime upperTimeBoundary, Boolean deleted) {
        return convertAll(provider.findBy(organizationId, organizerId, customerId, examinationTypes, appointmentStatus, lowerTimeBoundary, upperTimeBoundary, deleted));
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
    long count(Long organizationId, Long organizerId, ExaminationType examinationType,
               AppointmentStatus appointmentStatus, LocalDateTime lowerTimeBoundary,
               LocalDateTime upperTimeBoundary, Boolean deleted) {
        return provider.count(organizationId, organizerId, examinationType, appointmentStatus, lowerTimeBoundary, upperTimeBoundary, deleted);

    }


    /**
     * Counts the total appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId    the organization of the parameters (can be null for any organization).
     * @param organizerId       who must resolve the appointment (can be null for any organizer).
     * @param customerId        the customer if you want to filter by customer.
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
        return provider.count(organizationId, organizerId, customerId, examinationTypes, appointmentStatus, lowerTimeBoundary, upperTimeBoundary, deleted);
    }


    /**
     * Checks if the appointment overlaps with any different appointment.
     *
     * @param appointment the appointment to check
     * @return if overlaps with any other appointment apart from the input.
     */
    public boolean overlaps(AppointmentDTO appointment) {
        return provider.overlaps(reverse(appointment));
    }


    /**
     * Counts how many appointments has a customer.
     *
     * @param customerId the customer id card.
     * @return the total number of appointments.
     */
    public long countByCustomerId(Long customerId) {
        return provider.countByCustomerId(customerId);
    }


    /**
     * Gets the appointment that are previously set on time from an appointment.
     *
     * @param appointment the appointment as a reference for the search.
     * @return the list of appointments that are before the parameters on a descendent order of start time.
     */
    public List<AppointmentDTO> getPrevious(AppointmentDTO appointment) {
        return convertAll(provider.getPrevious(reverse(appointment)));
    }

    /**
     * Gets the appointments from an organization that are planned on the past.
     *
     * @param organizationId      the organization of the parameters (can be null for any organization).
     * @param examinationTypeName the name of the examination type (can be null for any type).
     * @return a list of appointments ordered ascendant by start time.
     */
    public List<AppointmentDTO> getPrevious(Long organizationId, String examinationTypeName) {
        return getPrevious(organizationId, examinationTypeName != null ? examinationTypeProvider.findByNameAndOrganizationId(examinationTypeName, organizationId, false) : null);
    }

    /**
     * Gets the appointments from an organization that are planned on the past.
     *
     * @param organizationId  the organization of the parameters (can be null for any organization).
     * @param examinationType the type of the appointment (can be null for any type).
     * @return a list of appointments ordered ascendant by start time.
     */
    public List<AppointmentDTO> getPrevious(Long organizationId, ExaminationType examinationType) {
        return convertAll(provider.getPrevious(organizationId, examinationType));
    }


    /**
     * Gets the appointment that are afterward of the selected appointment.
     *
     * @param appointment the appointment as a reference for the search.
     * @return the list of appointments that are before the parameters on an ascendant order of the starting time.
     */
    public List<AppointmentDTO> getNext(AppointmentDTO appointment) {
        return convertAll(provider.getNext(reverse(appointment)));
    }

    /**
     * Gets the appointments from an organization that are planned on the future.
     *
     * @param organizationId      the organization of the parameters (can be null for any organization).
     * @param examinationTypeName the name of the examination type (can be null for any type).
     * @return a list of appointments ordered ascendant by start time.
     */
    public List<AppointmentDTO> getNext(Long organizationId, String examinationTypeName) {
        return getNext(organizationId, examinationTypeName != null ? examinationTypeProvider.findByNameAndOrganizationId(examinationTypeName, organizationId, false) : null);
    }


    /**
     * Gets the appointments from an organization that are planned on the future.
     *
     * @param organizationId  the organization of the parameters (can be null for any organization).
     * @param examinationType the type of the appointment (can be null for any type).
     * @return a list of appointments ordered ascendant by start time.
     */
    public List<AppointmentDTO> getNext(Long organizationId, ExaminationType examinationType) {
        return convertAll(provider.getNext(organizationId, examinationType));
    }
}

