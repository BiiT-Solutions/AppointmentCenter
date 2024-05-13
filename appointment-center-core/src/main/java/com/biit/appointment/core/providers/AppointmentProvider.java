package com.biit.appointment.core.providers;

import com.biit.appointment.core.exceptions.AppointmentNotFoundException;
import com.biit.appointment.core.exceptions.InvalidParameterException;
import com.biit.appointment.core.exceptions.InvalidProfessionalSpecializationException;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentStatus;
import com.biit.appointment.persistence.entities.AppointmentTemplate;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.entities.ProfessionalSpecialization;
import com.biit.appointment.persistence.entities.Recurrence;
import com.biit.appointment.persistence.repositories.AppointmentRepository;
import com.biit.appointment.persistence.repositories.AppointmentTemplateRepository;
import com.biit.appointment.persistence.repositories.RecurrenceRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class AppointmentProvider extends ElementProvider<Appointment, Long, AppointmentRepository> {

    private final RecurrenceRepository recurrenceRepository;

    private final ProfessionalSpecializationProvider professionalSpecializationProvider;

    private final AppointmentTemplateRepository appointmentTemplateRepository;


    public AppointmentProvider(AppointmentRepository repository, RecurrenceRepository recurrenceRepository,
                               ProfessionalSpecializationProvider professionalSpecializationProvider,
                               AppointmentTemplateRepository appointmentTemplateRepository) {
        super(repository);
        this.recurrenceRepository = recurrenceRepository;
        this.professionalSpecializationProvider = professionalSpecializationProvider;
        this.appointmentTemplateRepository = appointmentTemplateRepository;
    }


    /**
     * Finds all appointments from an organizer.
     *
     * @param organizer the organizer of the appointment.
     * @return a list of appointments.
     */
    public List<Appointment> findByOrganizer(UUID organizer) {
        if (organizer == null) {
            throw new InvalidParameterException(this.getClass(), "You must select an organizer!");
        }
        return getRepository().findByOrganizer(organizer);
    }

    /**
     * Finds all appointments from an organization.
     *
     * @param organizationId the organization.
     * @return a list of appointments.
     */
    public List<Appointment> findByOrganizationId(String organizationId) {
        if (organizationId == null) {
            throw new InvalidParameterException(this.getClass(), "You must select an organization!");
        }
        return getRepository().findByOrganizationId(organizationId);
    }

    /**
     * Finds all appointments from a speaker.
     *
     * @param speakerIds the speakers of the appointment.
     * @return a list of appointments.
     */
    public List<Appointment> findBySpeakers(Collection<UUID> speakerIds) {
        if (speakerIds == null || speakerIds.isEmpty()) {
            throw new InvalidParameterException(this.getClass(), "You must select an speaker!");
        }
        return getRepository().findDistinctBySpeakersIn(speakerIds);
    }

    /**
     * Finds all appointments from a collection of attendees.
     *
     * @param attendeesIds a list of attendees
     * @return a list of appointments that contains any of the attendees.
     */
    public List<Appointment> findByAttendeesIn(Collection<UUID> attendeesIds) {
        if (attendeesIds == null || attendeesIds.isEmpty()) {
            throw new InvalidParameterException(this.getClass(), "You must select an attendee!");
        }
        return getRepository().findDistinctByAttendeesIn(attendeesIds);
    }

    /**
     * Find all appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId      the organization of the parameters (can be null for any organization).
     * @param organizer           who must resolve the appointment (can be null for any organizer).
     * @param attendee            who attend the meeting.
     * @param examinationTypes    a collection of types of the appointment (can be null for any type).
     * @param appointmentStatuses the status of the appointment (can be null for any status).
     * @param lowerTimeBoundary   the lower limit on time for searching an appointment  (can be null for no limit).
     * @param upperTimeBoundary   the upper limit on time for searching an appointment  (can be null for no limit).
     * @param deleted             the appointment is deleted or not.
     * @return a list of appointments.
     */
    public List<Appointment> findBy(String organizationId, UUID organizer, UUID attendee, Collection<ExaminationType> examinationTypes,
                                    Collection<AppointmentStatus> appointmentStatuses,
                                    LocalDateTime lowerTimeBoundary, LocalDateTime upperTimeBoundary, Boolean deleted) {
        final List<Recurrence> recurrences = recurrenceRepository.findBy(organizationId, organizer, examinationTypes, lowerTimeBoundary, upperTimeBoundary);
        final List<Appointment> appointments = getRepository().findBy(organizationId, organizer, attendee, examinationTypes, appointmentStatuses,
                lowerTimeBoundary, upperTimeBoundary, deleted);
        //Add appointments generated by recurrence.
        recurrences.forEach(recurrence -> {
            //Gets all matching dates.
            recurrence.getMatches(lowerTimeBoundary, upperTimeBoundary).forEach(matchingDate -> {
                //Generate a new appointment for each matching date.
                //Not if it has an appointment already generated on this date. As already got from appointmentRepository.
                //Ignore also any date marked for skip.
                if (recurrence.getAppointment(matchingDate) == null && !recurrence.getSkippedIterations().contains(matchingDate)) {
                    //Check if there is an appointment exception defined. If it is, do not create it as will be added later.
                    if (recurrence.getAppointment(matchingDate) == null) {
                        appointments.add(Appointment.of(recurrence.getAppointments().get(0),
                                matchingDate.atTime(recurrence.getAppointments().get(0).getStartTime().toLocalTime())));
                    }
                }
            });
/*            //Add all extra appointments defined by the user.
            if (recurrence.getAppointments() != null && recurrence.getAppointments().size() > 1) {
                //The first appointment is the source. Already included.
                appointments.addAll(recurrence.getAppointments().subList(1, recurrence.getAppointments().size()));
            }*/
        });
        Collections.sort(appointments);
        return appointments;
    }


    /**
     * Counts the total appointments that matches the search parameters. If startTime and endTime is defined, will search any appointment inside this range.
     *
     * @param organizationId      the organization of the appointment (can be null for any organization).
     * @param organizer           who must resolve the appointment (can be null for any organizer).
     * @param attendee            who attend the meeting.
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
        final AtomicLong appointments = new AtomicLong(getRepository().count(organizationId, organizer, attendee, examinationTypes, appointmentStatuses,
                lowerTimeBoundary, upperTimeBoundary, deleted));
        final List<Recurrence> recurrences = recurrenceRepository.findBy(organizationId, organizer, examinationTypes, lowerTimeBoundary, upperTimeBoundary);
        recurrences.forEach(recurrence -> appointments.addAndGet(recurrence.getMatches(lowerTimeBoundary, upperTimeBoundary).size()));
        return appointments.get();
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
     * Finds all appointments that are related to a template.
     *
     * @return a list of appointments that have been generated from a template.
     */
    public List<Appointment> findByAppointmentTemplateNotNull() {
        return getRepository().findByAppointmentTemplateNotNull();
    }


    /**
     * Finds all appointments that have been generated from a collection of templates.
     *
     * @param appointmentTemplatesIds a list of templates ids
     * @return a list of appointments.
     */
    public List<Appointment> findByAppointmentTemplatesIdsIn(Collection<Long> appointmentTemplatesIds) {
        return findByAppointmentTemplatesIn(appointmentTemplateRepository.findAllById(appointmentTemplatesIds));
    }


    /**
     * Finds all appointments that have been generated from a collection of templates.
     *
     * @param appointmentTemplates a list of templates
     * @return a list of appointments.
     */
    public List<Appointment> findByAppointmentTemplatesIn(Collection<AppointmentTemplate> appointmentTemplates) {
        if (appointmentTemplates == null || appointmentTemplates.isEmpty()) {
            throw new InvalidParameterException(this.getClass(), "You must select a template!");
        }
        return getRepository().findByAppointmentTemplateIn(appointmentTemplates);
    }


    public Appointment addSpeaker(Long appointmentId, UUID speaker, String updatedBy) {
        final Appointment appointment = getRepository().findById(appointmentId).orElseThrow(
                () -> new AppointmentNotFoundException(this.getClass(), "No appointment found with id '" + appointmentId + "'."));
        return addSpeaker(appointment, speaker, updatedBy);
    }

    public Appointment addSpeaker(Appointment appointment, UUID speaker, String updatedBy) {
        final List<ProfessionalSpecialization> specializations = professionalSpecializationProvider.findByUserUUID(speaker);
        if (appointment.getExaminationType() != null && appointment.getExaminationType().getAppointmentType() != null) {
            if (!specializations.stream().map(ProfessionalSpecialization::getAppointmentType).collect(Collectors.toSet())
                    .contains(appointment.getExaminationType().getAppointmentType())) {
                throw new InvalidProfessionalSpecializationException(this.getClass(), "Speaker '" + speaker
                        + "' has no the required specialization '" + appointment.getExaminationType().getAppointmentType() + "'.");
            }
        }
        appointment.addSpeaker(speaker);
        appointment.setUpdatedBy(updatedBy);
        return getRepository().save(appointment);
    }

    public Appointment create(AppointmentTemplate appointmentTemplate, LocalDateTime startingAt, UUID organizer, String createdBy) {
        if (appointmentTemplate == null) {
            return null;
        }
        final Appointment appointment = new Appointment();
        BeanUtils.copyProperties(appointmentTemplate, appointment);
        appointment.setExaminationType(appointmentTemplate.getExaminationType());
        appointment.setCreatedBy(createdBy);
        if (appointmentTemplate.getSpeakers() != null) {
            appointment.setSpeakers(new HashSet<>(appointmentTemplate.getSpeakers()));
        }
        appointment.setStartTime(startingAt);
        appointment.setEndTime(startingAt.plusMinutes(appointmentTemplate.getDuration()));
        appointment.setOrganizer(organizer);
        appointment.setColorTheme(appointmentTemplate.getColorTheme());
        appointment.setAppointmentTemplate(appointmentTemplate);

        return getRepository().save(appointment);
    }

}
