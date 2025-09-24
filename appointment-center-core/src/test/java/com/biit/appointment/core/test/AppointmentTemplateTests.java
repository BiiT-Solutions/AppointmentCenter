package com.biit.appointment.core.test;

import com.biit.appointment.core.exceptions.AppointmentTemplateAlreadyExistsException;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.core.providers.AppointmentTemplateProvider;
import com.biit.appointment.core.providers.AppointmentTypeProvider;
import com.biit.appointment.core.providers.ExaminationTypeProvider;
import com.biit.appointment.core.providers.ProfessionalSpecializationProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentTemplate;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.entities.ProfessionalSpecialization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SpringBootTest
@Test(groups = {"appointmentTemplateTest"})
public class AppointmentTemplateTests extends AbstractTestNGSpringContextTests {
    private static final String ORGANIZATION_ID = "The Organization";
    private static final UUID ORGANIZER = UUID.randomUUID();
    private static final UUID ATTENDER = UUID.randomUUID();
    private static final int TEMPLATE_DURATION = 90;
    private static final double TEMPLATE_COST = 100D;

    private static final String APPOINTMENT_SPECIALTY = "Physical";
    private static final String APPOINTMENT_TITLE = "The Template";
    private static final String APPOINTMENT_DESCRIPTION = "Template Description";

    private static final String TEST_TYPE_NAME = "basic";

    private static final Set<UUID> SPEAKERS = new HashSet<>(Arrays.asList(UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));

    @Autowired
    private AppointmentProvider appointmentProvider;

    @Autowired
    private AppointmentTypeProvider appointmentTypeProvider;

    @Autowired
    private ExaminationTypeProvider examinationTypeProvider;

    @Autowired
    private ProfessionalSpecializationProvider professionalSpecializationProvider;

    @Autowired
    private AppointmentTemplateProvider appointmentTemplateProvider;

    private ExaminationType type;

    private AppointmentTemplate appointmentTemplate;


    public static ExaminationType generateExaminationType(String name, AppointmentType appointmentType) {
        return new ExaminationType(name, ORGANIZATION_ID, appointmentType);
    }

    @BeforeClass
    public void generateExaminationType() {
        AppointmentType appointmentType = new AppointmentType(APPOINTMENT_SPECIALTY, ORGANIZATION_ID);
        appointmentType = appointmentTypeProvider.save(appointmentType);
        type = generateExaminationType(TEST_TYPE_NAME, appointmentType);
        type = examinationTypeProvider.save(type);
    }


    @BeforeClass
    public void generateProfessionalSpecialization() {
        professionalSpecializationProvider.save(new ProfessionalSpecialization(type.getAppointmentType().getName(), type.getAppointmentType(), ORGANIZATION_ID, ORGANIZER));
    }


    @Test
    public void generateAppointmentTemplate() {
        final AppointmentTemplate generatedAppointmentTemplate = new AppointmentTemplate();
        generatedAppointmentTemplate.setCost(TEMPLATE_COST);
        generatedAppointmentTemplate.setDuration(TEMPLATE_DURATION);
        generatedAppointmentTemplate.setOrganizationId(ORGANIZATION_ID);
        generatedAppointmentTemplate.setTitle(APPOINTMENT_TITLE);
        generatedAppointmentTemplate.setDescription(APPOINTMENT_DESCRIPTION);
        generatedAppointmentTemplate.setExaminationType(type);
        generatedAppointmentTemplate.setSpeakers(SPEAKERS);

        this.appointmentTemplate = appointmentTemplateProvider.save(generatedAppointmentTemplate);
    }


    @Test(dependsOnMethods = "generateAppointmentTemplate", expectedExceptions = AppointmentTemplateAlreadyExistsException.class)
    public void generateDuplicatedAppointmentTemplate() {
        final AppointmentTemplate generatedAppointmentTemplate = new AppointmentTemplate();
        generatedAppointmentTemplate.setOrganizationId(ORGANIZATION_ID);
        generatedAppointmentTemplate.setTitle(APPOINTMENT_TITLE);
        this.appointmentTemplate = appointmentTemplateProvider.save(generatedAppointmentTemplate);
    }


    @Test(dependsOnMethods = "generateAppointmentTemplate")
    public void generateAppointmentFromTemplate() {
        Appointment appointment = appointmentProvider.create(appointmentTemplate, LocalDateTime.now(), ORGANIZER, null);
        Assert.assertEquals(appointment.getEndTime().truncatedTo(ChronoUnit.MINUTES), LocalDateTime.now().plusMinutes(TEMPLATE_DURATION).truncatedTo(ChronoUnit.MINUTES));
        Assert.assertEquals(appointment.getCost(), TEMPLATE_COST);
        Assert.assertEquals(appointment.getTitle(), APPOINTMENT_TITLE);
        Assert.assertEquals(appointment.getDescription(), APPOINTMENT_DESCRIPTION);
        Assert.assertEquals(appointment.getOrganizationId(), ORGANIZATION_ID);
        Assert.assertEquals(appointment.getExaminationType(), type);

        //Add one attendee
        appointment.addAttendee(ATTENDER);
        appointmentProvider.save(appointment);
    }


    @Test(dependsOnMethods = "generateAppointmentFromTemplate")
    public void findTemplatesByAttendees() {
        Assert.assertEquals(appointmentTemplateProvider.findByAttendeeOnAppointment(ATTENDER).size(), 1);
        Assert.assertEquals(appointmentTemplateProvider.findByAttendeeOnAppointment(ORGANIZER).size(), 0);
    }

    @Test(dependsOnMethods = "generateAppointmentFromTemplate")
    public void findTemplatesByNonAttendees() {
        Assert.assertEquals(appointmentTemplateProvider.findByNonAttendeeOnAppointment(ATTENDER).size(), 0);
        Assert.assertEquals(appointmentTemplateProvider.findByNonAttendeeOnAppointment(ORGANIZER).size(), 1);
    }

    @AfterClass
    public void cleanDatabase() {
        appointmentProvider.deleteAll();
        appointmentTemplateProvider.deleteAll();
        professionalSpecializationProvider.deleteAll();
        examinationTypeProvider.deleteAll();
        appointmentTypeProvider.deleteAll();
    }
}
