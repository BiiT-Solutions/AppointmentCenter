package com.biit.appointment.core.test;

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
    private final static Long ORGANIZATION_ID = 43L;
    private final static UUID ORGANIZER = UUID.randomUUID();
    private final static int TEMPLATE_DURATION = 90;
    private final static double TEMPLATE_COST = 100D;

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
        AppointmentTemplate appointmentTemplate = new AppointmentTemplate();
        appointmentTemplate.setCost(TEMPLATE_COST);
        appointmentTemplate.setDuration(TEMPLATE_DURATION);
        appointmentTemplate.setOrganizationId(ORGANIZATION_ID);
        appointmentTemplate.setTitle(APPOINTMENT_TITLE);
        appointmentTemplate.setDescription(APPOINTMENT_DESCRIPTION);
        appointmentTemplate.setExaminationType(type);
        appointmentTemplate.setSpeakers(SPEAKERS);

        this.appointmentTemplate = appointmentTemplateProvider.save(appointmentTemplate);
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
