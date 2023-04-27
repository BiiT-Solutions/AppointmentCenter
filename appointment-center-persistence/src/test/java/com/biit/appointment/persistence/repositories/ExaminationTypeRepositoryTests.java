package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ExaminationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
@Test(groups = {"examinationTypeRepository"})
public class ExaminationTypeRepositoryTests extends AbstractTestNGSpringContextTests {

    private static final String TEST_NAME = "normal";
    private static final String TEST_TRANSLATION = "none";

    private static final String APPOINTMENT_SPECIALTY = "Physical";
    private static final long ORGANIZATION_ID = 456l;

    @Autowired
    private ExaminationTypeRepository examinationTypeRepository;

    @Autowired
    private AppointmentTypeRepository appointmentTypeRepository;

    public static ExaminationType generateExaminationType(String name, AppointmentType appointmentType) {
        return new ExaminationType(name, TEST_TRANSLATION, ORGANIZATION_ID, appointmentType);
    }

    @BeforeClass
    public void prepareData() {
        final AppointmentType appointmentType = new AppointmentType(APPOINTMENT_SPECIALTY, ORGANIZATION_ID);
        appointmentTypeRepository.save(appointmentType);
    }

    @Test
    public void storeEntity() {
        Assert.assertTrue(examinationTypeRepository.findAll().isEmpty());
        AppointmentType appointmentType = appointmentTypeRepository.findByNameAndOrganizationId(APPOINTMENT_SPECIALTY, ORGANIZATION_ID).orElseThrow();
        ExaminationType type = generateExaminationType(TEST_NAME, appointmentType);
        examinationTypeRepository.save(type);
        Assert.assertFalse(examinationTypeRepository.findAll().isEmpty());
    }

    @Test(dependsOnMethods = {"storeEntity"})
    public void searchEntity() {
        Assert.assertFalse(examinationTypeRepository.findAll().isEmpty());
        ExaminationType type = examinationTypeRepository.findByNameAndDeleted(TEST_NAME, false).get(0);
        Assert.assertEquals(type.getName(), TEST_NAME);
    }

    @Test(dependsOnMethods = {"storeEntity"})
    public void searchEntityWithSpecialty() {
        Assert.assertFalse(examinationTypeRepository.findAll().isEmpty());
        AppointmentType appointmentType = appointmentTypeRepository.findByNameAndOrganizationId(APPOINTMENT_SPECIALTY, ORGANIZATION_ID).orElseThrow();
        List<ExaminationType> types = examinationTypeRepository.findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(ORGANIZATION_ID,
                appointmentType, false);
        Assert.assertEquals(types.size(), 1);
        Assert.assertEquals(types.iterator().next().getName(), TEST_NAME);
    }

    @Test(dependsOnMethods = {"storeEntity"})
    public void searchEntityWithSpecialties() {
        Assert.assertFalse(examinationTypeRepository.findAll().isEmpty());
        AppointmentType appointmentType = appointmentTypeRepository.findByNameAndOrganizationId(APPOINTMENT_SPECIALTY, ORGANIZATION_ID).orElseThrow();

        Set<AppointmentType> appointmentTypes = new HashSet<>();
        List<ExaminationType> types = examinationTypeRepository.findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(ORGANIZATION_ID,
                appointmentTypes, false);
        Assert.assertEquals(types.size(), 0);

        appointmentTypes.add(appointmentType);
        types = examinationTypeRepository.findAllByOrOrganizationIdAndAppointmentTypeInAndDeleted(ORGANIZATION_ID, appointmentTypes, false);
        Assert.assertEquals(types.size(), 1);
        Assert.assertEquals(types.iterator().next().getName(), TEST_NAME);
    }

    @Test(dependsOnMethods = {"searchEntity"})
    public void searchInactiveEntity() {
        ExaminationType type = examinationTypeRepository.findByNameAndDeleted(TEST_NAME, false).get(0);
        type.setDeleted(true);
        examinationTypeRepository.save(type);
        Assert.assertTrue(examinationTypeRepository.findByNameAndDeleted(TEST_NAME, false).isEmpty());
    }

    @Test(dependsOnMethods = {"searchInactiveEntity"})
    public void removeEntity() {
        Assert.assertFalse(examinationTypeRepository.findAll().isEmpty());
        //It is already marked as deleted on the previous test.
        ExaminationType type = examinationTypeRepository.findByNameAndOrganizationIdAndDeleted(TEST_NAME, ORGANIZATION_ID, true).orElseThrow();
        examinationTypeRepository.delete(type);
        Assert.assertTrue(examinationTypeRepository.findAll().isEmpty());
    }

    @AfterClass
    public void cleanUp() {
        examinationTypeRepository.deleteAll();
        appointmentTypeRepository.deleteAll();
    }

}
