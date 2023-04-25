package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.AppointmentTestUtils;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentStatus;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ExaminationType;
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
import java.util.List;

import static com.biit.appointment.persistence.repositories.ExaminationTypeRepositoryTests.generateExaminationType;

@SpringBootTest
@Test(groups = {"appointmentRepository"})
public class AppointmentRepositoryTests extends AbstractTestNGSpringContextTests {

    private static final String FORM_FILE = "USMO_Physiotherapist.json";
    private static final String FORM_NAME = "USMO_Physiotherapist";
    private static final long ORGANIZER_ID = 123456l;
    private static final long ORGANIZATION_ID = 456l;
    private static final String APPOINTMENT_SPECIALTY = "Physical";

    // Timestamp without nanoseconds.
    private static final long END_TIME_MINUTES_INCREMENT = 5;
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

    private static final LocalDateTime START_TIME_1 = CURRENT_TIME;

    private static final LocalDateTime START_TIME_2 = CURRENT_TIME.plusMinutes(10);
    private static final LocalDateTime OVERLAP_START_TIME_2 = START_TIME_2.plusMinutes(END_TIME_MINUTES_INCREMENT / 3);
    private static final LocalDateTime NO_OVERLAP_START_TIME_2 = START_TIME_2.minusMinutes(END_TIME_MINUTES_INCREMENT / 3);

    private static final LocalDateTime START_TIME_3 = CURRENT_TIME.plusMinutes(20);
    private static final LocalDateTime START_TIME_4 = CURRENT_TIME.plusMinutes(30);
    private static final LocalDateTime START_TIME_5 = CURRENT_TIME.plusMinutes(50);
    private static final LocalDateTime START_TIME_6 = CURRENT_TIME.plusMinutes(40);
    private static final LocalDateTime START_TIME_7 = CURRENT_TIME.plusMinutes(45);
    private static final LocalDateTime START_TIME_CANCELLED = CURRENT_TIME.plusMinutes(55);

    private static final String LONG_TEXT_INFO = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
            + "Sed massa sem, suscipit sed iaculis ac, tincidunt eu nisi. Praesent bibendum est a metus cursus laoreet id ut tellus. "
            + "Suspendisse at tellus a mi congue viverra a a nulla. Morbi fringilla fermentum maximus. "
            + "Duis sed faucibus lorem, id faucibus sem. Sed ultricies, augue tristique mattis facilisis, erat lorem hendrerit velit, "
            + "quis interdum mauris libero eu purus. Etiam sodales accumsan nulla eget dapibus. Mauris interdum, ante rhoncus molestie posuere, "
            + "lectus tortor mattis enim, sed dapibus enim odio eu urna.";


    private static final LocalDateTime LONG_TIME_4 = CURRENT_TIME.plusHours(1000);

    private static final String TEST_NAME = "basic";
    private static final String TEST_OVERLAPS_NAME = "basicOverlaps";

    @Autowired
    private AppointmentRepository appointmentRepository;


    @Autowired
    private ExaminationTypeRepository examinationTypeRepository;

    @Autowired
    private AppointmentTypeRepository appointmentTypeRepository;

    private ExaminationType type;
    private ExaminationType typeAllowsOverlaps;

    private Long patientId = 42L;

    @BeforeClass
    public void prepareData() {
        final AppointmentType appointmentType = new AppointmentType(APPOINTMENT_SPECIALTY, ORGANIZATION_ID);
        appointmentTypeRepository.save(appointmentType);
    }


    @BeforeClass(dependsOnMethods = "prepareData")
    public void storeExaminationTypeForAppointment() {
        Assert.assertEquals(examinationTypeRepository.count(), 0);
        AppointmentType appointmentType = appointmentTypeRepository.findByNameAndOrganizationId(APPOINTMENT_SPECIALTY, ORGANIZATION_ID).orElseThrow();
        type = generateExaminationType(TEST_NAME, appointmentType);
        type = examinationTypeRepository.save(type);
        Assert.assertEquals(examinationTypeRepository.count(), 1);

        typeAllowsOverlaps = generateExaminationType(TEST_OVERLAPS_NAME, appointmentType);
        typeAllowsOverlaps.setAppointmentOverlapsAllowed(true);
        typeAllowsOverlaps = examinationTypeRepository.save(typeAllowsOverlaps);
        Assert.assertEquals(examinationTypeRepository.count(), 2);
    }


    @Test(groups = {"appointmentRepository"})
    public void storeEntity() {
        Assert.assertNotNull(patientId);
        Assert.assertNotNull(type);

        Assert.assertEquals(appointmentRepository.count(), 0);
        // Create appointment with examination result
        Appointment appointment = AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, START_TIME_1, type,
                patientId);
        // appointment.addExaminationResult(AppointmentTestUtils.createExaminationResult(examinationForm));

