package com.biit.appointment.core.test;

import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.core.providers.AppointmentTypeProvider;
import com.biit.appointment.core.providers.ExaminationTypeProvider;
import com.biit.appointment.core.providers.RecurrenceProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.entities.Recurrence;
import com.biit.appointment.persistence.entities.RecurrenceFrequency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
@Test(groups = {"recurrenceTest"})
public class RecurrenceTest extends AbstractTestNGSpringContextTests {
    private final static Long ORGANIZER_ID = 32L;
    private final static Long ORGANIZATION_ID = 43L;

    private static final String APPOINTMENT_SPECIALTY = "Physical";

    private static final String TEST_TYPE_NAME = "basic";

    private static final Set<Long> ATTENDEES = new HashSet<>(Arrays.asList(1L, 2L, 3L, 4L, 5L));

    @Autowired
    private AppointmentProvider appointmentProvider;

    @Autowired
    private AppointmentTypeProvider appointmentTypeProvider;

    @Autowired
    private ExaminationTypeProvider examinationTypeProvider;

    @Autowired
    private RecurrenceProvider recurrenceProvider;

    private ExaminationType type;

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

    private Recurrence generateRecurrence(Appointment appointment, LocalDateTime startsAt, LocalDateTime endsAt, RecurrenceFrequency frequency) {
        final Recurrence recurrence = new Recurrence();
        recurrence.setOrganizationId(ORGANIZATION_ID);
        recurrence.setOrganizerId(ORGANIZER_ID);
        recurrence.addAppointment(appointment);
        recurrence.setStartsAt(startsAt);
        recurrence.setEndsAt(endsAt);
        recurrence.setFrequency(frequency);
        return recurrenceProvider.save(recurrence);
    }


    @BeforeClass
    public void generateExaminationType() {
        AppointmentType appointmentType = new AppointmentType(APPOINTMENT_SPECIALTY, ORGANIZATION_ID);
        appointmentType = appointmentTypeProvider.save(appointmentType);
        type = generateExaminationType(TEST_TYPE_NAME, appointmentType);
        type = examinationTypeProvider.save(type);
    }

    @AfterMethod
    public void cleanDatabase() {
        appointmentProvider.deleteAll();
        recurrenceProvider.deleteAll();
    }


    @Test
    public void dailyRecurrence() {
        Appointment pastWeekAppointment = generateAppointment(LocalDateTime.now().minusDays(7));
        generateRecurrence(pastWeekAppointment, LocalDateTime.now().minusDays(7), LocalDateTime.now().plusDays(7), RecurrenceFrequency.DAILY);
        List<Appointment> appointments = appointmentProvider.findBy(null, null, null, null, null,
                LocalDateTime.now(), LocalDateTime.now().plusDays(7), null);
        Assert.assertEquals(appointments.size(), 8);
    }

    @Test
    public void invalidDailyRecurrence() {
        Appointment pastWeekAppointment = generateAppointment(LocalDateTime.now().minusDays(7));
        generateRecurrence(pastWeekAppointment, LocalDateTime.now().minusDays(7), LocalDateTime.now().plusDays(7), RecurrenceFrequency.DAILY);
        List<Appointment> appointments = appointmentProvider.findBy(null, null, null, null, null,
                LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(8), null);
        Assert.assertEquals(appointments.size(), 0);
    }

    @Test
    public void weeklyRecurrence() {
        Appointment pastWeekAppointment = generateAppointment(LocalDateTime.now().minusDays(7));
        generateRecurrence(pastWeekAppointment, LocalDateTime.now().minusDays(7), LocalDateTime.now().plusDays(7), RecurrenceFrequency.WEEKLY);
        List<Appointment> appointments = appointmentProvider.findBy(null, null, null, null, null,
                LocalDateTime.now(), LocalDateTime.now().plusDays(7), null);
        Assert.assertEquals(appointments.size(), 2);
    }

