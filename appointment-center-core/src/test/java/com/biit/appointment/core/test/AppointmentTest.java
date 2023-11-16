package com.biit.appointment.core.test;

import com.biit.appointment.core.exceptions.InvalidProfessionalSpecializationException;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.core.providers.AppointmentTypeProvider;
import com.biit.appointment.core.providers.ExaminationTypeProvider;
import com.biit.appointment.core.providers.ProfessionalSpecializationProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.entities.ProfessionalSpecialization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@Test(groups = {"appointmentTest"})
public class AppointmentTest extends AbstractTestNGSpringContextTests {

    private final static Long ORGANIZER_ID = 32L;
    private final static Long ORGANIZATION_ID = 43L;
    private final static Long PRACTITIONER_ID = 42L;
    private final static Long OTHER_PRACTITIONER_ID = 41L;

    private static final String APPOINTMENT_SPECIALTY = "Physical";
    private static final String OTHER_APPOINTMENT_SPECIALTY = "TaiChi";

    private static final String TEST_TYPE_NAME = "basic";
    private static final String OTHER_TYPE_NAME = "Acupuncture";

    private static final Set<Long> ATTENDEES = new HashSet<>(Arrays.asList(1L, 2L, 3L, 4L, 5L));

    @Autowired
    private AppointmentProvider appointmentProvider;

    @Autowired
    private AppointmentTypeProvider appointmentTypeProvider;


    @Autowired
    private ExaminationTypeProvider examinationTypeProvider;

    @Autowired
    private ProfessionalSpecializationProvider professionalSpecializationProvider;

    private ExaminationType type;
    private ExaminationType type2;

    public static ExaminationType generateExaminationType(String name, AppointmentType appointmentType) {
        return new ExaminationType(name, ORGANIZATION_ID, appointmentType);
    }

    private Appointment generateAppointment(LocalDateTime onDate) {
        final Appointment appointment = new Appointment();
        appointment.setStartTime(LocalDateTime.now());
        appointment.setEndTime(LocalDateTime.now().plusHours(2));
        appointment.setOrganizerId(ORGANIZER_ID);
        appointment.setOrganizationId(ORGANIZATION_ID);
        appointment.setExaminationType(type);
        appointment.setAttendees(ATTENDEES);
        appointment.setCost(50D);

        LocalDateTime appointmentStartTime = appointment.getStartTime();
        appointment.setStartTime(onDate.toLocalDate().atTime(appointment.getStartTime().toLocalTime()));
        appointment.setEndTime(appointment.getStartTime().plus(Duration.between(appointmentStartTime, appointment.getEndTime())));

        return appointmentProvider.save(appointment);
    }

    @BeforeClass
    public void generateExaminationTypes() {
        AppointmentType appointmentType = new AppointmentType(APPOINTMENT_SPECIALTY, ORGANIZATION_ID);
        appointmentType = appointmentTypeProvider.save(appointmentType);
        type = generateExaminationType(TEST_TYPE_NAME, appointmentType);
        type = examinationTypeProvider.save(type);

        appointmentType = new AppointmentType(OTHER_APPOINTMENT_SPECIALTY, ORGANIZATION_ID);
        appointmentType = appointmentTypeProvider.save(appointmentType);
        type2 = generateExaminationType(OTHER_TYPE_NAME, appointmentType);
        type2 = examinationTypeProvider.save(type2);
    }

    @BeforeClass
    public void generateProfessionalSpecialization() {
        professionalSpecializationProvider.save(new ProfessionalSpecialization(type.getAppointmentType().getName(), type.getAppointmentType(), ORGANIZATION_ID, PRACTITIONER_ID));
        professionalSpecializationProvider.save(new ProfessionalSpecialization(type2.getAppointmentType().getName(), type2.getAppointmentType(), ORGANIZATION_ID, OTHER_PRACTITIONER_ID));
    }

    @AfterMethod
    public void cleanDatabase() {
        appointmentProvider.deleteAll();
    }

    @Test(expectedExceptions = InvalidProfessionalSpecializationException.class)
    public void addSpeakerWithoutSpecialization() {
        final Appointment nextWeekAppointment = generateAppointment(LocalDateTime.now().plusDays(7));
        appointmentProvider.addSpeaker(nextWeekAppointment, 1L, null);
    }

    @Test
    public void addSpeakerWithSpecialization() {
        final Appointment nextWeekAppointment = generateAppointment(LocalDateTime.now().plusDays(7));
        appointmentProvider.addSpeaker(nextWeekAppointment, PRACTITIONER_ID, null);
    }

    @Test(expectedExceptions = InvalidProfessionalSpecializationException.class)
    public void addSpeakerWitInvalidSpecialization() {
        final Appointment nextWeekAppointment = generateAppointment(LocalDateTime.now().plusDays(7));
        appointmentProvider.addSpeaker(nextWeekAppointment, OTHER_PRACTITIONER_ID, null);
    }


    @AfterClass
    public void cleanUpDatabase() {
        appointmentProvider.deleteAll();
        examinationTypeProvider.deleteAll();
        professionalSpecializationProvider.deleteAll();
        appointmentTypeProvider.deleteAll();
    }
}