        appointment = appointmentRepository.save(appointment);

        Assert.assertNotNull(appointment.getId());
        Assert.assertEquals(appointmentRepository.count(), 1);
        Assert.assertNotNull(appointmentRepository.findByOrganizerId(ORGANIZER_ID));
        Assert.assertNotNull(appointmentRepository.getById(appointment.getId()));
    }

    @Test(groups = {"appointmentRepository"}, dependsOnMethods = {"storeEntity"})
    public void getAppointmentsByPatient() {
        Assert.assertEquals(appointmentRepository.countByCustomerId(patientId), 1);
        Assert.assertEquals(appointmentRepository.countByCustomerId(-1L), 0);
    }

    @Test(groups = {"appointmentRepository"}, dependsOnMethods = {"storeEntity"})
    public void retrieveEntity() {
        Assert.assertEquals(appointmentRepository.count(), 1);
        List<Appointment> appointments = appointmentRepository.findByOrganizerId(ORGANIZER_ID);
        Assert.assertNotNull(appointments);
        Assert.assertFalse(appointments.isEmpty());
        Appointment appointment = appointments.get(0);
        Assert.assertNotNull(appointment);
        Assert.assertEquals(appointment.getStartTime(), START_TIME_1);
        Assert.assertEquals(appointment.getEndTime(), START_TIME_1.plusMinutes(END_TIME_MINUTES_INCREMENT));
        Assert.assertEquals(appointment.getCustomerId(), patientId);
    }

    @Test(groups = {"appointmentRepository"}, dependsOnMethods = {"storeEntity"})
    public void retrieveAllEntities() {
        List<Appointment> appointments = appointmentRepository.findAll();
        Assert.assertNotNull(appointments);
        Assert.assertEquals(appointments.size(), 1);
        Assert.assertEquals(appointments.get(0).getStartTime(), START_TIME_1);
        Assert.assertEquals(appointments.get(0).getEndTime(), START_TIME_1.plusMinutes(END_TIME_MINUTES_INCREMENT));
        Assert.assertEquals(appointmentRepository.count(), 1);

        // Check get All with filters;
        Assert.assertEquals(appointmentRepository.findAll(
                ORGANIZATION_ID, ORGANIZER_ID, (ExaminationType) null, null, null, null, null).size(), 1);
        Assert.assertEquals(appointmentRepository.findAll(
                ORGANIZATION_ID, ORGANIZER_ID, type, null, null, null, null).size(), 1);
        Assert.assertEquals(appointmentRepository.findAll(
                ORGANIZATION_ID, ORGANIZER_ID, type, AppointmentStatus.NOT_STARTED, null, null, null).size(), 1);
        Assert.assertEquals(appointmentRepository.findAll(
                ORGANIZATION_ID, ORGANIZER_ID, type, AppointmentStatus.REPORT_CLOSED, null, null, null).size(), 0);
        Assert.assertEquals(appointmentRepository.findAll(
                ORGANIZATION_ID, ORGANIZER_ID, type, AppointmentStatus.NOT_STARTED, START_TIME_1, null, null).size(), 1);
        Assert.assertEquals(appointmentRepository.findAll(
                ORGANIZATION_ID, ORGANIZER_ID, type, AppointmentStatus.NOT_STARTED, START_TIME_1, START_TIME_1.plusMinutes(END_TIME_MINUTES_INCREMENT), null).size(), 1);
        Assert.assertEquals(appointmentRepository.findAll(
                ORGANIZATION_ID, ORGANIZER_ID, type, AppointmentStatus.NOT_STARTED, null, START_TIME_1.plusMinutes(END_TIME_MINUTES_INCREMENT), null).size(), 1);

        // Check rowcount with filters
        Assert.assertEquals(appointmentRepository.count(
                ORGANIZATION_ID, null, (ExaminationType) null, null, START_TIME_1, START_TIME_1.plusMinutes(END_TIME_MINUTES_INCREMENT), false), 1);
        Assert.assertEquals(appointmentRepository.count(
                ORGANIZATION_ID, null, (ExaminationType) null, null, null, null, false), 1);
        Assert.assertEquals(
                appointmentRepository.count(
                        null, null, (ExaminationType) null, null, null, null, false),
                1);
        Assert.assertEquals(appointmentRepository.count(
                ORGANIZATION_ID, null, Arrays.asList(type), null, START_TIME_1, START_TIME_1.plusMinutes(END_TIME_MINUTES_INCREMENT), false), 1);

        // Check rowcount with filters
        Assert.assertEquals(appointmentRepository.count(
                ORGANIZATION_ID, null, (ExaminationType) null, null, START_TIME_1, START_TIME_1.plusMinutes(END_TIME_MINUTES_INCREMENT), false), 1);
        Assert.assertEquals(appointmentRepository.count(
                ORGANIZATION_ID, null, (ExaminationType) null, null, null, null, false), 1);
        Assert.assertEquals(appointmentRepository.count(
                null, null, (ExaminationType) null, null, null, null, false), 1);
        Assert.assertEquals(appointmentRepository.count(
                ORGANIZATION_ID, null, Arrays.asList(type), null, START_TIME_1, START_TIME_1.plusMinutes(END_TIME_MINUTES_INCREMENT), false), 1);
        Assert.assertEquals(appointmentRepository.count(
                ORGANIZATION_ID, null, Arrays.asList(type), AppointmentStatus.NOT_STARTED, START_TIME_1, START_TIME_1.plusMinutes(END_TIME_MINUTES_INCREMENT), false), 1);
        Assert.assertEquals(appointmentRepository.count(
                ORGANIZATION_ID, null, Arrays.asList(type), AppointmentStatus.STARTED, START_TIME_1, START_TIME_1.plusMinutes(END_TIME_MINUTES_INCREMENT), false), 0);
        Assert.assertEquals(appointmentRepository.count(
                ORGANIZATION_ID, null, (List<ExaminationType>) null, AppointmentStatus.STARTED, START_TIME_1, START_TIME_1.plusMinutes(END_TIME_MINUTES_INCREMENT), false), 0);

        // NOTE This test will fail with older versions of MYSQL!
        Assert.assertEquals(appointmentRepository.findAll(
                ORGANIZATION_ID, ORGANIZER_ID, type, AppointmentStatus.NOT_STARTED, START_TIME_1.plusMinutes(END_TIME_MINUTES_INCREMENT + 1), null, null).size(), 0);
    }

    @Test(groups = {"appointmentRepository"}, dependsOnMethods = {"retrieveAllEntities"})
    public void checkIfOverlapsByStartTime() {
        Appointment appointment = appointmentRepository
                .save(AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, START_TIME_2, type, patientId));
        Assert.assertTrue(appointmentRepository.overlaps(
                AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, OVERLAP_START_TIME_2,
                        OVERLAP_START_TIME_2.plusMinutes(END_TIME_MINUTES_INCREMENT), type, patientId)) > 0);
        appointmentRepository.delete(appointment);
    }

    @Test(groups = {"appointmentRepository"}, dependsOnMethods = {"retrieveAllEntities"})
    public void checkIfOverlapsByStartTimeDifferentDoctor() {
        Appointment appointment = appointmentRepository
                .save(AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, START_TIME_2, type, patientId));
        // Different organizers, then false.
        Assert.assertFalse(appointmentRepository.overlaps(
                AppointmentTestUtils.createAppointment(ORGANIZER_ID + 1, ORGANIZATION_ID, OVERLAP_START_TIME_2,
                        OVERLAP_START_TIME_2.plusMinutes(END_TIME_MINUTES_INCREMENT), type, patientId)) > 0);
        appointmentRepository.delete(appointment);
    }

    @Test(groups = {"appointmentRepository"}, dependsOnMethods = {"retrieveAllEntities"})
    public void checkIfOverlapsByEndTime() {
        Appointment appointment = appointmentRepository
                .save(AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, START_TIME_2, type, patientId));
        Assert.assertTrue(appointmentRepository.overlaps(
                AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, NO_OVERLAP_START_TIME_2,
                        NO_OVERLAP_START_TIME_2.plusMinutes(END_TIME_MINUTES_INCREMENT), type, patientId)) > 0);
        appointmentRepository.delete(appointment);
    }

    @Test(groups = {"appointmentRepository"}, dependsOnMethods = {"retrieveAllEntities"})
    public void checkNoOverlaps() {
        Appointment appointment = appointmentRepository
                .save(AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, START_TIME_3, type, patientId));
        Assert.assertFalse(appointmentRepository.overlaps(
                AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, NO_OVERLAP_START_TIME_2,
                        NO_OVERLAP_START_TIME_2.plusMinutes(END_TIME_MINUTES_INCREMENT), type, patientId)) > 0);
        appointmentRepository.delete(appointment);
    }

    @Test(groups = {"appointmentRepository"}, dependsOnMethods = {"retrieveAllEntities"})
    public void checkOverlapsButAllowed() {
        Appointment appointment = appointmentRepository.save(AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, START_TIME_5,
                typeAllowsOverlaps, patientId));
        Assert.assertFalse(appointmentRepository.overlaps(AppointmentTestUtils.createAppointment(ORGANIZER_ID,
                ORGANIZATION_ID, START_TIME_5, typeAllowsOverlaps, patientId)) > 0);
        appointmentRepository.delete(appointment);
    }

    @Test(groups = {"appointmentRepository"}, dependsOnMethods = {"retrieveAllEntities"})
    public void checkOverlapsButOnlyOneAllowed() {
        Appointment appointment = appointmentRepository
                .save(AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, START_TIME_6, type, patientId));
        Assert.assertTrue(appointmentRepository.overlaps(AppointmentTestUtils.createAppointment(ORGANIZER_ID,
                ORGANIZATION_ID, START_TIME_6, typeAllowsOverlaps, patientId)) > 0);
        appointmentRepository.delete(appointment);
    }

    @Test(groups = {"appointmentRepository"}, dependsOnMethods = {"retrieveAllEntities"})
    public void checkOverlapsButOtherOneAllowed() {
        Appointment appointment = appointmentRepository.save(AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, START_TIME_7,
                typeAllowsOverlaps, patientId));
        Assert.assertTrue(appointmentRepository.overlaps(
                AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, START_TIME_7, type, patientId)) > 0);
        appointmentRepository.delete(appointment);
    }

    @Test(groups = {"appointmentRepository"}, dependsOnMethods = {"retrieveAllEntities"})
    public void checkOverlapsCancelledAppointment() {
        Appointment appointment = AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID,
                START_TIME_CANCELLED, OVERLAP_START_TIME_2.plusMinutes(END_TIME_MINUTES_INCREMENT), type,
                patientId);
        appointment = appointmentRepository.save(appointment);
        Assert.assertTrue(appointmentRepository.overlaps(
                AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, START_TIME_CANCELLED,
                        OVERLAP_START_TIME_2.plusMinutes(END_TIME_MINUTES_INCREMENT), type, patientId)) > 0);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        Assert.assertFalse(appointmentRepository.overlaps(
                AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, START_TIME_CANCELLED,
                        OVERLAP_START_TIME_2.plusMinutes(END_TIME_MINUTES_INCREMENT), type, patientId)) > 0);
        appointmentRepository.delete(appointment);
    }

    @Test(groups = {"appointmentRepository"}, dependsOnMethods = {"retrieveAllEntities"})
    public void searchNextWorkshop() {
        AppointmentType appointmentType = appointmentTypeRepository.findByNameAndOrganizationId(APPOINTMENT_SPECIALTY, ORGANIZATION_ID).orElseThrow();
        ExaminationType workshopType = generateExaminationType(TEST_NAME, appointmentType);
        workshopType.setAppointmentOverlapsAllowed(true);
        workshopType = examinationTypeRepository.save(workshopType);

        // Next workshop in 1 week
        LocalDateTime workshop1StartsInTime = CURRENT_TIME.plusDays(7);

        // Create first workshop on the future
        Appointment workshop1 = AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, workshop1StartsInTime,
                workshopType, patientId);
        workshop1 = appointmentRepository.save(workshop1);

        // Create second workshop later on the future
        LocalDateTime workshop2StartsInTime = CURRENT_TIME.plusDays(14);
        Appointment workshop2 = AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, workshop2StartsInTime,
                workshopType, patientId);
        appointmentRepository.save(workshop2);

        Assert.assertEquals(appointmentRepository.getNext(ORGANIZATION_ID, workshopType).get(0), workshop1);

        appointmentRepository.delete(workshop1);
        appointmentRepository.delete(workshop2);
    }

    @Test(groups = {"appointmentRepository"}, dependsOnMethods = {"retrieveAllEntities"})
    public void checkBigAppointment() {
        Appointment appointment = appointmentRepository
                .save(AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, START_TIME_2, type, patientId));
        Assert.assertTrue(appointmentRepository.overlaps(
                AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, START_TIME_2.minusMinutes(END_TIME_MINUTES_INCREMENT),
                        START_TIME_2.plusMinutes(2 * END_TIME_MINUTES_INCREMENT), type, patientId)) > 0);
        appointmentRepository.delete(appointment);
    }

    @Test(groups = {"appointmentRepository"}, dependsOnMethods = {"checkBigAppointment",
            "checkOverlapsCancelledAppointment"})
    public void checkSuggestedAppointments() {
        long previousAppointments = appointmentRepository.count(ORGANIZATION_ID, ORGANIZER_ID,
                (List<ExaminationType>) null, null, START_TIME_1, LONG_TIME_4, false);
        long totalAppointments = appointmentRepository.count();
        appointmentRepository.save(AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, null, type, patientId));
        Assert.assertEquals(appointmentRepository.count(), totalAppointments + 1);
        Assert.assertEquals(appointmentRepository.count(ORGANIZATION_ID, ORGANIZER_ID, (List<ExaminationType>) null, null,
                START_TIME_1, LONG_TIME_4, false), previousAppointments + 1);

        Assert.assertEquals(appointmentRepository.findAll(ORGANIZATION_ID, ORGANIZER_ID, null, null,
                START_TIME_1, LONG_TIME_4, false).size(), previousAppointments + 1);
    }

    @Test(groups = {"appointmentRepository"}, dependsOnMethods = {"retrieveAllEntities", "getAppointmentsByPatient"})
    public void checkEditedAppointment() {
        Appointment appointment = AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, START_TIME_2, type,
                patientId);
        appointment = appointmentRepository.save(appointment);
        Assert.assertFalse(appointmentRepository.overlaps(appointment) > 0);
        appointmentRepository.delete(appointment);
    }

