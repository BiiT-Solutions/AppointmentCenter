package com.biit.appointment.core.controllers;

import com.biit.appointment.core.controllers.kafka.AppointmentEventSender;
import com.biit.appointment.core.converters.AppointmentConverter;
import com.biit.appointment.core.converters.AppointmentTemplateConverter;
import com.biit.appointment.core.converters.models.AppointmentConverterRequest;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.models.AppointmentTemplateDTO;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.core.providers.ExaminationTypeProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentStatus;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.repositories.AppointmentRepository;
import com.biit.kafka.controllers.KafkaElementController;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Controller
public class AppointmentController extends KafkaElementController<Appointment, Long, AppointmentDTO, AppointmentRepository,
        AppointmentProvider, AppointmentConverterRequest, AppointmentConverter> {

    private final ExaminationTypeProvider examinationTypeProvider;
    private final AppointmentTemplateConverter appointmentTemplateConverter;

    protected AppointmentController(AppointmentProvider provider, AppointmentConverter converter,
                                    ExaminationTypeProvider examinationTypeProvider,
                                    AppointmentEventSender eventSender,
                                    AppointmentTemplateConverter appointmentTemplateConverter) {
        super(provider, converter, eventSender);
        this.examinationTypeProvider = examinationTypeProvider;
        this.appointmentTemplateConverter = appointmentTemplateConverter;
    }

    @Override
    protected AppointmentConverterRequest createConverterRequest(Appointment appointment) {
        return new AppointmentConverterRequest(appointment);
    }

    /**
     * Find all appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId       the organization of the parameters (can be null for any organization).
     * @param organizer            who must resolve the appointment (can be null for any organizer).
     * @param attendee             the id of one attendee.
     * @param examinationTypeNames a collection of name of types.
     * @param appointmentStatuses  the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary    the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary    the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted              the appointment is deleted or not.
     * @return a list of appointments.
     */
    public List<AppointmentDTO> findByWithExaminationTypeNames(String organizationId, UUID organizer, UUID attendee, Collection<String> examinationTypeNames,
                                                               Collection<AppointmentStatus> appointmentStatuses,
                                                               LocalDateTime lowerTimeBoundary, LocalDateTime upperTimeBoundary, Boolean deleted) {
        final List<ExaminationType> examinationTypes = examinationTypeProvider.findByNameAndDeleted(examinationTypeNames, false);
        return findBy(organizationId, organizer, attendee, examinationTypes, appointmentStatuses, lowerTimeBoundary,
                upperTimeBoundary, deleted);
    }


    /**
     * Find all appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId      the organization of the parameters (can be null for any organization).
     * @param organizer           who must resolve the appointment (can be null for any organizer).
     * @param attendee            the id of one attendee.
     * @param examinationTypes    a collection of types of the appointment (can be null for any type).
     * @param appointmentStatuses the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary   the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary   the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted             the appointment is deleted or not.
     * @return a list of appointments.
     */
    public List<AppointmentDTO> findBy(String organizationId, UUID organizer, UUID attendee, Collection<ExaminationType> examinationTypes,
                                       Collection<AppointmentStatus> appointmentStatuses,
                                       LocalDateTime lowerTimeBoundary, LocalDateTime upperTimeBoundary, Boolean deleted) {
        return convertAll(getProvider().findBy(organizationId, organizer, attendee, examinationTypes, appointmentStatuses,
                lowerTimeBoundary, upperTimeBoundary, deleted));
    }

    /**
     * Counts the total appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId       the organization of the parameters (can be null for any organization).
     * @param organizer            who must resolve the appointment (can be null for any organizer).
     * @param attendee             the id of one attendee.
     * @param examinationTypeNames a collection of name of types.
     * @param appointmentStatuses  the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary    the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary    the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted              the appointment is deleted or not.
     * @return the total number of appointments
     */
    public long countByWithExaminationTypeNames(String organizationId, UUID organizer, UUID attendee, Collection<String> examinationTypeNames,
                                                Collection<AppointmentStatus> appointmentStatuses,
                                                LocalDateTime lowerTimeBoundary, LocalDateTime upperTimeBoundary, Boolean deleted) {
        final List<ExaminationType> examinationTypes = examinationTypeProvider.findByNameAndDeleted(examinationTypeNames, false);
        return getProvider().count(organizationId, organizer, attendee, examinationTypes, appointmentStatuses,
                lowerTimeBoundary, upperTimeBoundary, deleted);
    }


    /**
     * Counts the total appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId      the organization of the parameters (can be null for any organization).
     * @param organizer           who must resolve the appointment (can be null for any organizer).
     * @param attendee            the id of one attendee.
     * @param examinationTypes    a collection of types of the appointment (can be null for any type).
     * @param appointmentStatuses the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary   the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary   the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted             the appointment is deleted or not.
     * @return the total number of appointments
     */
    public long count(String organizationId, UUID organizer, UUID attendee, Collection<ExaminationType> examinationTypes,
                      Collection<AppointmentStatus> appointmentStatuses,
                      LocalDateTime lowerTimeBoundary, LocalDateTime upperTimeBoundary, Boolean deleted) {
        return getProvider().count(organizationId, organizer, attendee, examinationTypes, appointmentStatuses,
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

    public AppointmentDTO addSpeaker(Long appointmentId, UUID speakerId, String updatedBy) {
        return convert(getProvider().addSpeaker(appointmentId, speakerId, updatedBy));
    }

    public AppointmentDTO addSpeaker(AppointmentDTO appointment, UUID speakerId, String updatedBy) {
        return convert(getProvider().addSpeaker(reverse(appointment), speakerId, updatedBy));
    }

    public AppointmentDTO create(AppointmentTemplateDTO appointmentTemplateDTO, LocalDateTime startingAt, UUID organizer, String createdBy) {
        return convert(getProvider().create(appointmentTemplateConverter.reverse(appointmentTemplateDTO), startingAt, organizer, createdBy));
    }

    public List<AppointmentDTO> getByOrganizer(UUID organizer) {
        return convertAll(getProvider().findByOrganizer(organizer));
    }

    public List<AppointmentDTO> getByOrganizationId(String organizationId) {
        return convertAll(getProvider().findByOrganizationId(organizationId));
    }

    public List<AppointmentDTO> getByAttendeesIds(Collection<UUID> attendeesIds) {
        return convertAll(getProvider().findByAttendeesIn(attendeesIds));
    }

    public List<AppointmentDTO> findByAppointmentTemplatesIn(Collection<Long> appointmentTemplatesIds) {
        return convertAll(getProvider().findByAppointmentTemplatesIdsIn(appointmentTemplatesIds));
    }
}