    @Test
    public void invalidWeeklyRecurrence() {
        Appointment pastWeekAppointment = generateAppointment(LocalDateTime.now().minusDays(1));
        generateRecurrence(pastWeekAppointment, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(5), RecurrenceFrequency.WEEKLY);
        List<Appointment> appointments = appointmentProvider.findBy(null, null, null, null, null,
                LocalDateTime.now().minusDays(10), LocalDateTime.now().plusDays(10), null);
        //Has only the original appointment.
        Assert.assertEquals(appointments.size(), 1);
    }

    @Test
    public void monthlyRecurrence() {
        Appointment pastWeekAppointment = generateAppointment(LocalDateTime.now().minusDays(35));
        generateRecurrence(pastWeekAppointment, LocalDateTime.now().minusDays(35), LocalDateTime.now().minusDays(2), RecurrenceFrequency.MONTHLY);
        List<Appointment> appointments = appointmentProvider.findBy(null, null, null, null, null,
                LocalDateTime.now().minusDays(7), LocalDateTime.now().minusDays(2), null);
        Assert.assertEquals(appointments.size(), 1);
    }

    @Test
    public void invalidMonthlyRecurrence() {
        Appointment pastWeekAppointment = generateAppointment(LocalDateTime.now().minusDays(35));
        generateRecurrence(pastWeekAppointment, LocalDateTime.now().minusDays(35), LocalDateTime.now().minusDays(2), RecurrenceFrequency.MONTHLY);
        List<Appointment> appointments = appointmentProvider.findBy(null, null, null, null, null,
                LocalDateTime.now(), LocalDateTime.now().plusDays(100), null);
        Assert.assertEquals(appointments.size(), 0);
    }

    @Test
    public void yearlyRecurrence() {
        Appointment pastWeekAppointment = generateAppointment(LocalDateTime.now().minusYears(5).minusDays(1));
        generateRecurrence(pastWeekAppointment, LocalDateTime.now().minusYears(5), LocalDateTime.now().plusYears(5), RecurrenceFrequency.YEARLY);
        Assert.assertEquals(appointmentProvider.findBy(null, null, null, null, null,
                LocalDateTime.now().minusDays(7), LocalDateTime.now(), null).size(), 1);
        Assert.assertEquals(appointmentProvider.findBy(null, null, null, null, null,
                LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(1), null).size(), 2);
        Assert.assertEquals(appointmentProvider.findBy(null, null, null, null, null,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(200), null).size(), 0);
    }

    @Test
    public void workingDaysRecurrence() {
        //From Monday
        Appointment pastWeekAppointment = generateAppointment(LocalDateTime.of(2023, Month.NOVEMBER, 6, 10, 30));
        generateRecurrence(pastWeekAppointment, LocalDateTime.of(2023, Month.NOVEMBER, 6, 10, 30), LocalDateTime.of(2023, Month.NOVEMBER, 6, 10, 30).plusWeeks(2), RecurrenceFrequency.WORKING_DAYS);
        //Check on Sunday
        Assert.assertEquals(appointmentProvider.findBy(null, null, null, null, null,
                LocalDateTime.of(2023, Month.NOVEMBER, 6, 10, 30), LocalDateTime.of(2023, Month.NOVEMBER, 12, 11, 30), null).size(), 5);
        //Check next Monday
        Assert.assertEquals(appointmentProvider.findBy(null, null, null, null, null,
                LocalDateTime.of(2023, Month.NOVEMBER, 6, 10, 30), LocalDateTime.of(2023, Month.NOVEMBER, 13, 11, 30), null).size(), 6);
        //Check next Sunday
        Assert.assertEquals(appointmentProvider.findBy(null, null, null, null, null,
                LocalDateTime.of(2023, Month.NOVEMBER, 6, 10, 30), LocalDateTime.of(2023, Month.NOVEMBER, 19, 11, 30), null).size(), 10);
    }