//    @Test(groups = {"appointmentRepository"}, dependsOnMethods = {"retrieveAllEntities"})
//    public void addAnamneseExtraInfo() {
//        Appointment appointment = AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, START_TIME_4, type,
//                patientId);
//        AnamneseExtra extra = new AnamneseExtra(LONG_TEXT_INFO);
//        appointment.setAnamneseExtra(extra);
//
//        appointment = appointmentRepository.save(appointment);
//
//        Assert.assertEquals(anamneseExtraRepository.count(), 1);
//
//        appointmentRepository.delete(appointment);
//
//        Assert.assertEquals(anamneseExtraRepository.count(), 0);
//    }

    @Test(groups = {"appointmentRepository"}, dependsOnMethods = {"retrieveAllEntities"})
    public void isAppointmentModified()
            throws InterruptedException {
        Appointment appointment = AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID, START_TIME_4, type,
                patientId);

        appointment = appointmentRepository.save(appointment);
        Appointment dbAppointment = appointmentRepository.findById(appointment.getId()).orElseThrow();
        Appointment dbAppointment2 = appointmentRepository.findById(appointment.getId()).orElseThrow();

        Assert.assertEquals(dbAppointment.getUpdatedAt(), dbAppointment2.getUpdatedAt());
        Assert.assertFalse(dbAppointment.isUpdated());

        Thread.sleep(1000);
        //Force a change on the update.
        dbAppointment2.setCustomerId(11L);
        dbAppointment2 = appointmentRepository.save(dbAppointment2);

        Assert.assertTrue(dbAppointment2.getUpdatedAt().isAfter(dbAppointment.getUpdatedAt()));
        Assert.assertTrue(dbAppointment2.isUpdated());

        appointmentRepository.delete(dbAppointment2);
    }

    @Test(groups = {"appointmentRepository"})
    public void getPreviousAppointment() {
        Appointment appointment1 = AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID,
                LONG_TIME_4, type, patientId);
        appointment1 = appointmentRepository.save(appointment1);
        Appointment appointment2 = AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID,
                LONG_TIME_4.minusSeconds(200), type, patientId);
        appointment2 = appointmentRepository.save(appointment2);
        Appointment appointment3 = AppointmentTestUtils.createAppointment(ORGANIZER_ID, ORGANIZATION_ID,
                LONG_TIME_4.minusSeconds(500), type, patientId);
        appointment3 = appointmentRepository.save(appointment3);

        Assert.assertEquals(appointmentRepository.getPrevious(appointment1).get(0), appointment2);
        Assert.assertEquals(appointmentRepository.getPrevious(appointment2).get(0), appointment3);
        Assert.assertTrue(appointmentRepository.getPrevious(appointment3).isEmpty());

        // Change starting time of third appointment
        appointment3.setStartTime(LONG_TIME_4.minusSeconds(100));
        appointment3 = appointmentRepository.save(appointment3);
        // With Transactional.NEVER the get previous returns the appointment with the
        // wrong time and equals fails.
        Appointment previousAppointment = appointmentRepository.getPrevious(appointment1).get(0);
        Assert.assertEquals(previousAppointment, appointment3);

        appointmentRepository.delete(appointment1);
        appointmentRepository.delete(appointment2);
        appointmentRepository.delete(appointment3);
    }

    @AfterClass(alwaysRun = true)
    public void clearDatabase() {
        appointmentRepository.deleteAll();
        examinationTypeRepository.deleteAll();
        appointmentTypeRepository.deleteAll();
    }

}
