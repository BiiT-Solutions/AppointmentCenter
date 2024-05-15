package com.biit.appointment.core.providers;

import com.biit.appointment.core.exceptions.InvalidParameterException;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentTemplate;
import com.biit.appointment.persistence.repositories.AppointmentRepository;
import com.biit.appointment.persistence.repositories.AppointmentTemplateRepository;
import com.biit.server.providers.ElementProvider;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AppointmentTemplateProvider extends ElementProvider<AppointmentTemplate, Long, AppointmentTemplateRepository> {

    private final AppointmentRepository appointmentRepository;


    public AppointmentTemplateProvider(AppointmentTemplateRepository repository, AppointmentRepository appointmentRepository) {
        super(repository);
        this.appointmentRepository = appointmentRepository;
    }


    /**
     * Finds all appointments from an organization.
     *
     * @param organizationId the organization of the appointment.
     * @return a list of appointments.
     */
    public List<AppointmentTemplate> findByOrganizationId(String organizationId) {
        if (organizationId == null) {
            throw new InvalidParameterException(this.getClass(), "You must select an organization!");
        }
        return getRepository().findByOrganizationId(organizationId);
    }


    public Optional<AppointmentTemplate> findByTitleOrganizationId(String title, String organizationId) {
        if (organizationId == null) {
            throw new InvalidParameterException(this.getClass(), "You must select an organization!");
        }
        return getRepository().findByTitleAndOrganizationId(title, organizationId);
    }


    public List<AppointmentTemplate> findByTitleIn(Collection<String> titles) {
        return getRepository().findByTitleIn(titles);
    }


    public List<AppointmentTemplate> findByAttendeeOnAppointment(UUID attendeeUUID) {
        final List<Appointment> appointments = appointmentRepository.findDistinctByAttendeesIn(Collections.singletonList(attendeeUUID));
        return getRepository().findAllById(appointments.stream().map(appointment ->
                appointment.getAppointmentTemplate().getId()).collect(Collectors.toSet()));
    }

}