    @Test
    public void monthlyOnDayRecurrence() {
        //From Monday
        Appointment pastWeekAppointment = generateAppointment(LocalDateTime.of(2023, Month.NOVEMBER, 6, 10, 30));
        generateRecurrence(pastWeekAppointment, LocalDateTime.of(2023, Month.NOVEMBER, 6, 10, 30), LocalDateTime.of(2024, Month.JANUARY, 6, 10, 30).plusWeeks(2), RecurrenceFrequency.MONTHLY_ON_WEEK_DAY);
        //Check on December
        List<Appointment> appointments = appointmentProvider.findBy(null, null, null, null, null,
                LocalDateTime.of(2023, Month.DECEMBER, 1, 0, 0), LocalDateTime.of(2023, Month.DECEMBER, 31, 23, 59), null);
        Assert.assertEquals(appointments.size(), 1);
        //Check that is Monday also.
        Assert.assertEquals(appointments.get(0).getStartTime().getDayOfWeek(), DayOfWeek.MONDAY);
        //Check is first week also.
        Assert.assertEquals(appointments.get(0).getStartTime().get(ChronoField.ALIGNED_WEEK_OF_MONTH), 1);

        //Check also with January
        appointments = appointmentProvider.findBy(null, null, null, null, null,
                LocalDateTime.of(2023, Month.DECEMBER, 1, 0, 0), LocalDateTime.of(2024, Month.JANUARY, 31, 23, 59), null);
        Assert.assertEquals(appointments.size(), 2);
        //Check that is Monday also.
        Assert.assertEquals(appointments.get(1).getStartTime().getDayOfWeek(), DayOfWeek.MONDAY);
        //Check is first week also.
        Assert.assertEquals(appointments.get(1).getStartTime().get(ChronoField.ALIGNED_WEEK_OF_MONTH), 1);
    }

    @Test
    public void monthlyOnDayOtherWeekRecurrence() {
        //From Monday
        Appointment pastWeekAppointment = generateAppointment(LocalDateTime.of(2023, Month.NOVEMBER, 20, 10, 30));
        generateRecurrence(pastWeekAppointment, LocalDateTime.of(2023, Month.NOVEMBER, 20, 10, 30), LocalDateTime.of(2024, Month.JANUARY, 20, 10, 30).plusWeeks(2), RecurrenceFrequency.MONTHLY_ON_WEEK_DAY);
        //Check on December
        List<Appointment> appointments = appointmentProvider.findBy(null, null, null, null, null,
                LocalDateTime.of(2023, Month.DECEMBER, 1, 0, 0), LocalDateTime.of(2023, Month.DECEMBER, 31, 23, 59), null);
        Assert.assertEquals(appointments.size(), 1);
        //Check that is Monday also.
        Assert.assertEquals(appointments.get(0).getStartTime().getDayOfWeek(), DayOfWeek.MONDAY);
        //Check is third week also.
        Assert.assertEquals(appointments.get(0).getStartTime().get(ChronoField.ALIGNED_WEEK_OF_MONTH), 3);

        //Check also with January
        appointments = appointmentProvider.findBy(null, null, null, null, null,
                LocalDateTime.of(2023, Month.DECEMBER, 1, 0, 0), LocalDateTime.of(2024, Month.JANUARY, 31, 23, 59), null);
        Assert.assertEquals(appointments.size(), 2);
        //Check that is Monday also.
        Assert.assertEquals(appointments.get(1).getStartTime().getDayOfWeek(), DayOfWeek.MONDAY);
        //Check is third week also.
        Assert.assertEquals(appointments.get(1).getStartTime().get(ChronoField.ALIGNED_WEEK_OF_MONTH), 3);
    }

    @AfterClass
    public void cleanUpDatabase() {
        appointmentProvider.deleteAll();
        recurrenceProvider.deleteAll();
        examinationTypeProvider.deleteAll();
        appointmentTypeProvider.deleteAll();
    }
}
