package com.biit.appointment.rest.external;

import com.biit.appointment.core.controllers.UserAvailabilityController;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.models.ExternalCalendarCredentialsDTO;
import com.biit.appointment.core.models.UserAvailabilityDTO;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.google.client.GoogleClientProvider;
import com.biit.appointment.google.converter.GoogleCalendarCredentialsConverter;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.Schedule;
import com.biit.appointment.persistence.entities.ScheduleRange;
import com.biit.appointment.persistence.repositories.ScheduleRepository;
import com.biit.appointment.rest.Server;
import com.biit.server.security.model.IAuthenticatedUser;
import com.biit.server.security.model.AuthRequest;
import com.biit.usermanager.client.providers.AuthenticatedUserProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * This tests needs to give access to google calendar by a link.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = Server.class)
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Test(groups = {"externalCalendarTests"})
public class GoogleCalendarAccessTests extends AbstractTestNGSpringContextTests {
    private static final String USER_NAME = "user";
    private static final String USER_PASSWORD = "password";
    private static final String JWT_SALT = "4567";
    private static final String ORGANIZATION_ID = "THE ORGANIZATION";
    //Monday
    private final LocalDate today = LocalDate.of(2025, 2, 24);

    @Autowired
    private AppointmentProvider appointmentProvider;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public UserAvailabilityController userAvailabilityController;

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private GoogleCalendarCredentialsConverter googleCalendarCredentialsConverter;

    private MockMvc mockMvc;

    private String adminJwtToken;

    private IAuthenticatedUser admin;

    private Credential credential;

    private AppointmentDTO googleAppointment;

    private <T> String toJson(T object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    private Appointment generateAppointment(LocalDateTime onDate, UUID organizer, Set<UUID> attendees) {
        final Appointment appointment = new Appointment();
        appointment.setOrganizer(organizer);
        appointment.setOrganizationId(ORGANIZATION_ID);
        appointment.setAttendees(attendees);
        appointment.setCost(50D);

        appointment.setStartTime(onDate);
        appointment.setEndTime(appointment.getStartTime().plusHours(2));

        return appointmentProvider.save(appointment);
    }

    @BeforeClass
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }


    @BeforeClass
    public void addUser() {
        //Create the admin user
        admin = authenticatedUserProvider.createUser(USER_NAME, USER_NAME, USER_PASSWORD);
    }


    @BeforeClass
    public void setUserAppointments() {
        generateAppointment(LocalDateTime.of(today, LocalTime.of(9, 00)), UUID.fromString(admin.getUID()), new HashSet<>());
    }

    @BeforeClass(dependsOnMethods = "addUser")
    public void generateSchedule() {
        final Schedule schedule = new Schedule();
        schedule.addRange(new ScheduleRange(DayOfWeek.MONDAY, LocalTime.of(9, 00), LocalTime.of(13, 30)));
        schedule.setUser(UUID.fromString(admin.getUID()));

        scheduleRepository.save(schedule);
    }

    @BeforeClass(dependsOnMethods = "addUser")
    public void generateCredentials() throws GeneralSecurityException, IOException {
        final NetHttpTransport netHttpTransport = GoogleNetHttpTransport.newTrustedTransport();
        final GoogleClientProvider googleClient = new GoogleClientProvider();
        credential = googleClient.getCredentials(netHttpTransport);
    }

    @Test
    public void checkAuthentication() {
        //Check the admin user
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(USER_NAME, JWT_SALT + USER_PASSWORD));
    }


    @Test
    public void setAdminAuthentication() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername(USER_NAME);
        request.setPassword(USER_PASSWORD);

        final MvcResult createResult = this.mockMvc
                .perform(post("/auth/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.AUTHORIZATION))
                .andReturn();

        adminJwtToken = createResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        Assert.assertNotNull(adminJwtToken);
    }

    @Test(dependsOnMethods = {"setAdminAuthentication"})
    public void storeCredentials() throws Exception {
        ExternalCalendarCredentialsDTO externalCalendarCredentialsDTO = googleCalendarCredentialsConverter.
                convertElement(UUID.fromString(admin.getUID()), credential);

        final MvcResult result = this.mockMvc
                .perform(post("/external-calendar-credentials")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(externalCalendarCredentialsDTO))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
    }

    /**
     * On google calendar an appointment from 11h to 11h30 exists. So availability must be after 11h30
     *
     * @throws Exception
     */
    @Test(dependsOnMethods = {"storeCredentials"})
    public void checkAvailabilityGet() throws Exception {
        final MvcResult result = this.mockMvc
                .perform(get("/availabilities/users/me"
                        + "/from/" + LocalDateTime.of(today, LocalTime.of(9, 0)).atOffset(ZoneOffset.UTC)
                        + "/to/" + LocalDateTime.of(today, LocalTime.of(13, 0)).atOffset(ZoneOffset.UTC)
                        + "/slot-in-minutes/30/slots/3")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final List<UserAvailabilityDTO> availabilities = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), UserAvailabilityDTO[].class));
        Assert.assertEquals(availabilities.size(), 3);
        Assert.assertEquals(availabilities.get(0).getStartTime(), LocalDateTime.of(today, LocalTime.of(11, 30)));
        Assert.assertEquals(availabilities.get(1).getStartTime(), LocalDateTime.of(today, LocalTime.of(12, 0)));
        Assert.assertEquals(availabilities.get(2).getStartTime(), LocalDateTime.of(today, LocalTime.of(12, 30)));
    }


    /**
     * On google calendar a recursive appointment from 11h to 11h30 exists.
     *
     * @throws Exception
     */
    @Test(dependsOnMethods = {"storeCredentials"})
    public void getByRange() throws Exception {
        final MvcResult result = this.mockMvc
                .perform(get("/appointments/external-providers/GOOGLE"
                        + "/from/" + LocalDateTime.of(today, LocalTime.of(0, 0)).atOffset(ZoneOffset.UTC)
                        + "/to/" + LocalDateTime.of(today, LocalTime.of(23, 59)).atOffset(ZoneOffset.UTC))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final List<AppointmentDTO> appointmentsOnGoogle = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), AppointmentDTO[].class));
        Assert.assertEquals(appointmentsOnGoogle.size(), 1);
        googleAppointment = appointmentsOnGoogle.get(0);
    }


    /**
     * On google calendar a recursive appointment from 11h to 11h30 exists.
     *
     * @throws Exception
     */
    @Test(dependsOnMethods = {"storeCredentials"})
    public void getByRangeAndTotal() throws Exception {
        final MvcResult result = this.mockMvc
                .perform(get("/appointments/external-providers/GOOGLE"
                        + "/from/" + LocalDateTime.of(today, LocalTime.of(0, 0)).atOffset(ZoneOffset.UTC)
                        + "/total/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final List<AppointmentDTO> appointmentsOnGoogle = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), AppointmentDTO[].class));
        Assert.assertEquals(appointmentsOnGoogle.size(), 1);
    }


    @Test(dependsOnMethods = {"getByRange"})
    public void getAppointmentByExternalRefererence() throws Exception {
        final MvcResult result = this.mockMvc
                .perform(get("/appointments/external-providers/GOOGLE"
                        + "/external-references/" + googleAppointment.getExternalReference())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final AppointmentDTO appointmentOnGoogle = objectMapper.readValue(result.getResponse().getContentAsString(), AppointmentDTO.class);
        Assert.assertNotNull(appointmentOnGoogle);
    }

}
