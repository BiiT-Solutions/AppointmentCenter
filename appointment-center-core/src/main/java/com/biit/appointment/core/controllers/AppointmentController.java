package com.biit.appointment.core.controllers;

import com.biit.appointment.core.controllers.kafka.AppointmentEventSender;
import com.biit.appointment.core.converters.AppointmentConverter;
import com.biit.appointment.core.converters.AppointmentTemplateConverter;
import com.biit.appointment.core.converters.models.AppointmentConverterRequest;
import com.biit.appointment.core.exceptions.AppointmentNotFoundException;
import com.biit.appointment.core.exceptions.AttendanceNotFoundException;
import com.biit.appointment.core.exceptions.YouAreAlreadyOnThisAppointmentException;
import com.biit.appointment.core.exceptions.YouAreNotOnThisAppointmentException;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.models.AppointmentTemplateDTO;
import com.biit.appointment.core.models.AttendanceRequest;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.core.providers.AppointmentTemplateProvider;
import com.biit.appointment.core.providers.AttendanceProvider;
import com.biit.appointment.core.providers.ExaminationTypeProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentStatus;
import com.biit.appointment.persistence.entities.Attendance;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.repositories.AppointmentRepository;
import com.biit.kafka.controllers.KafkaElementController;
import com.biit.server.exceptions.UserNotFoundException;
import com.biit.server.security.IAuthenticatedUser;
import com.biit.server.security.IAuthenticatedUserProvider;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class AppointmentController extends KafkaElementController<Appointment, Long, AppointmentDTO, AppointmentRepository,
        AppointmentProvider, AppointmentConverterRequest, AppointmentConverter> {

    private static final int MINUTES_TO_CONSIDER_FUTURE_APPOINTMENT = 30;

    private final ExaminationTypeProvider examinationTypeProvider;
    private final AppointmentTemplateConverter appointmentTemplateConverter;

    private final IAuthenticatedUserProvider authenticatedUserProvider;

    private final AttendanceProvider attendanceProvider;

    private final AppointmentTemplateProvider appointmentTemplateProvider;

    protected AppointmentController(AppointmentProvider provider, AppointmentConverter converter,
                                    ExaminationTypeProvider examinationTypeProvider,
                                    AppointmentEventSender eventSender,
                                    AppointmentTemplateConverter appointmentTemplateConverter,
                                    IAuthenticatedUserProvider authenticatedUserProvider,
                                    AttendanceProvider attendanceProvider, AppointmentTemplateProvider appointmentTemplateProvider) {
        super(provider, converter, eventSender);
        this.examinationTypeProvider = examinationTypeProvider;
        this.appointmentTemplateConverter = appointmentTemplateConverter;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.attendanceProvider = attendanceProvider;
        this.appointmentTemplateProvider = appointmentTemplateProvider;
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
     * @param examinationTypeNames a collection of type's names.
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
     * @param examinationTypes    a collection of appointment's types (can be null for any type).
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
     * @param examinationTypeNames a collection types' names
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
     * @param examinationTypes    a collection appointment's types (can be null for any type).
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

    public List<AppointmentDTO> getByAttendeesIdsAndTemplates(Collection<UUID> attendeesIds, Collection<Long> templatesIds) {
        return convertAll(getProvider().findByAttendeesInAndAppointmentTemplateIn(attendeesIds,
                appointmentTemplateProvider.findByIdIn(templatesIds)));
    }


    public AppointmentDTO getCurrentByAttendeeAndTemplates(String username, Collection<Long> templatesIds) {
        final IAuthenticatedUser user = authenticatedUserProvider.findByUsername(username).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "No user found with username '" + username + "'."));

        return getCurrentByAttendeeAndTemplates(user, templatesIds);
    }


    public AppointmentDTO getCurrentByAttendeeAndTemplatesNames(UUID attendeeUUID, Collection<String> templatesTitle) {
        //Get organization by user.
        authenticatedUserProvider.findByUID(attendeeUUID.toString()).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "No user found with UUID '" + attendeeUUID + "'."));

        final List<Appointment> appointmentsFromUserInTemplates = getProvider().findByAttendeesInAndAppointmentTemplateIn(
                Collections.singleton(attendeeUUID),
                appointmentTemplateProvider.findByTitleIn(templatesTitle));

        return getCurrentByAttendeeAndAppointments(appointmentsFromUserInTemplates);
    }


    public AppointmentDTO getCurrentByAttendeeAndTemplates(UUID attendeeUUID, Collection<Long> templatesIds) {
        final IAuthenticatedUser user = authenticatedUserProvider.findByUID(attendeeUUID.toString()).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "No user found with UUID '" + attendeeUUID + "'."));

        return getCurrentByAttendeeAndTemplates(user, templatesIds);
    }


    public AppointmentDTO getCurrentByAttendeeAndTemplates(IAuthenticatedUser user, Collection<Long> templatesIds) {
        final List<Appointment> appointmentsFromUserInTemplates = getProvider().findByAttendeesInAndAppointmentTemplateIn(
                Collections.singleton(UUID.fromString(user.getUID())),
                appointmentTemplateProvider.findByIdIn(templatesIds));

        return getCurrentByAttendeeAndAppointments(appointmentsFromUserInTemplates);
    }

    public List<AppointmentDTO> getAppointmentsOnToday(String username) {
        final IAuthenticatedUser user = authenticatedUserProvider.findByUsername(username).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "No user found with username '" + username + "'."));

        return getAppointmentsOnToday(user);
    }

    public List<AppointmentDTO> getAppointmentsOnToday(IAuthenticatedUser user) {
        return convertAll(getProvider().findByAttendeesInAndToday(
                Collections.singleton(UUID.fromString(user.getUID()))));
    }

    public AppointmentDTO getNextAppointmentOnFuture(String username) {
        final IAuthenticatedUser user = authenticatedUserProvider.findByUsername(username).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "No user found with username '" + username + "'."));

        return getNextAppointmentOnFuture(user);
    }

    public AppointmentDTO getNextAppointmentOnFuture(IAuthenticatedUser user) {
        final List<Appointment> appointmentsFromUserInTemplates = getProvider().findByAttendeesIn(
                Collections.singleton(UUID.fromString(user.getUID())));

        return getFirstOnTheFuture(appointmentsFromUserInTemplates);
    }


    /**
     * If one appointment is currently on execution, get this one,
     * if not, get the last one at the past, if not the first one at the future.
     */
    private AppointmentDTO getCurrentByAttendeeAndAppointments(List<Appointment> appointmentsFromUserInTemplates) {

        appointmentsFromUserInTemplates.sort(Comparator.comparing(Appointment::getStartTime));


        final List<Appointment> appointmentsInThePast = appointmentsFromUserInTemplates.stream().filter(appointment ->
                appointment.getEndTime().isBefore(LocalDateTime.now())).toList();
        final List<Appointment> appointmentsAtThePresent = appointmentsFromUserInTemplates.stream().filter(appointment ->
                appointment.getStartTime().isBefore(LocalDateTime.now()) && appointment.getEndTime().isAfter(LocalDateTime.now())).toList();
        final List<Appointment> appointmentsAtTheFuture = appointmentsFromUserInTemplates.stream().filter(appointment ->
                appointment.getStartTime().isAfter(LocalDateTime.now())).toList();

        //If one is at the present, get this one, if not, get the one at the past, if not the one at the future.
        if (!appointmentsAtThePresent.isEmpty()) {
            return convert(appointmentsAtThePresent.get(0));
        }

        //If one at the future is going to almost start.
        if (!appointmentsAtTheFuture.isEmpty()) {
            if (appointmentsAtTheFuture.get(0).getStartTime().isAfter(LocalDateTime.now().minusMinutes(MINUTES_TO_CONSIDER_FUTURE_APPOINTMENT))) {
                return convert(appointmentsAtTheFuture.get(0));
            }
        }

        if (!appointmentsInThePast.isEmpty()) {
            return convert(appointmentsInThePast.get(appointmentsInThePast.size() - 1));
        }

        if (!appointmentsAtTheFuture.isEmpty()) {
            return convert(appointmentsAtTheFuture.get(0));
        }

        return null;
    }

    /**
     * Gets the first appointment that starts after today.
     */
    private AppointmentDTO getFirstOnTheFuture(List<Appointment> appointmentsFromUserInTemplates) {
        appointmentsFromUserInTemplates.sort(Comparator.comparing(Appointment::getStartTime));
        final List<Appointment> appointmentsAtTheFuture = appointmentsFromUserInTemplates.stream().filter(appointment ->
                appointment.getStartTime().isAfter(LocalDateTime.now())).toList();
        if (!appointmentsAtTheFuture.isEmpty()) {
            return convert(appointmentsAtTheFuture.get(0));
        }
        return null;
    }


    public List<AppointmentDTO> findByAppointmentTemplatesIn(Collection<Long> appointmentTemplatesIds) {
        return convertAll(getProvider().findByAppointmentTemplatesIdsIn(appointmentTemplatesIds));
    }

    public AppointmentDTO subscribe(Long appointmentId, String username) {
        final IAuthenticatedUser user = authenticatedUserProvider.findByUsername(username).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "No user found with username '" + username + "'."));

        final Appointment appointment = getProvider().findById(appointmentId).orElseThrow(() ->
                new AppointmentNotFoundException(this.getClass(), "No appointment found with id '" + appointmentId + "'."));

        appointment.getAttendees().add(UUID.fromString(user.getUID()));
        return convert(getProvider().save(appointment));
    }

    public AppointmentDTO unsubscribe(Long appointmentId, String username) {
        final IAuthenticatedUser user = authenticatedUserProvider.findByUsername(username).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "No user found with username '" + username + "'."));

        final Appointment appointment = getProvider().findById(appointmentId).orElseThrow(() ->
                new AppointmentNotFoundException(this.getClass(), "No appointment found with id '" + appointmentId + "'."));

        appointment.getAttendees().remove(UUID.fromString(user.getUID()));
        return convert(getProvider().save(appointment));
    }

    public AppointmentDTO attend(String attendanceRequest, String createdBy) {
        return attend(AttendanceRequest.decode(attendanceRequest), createdBy);
    }

    public AppointmentDTO attend(AttendanceRequest attendanceRequest, String createdBy) {
        return attend(attendanceRequest.getAppointmentId(), attendanceRequest.getAttender(), createdBy);
    }

    public AppointmentDTO attend(Long appointmentId, UUID userUUID, String createdBy) {
        final IAuthenticatedUser user = authenticatedUserProvider.findByUID(userUUID.toString()).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "No user found with UUID '" + userUUID + "'."));

        final Appointment appointment = getProvider().findById(appointmentId).orElseThrow(() ->
                new AppointmentNotFoundException(this.getClass(), "No appointment found with id '" + appointmentId + "'."));

        if (!appointment.getAttendees().contains(userUUID)) {
            throw new YouAreNotOnThisAppointmentException(this.getClass(), "User '" + user.getName() + "' is not on the attendees list!");
        }

        final Set<Attendance> attendances = attendanceProvider.findByAppointment(appointment);
        //Check you are not attended yet!
        final Optional<Attendance> currenAttendance = attendances.stream().filter(attendance ->
                !Objects.equals(attendance.getAttendee(), userUUID)).findFirst();
        if (currenAttendance.isPresent()) {
            throw new YouAreAlreadyOnThisAppointmentException(this.getClass(), "User '" + user.getName() + "' has already attended this appointment!");
        }
        final Attendance attendance = new Attendance(userUUID, appointment);
        attendance.setCreatedBy(createdBy);
        attendances.add(attendance);
        appointment.setAttendances(attendances);
        return convert(getProvider().save(appointment));
    }


    public void isAttending(Long appointmentId, String username) {
        if (username == null) {
            throw new UserNotFoundException(this.getClass(), "Username cannot be null.");
        }
        final IAuthenticatedUser user = authenticatedUserProvider.findByUsername(username).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "No user found with username '" + username + "'."));
        isAttending(appointmentId, user);
    }


    public void isAttending(Long appointmentId, UUID userUUID) {
        if (userUUID == null) {
            throw new UserNotFoundException(this.getClass(), "UUID cannot be null.");
        }
        final IAuthenticatedUser user = authenticatedUserProvider.findByUID(userUUID.toString()).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "No user found with UUID '" + userUUID + "'."));
        isAttending(appointmentId, user);
    }


    public void isAttending(Long appointmentId, IAuthenticatedUser user) {
        final Appointment appointment = getProvider().findById(appointmentId).orElseThrow(() ->
                new AppointmentNotFoundException(this.getClass(), "No appointment found with id '" + appointmentId + "'."));

        attendanceProvider.findBy(UUID.fromString(user.getUID()), appointment).orElseThrow(() ->
                new AttendanceNotFoundException(this.getClass(), "User '" + user + "' with UUID '" + user.getUID() + "' has not passed the QR code."));
    }


    public AppointmentDTO unattend(Long appointmentId, UUID userUUID, String createdBy) {
        final IAuthenticatedUser user = authenticatedUserProvider.findByUID(userUUID.toString()).orElseThrow(() ->
                new UserNotFoundException(this.getClass(), "No user found with UUID '" + userUUID + "'."));

        final Appointment appointment = getProvider().findById(appointmentId).orElseThrow(() ->
                new AppointmentNotFoundException(this.getClass(), "No appointment found with id '" + appointmentId + "'."));

        if (!appointment.getAttendees().contains(userUUID)) {
            throw new YouAreNotOnThisAppointmentException(this.getClass(), "User '" + user.getName() + "' is not on the attendees list!");
        }

        final Set<Attendance> attendances = attendanceProvider.findByAppointment(appointment);

        //Check you are attending!
        attendances.stream().filter(attendance -> Objects.equals(attendance.getAttendee(), userUUID)).findFirst().orElseThrow(() ->
                new YouAreNotOnThisAppointmentException(this.getClass(), "User '" + user.getName() + "' is not attending this appointment!"));

        appointment.setAttendances(attendances.stream().filter(
                attendance -> !Objects.equals(attendance.getAttendee(), userUUID)
        ).collect(Collectors.toSet()));
        return convert(getProvider().save(appointment));
    }
}

