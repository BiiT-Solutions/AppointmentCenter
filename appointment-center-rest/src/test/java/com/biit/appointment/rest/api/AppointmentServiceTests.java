package com.biit.appointment.rest.api;

import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.core.providers.AppointmentTypeProvider;
import com.biit.appointment.core.providers.AttendanceProvider;
import com.biit.appointment.core.providers.ExaminationTypeProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.rest.Server;
import com.biit.server.security.IAuthenticatedUser;
import com.biit.server.security.model.AuthRequest;
import com.biit.usermanager.client.providers.AuthenticatedUserProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = Server.class)
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Test(groups = {"appointmentServiceTests"})
public class AppointmentServiceTests extends AbstractTestNGSpringContextTests {
    private final static String USER_NAME = "user";
    private final static String GUEST_NAME = "guest";
    private final static String USER_PASSWORD = "password";
    private final static String JWT_SALT = "4567";
    private final static String ORGANIZATION_ID = "The Organization";
    private static final String TEST_TYPE_NAME = "basic";
    private static final String APPOINTMENT_TITLE = "The Appointment";
    private static final String APPOINTMENT_2_TITLE = "The Appointment 2";
    private static final String APPOINTMENT_3_TITLE = "The Appointment 2";
    private static final String APPOINTMENT_SPECIALTY = "Physical";

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppointmentTypeProvider appointmentTypeProvider;

    @Autowired
    private ExaminationTypeProvider examinationTypeProvider;

    @Autowired
    private AppointmentProvider appointmentProvider;

    @Autowired
    private AttendanceProvider attendanceProvider;

    private MockMvc mockMvc;

    private String adminJwtToken;
    private String guestJwtToken;

    private ExaminationType type;

    private Appointment appointment;

    private IAuthenticatedUser admin;
    private IAuthenticatedUser guest;


    private <T> String toJson(T object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    private <T> T fromJson(String payload, Class<T> clazz) throws IOException {
        return objectMapper.readValue(payload, clazz);
    }

    private <T> List<T> fromJsonList(String payload) throws IOException {
        return objectMapper.readValue(payload, new TypeReference<>() {
        });
    }

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
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @BeforeClass
    public void addUser() {
        //Create the admin user
        admin = authenticatedUserProvider.createUser(USER_NAME, USER_NAME, USER_PASSWORD);
        guest = authenticatedUserProvider.createUser(GUEST_NAME, GUEST_NAME, USER_PASSWORD);
        authenticatedUserProvider.setRoles(guest, Collections.singleton("GUEST"));
    }

    @BeforeClass
    public void createAppointment() {
        final Appointment appointment = new Appointment();
        appointment.setTitle(APPOINTMENT_TITLE);
        appointment.setStartTime(LocalDateTime.of(2024, 3, 27, 16, 38, 3));
        appointment.setEndTime(LocalDateTime.of(2024, 3, 27, 18, 38, 3));
        appointment.setOrganizer(UUID.fromString(admin.getUID()));
        appointment.setCreatedBy(admin.getUsername());
        this.appointment = appointmentProvider.save(appointment);
    }


    @BeforeClass
    public void createTodayAppointment() {
        final Appointment appointment = new Appointment();
        appointment.setTitle(APPOINTMENT_2_TITLE);
        appointment.setStartTime(LocalDateTime.now());
        appointment.setEndTime(LocalDateTime.now().plusHours(2));
        appointment.setOrganizer(UUID.fromString(admin.getUID()));
        appointment.addAttendee(UUID.fromString(admin.getUID()));
        appointment.setCreatedBy(admin.getUsername());
        appointmentProvider.save(appointment);
    }


    @BeforeClass
    public void createFutureAppointment() {
        final Appointment appointment = new Appointment();
        appointment.setTitle(APPOINTMENT_3_TITLE);
        appointment.setStartTime(LocalDateTime.now().plusDays(1));
        appointment.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        appointment.setOrganizer(UUID.fromString(admin.getUID()));
        appointment.addAttendee(UUID.fromString(admin.getUID()));
        appointment.setCreatedBy(admin.getUsername());
        appointmentProvider.save(appointment);
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


    @Test
    public void setGuestAuthentication() throws Exception {
        AuthRequest request = new AuthRequest();
        request.setUsername(GUEST_NAME);
        request.setPassword(USER_PASSWORD);

        final MvcResult createResult = this.mockMvc
                .perform(post("/auth/public/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().exists(HttpHeaders.AUTHORIZATION))
                .andReturn();

        guestJwtToken = createResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        Assert.assertNotNull(guestJwtToken);
    }


    @Test(dependsOnMethods = "setAdminAuthentication")
    public void checkAppointmentTimeFormat() throws Exception {
        this.mockMvc
                .perform(get("/appointments/" + appointment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(content().json("{'startTime':'2024-03-27T16:38:03Z'}"))
                .andExpect(content().json("{'endTime':'2024-03-27T18:38:03Z'}"))
                .andReturn();
    }


    @Test(dependsOnMethods = "setAdminAuthentication")
    public void getOwnAppointmentByOrganizer() throws Exception {
        this.mockMvc
                .perform(get("/appointments/organizers/" + admin.getUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }


    @Test(dependsOnMethods = "setAdminAuthentication")
    public void getOthersAppointmentByOrganizer() throws Exception {
        this.mockMvc
                .perform(get("/appointments/organizers/" + admin.getUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + guestJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andReturn();
    }


    @Test(dependsOnMethods = "setAdminAuthentication")
    public void adminGetOthersAppointmentByOrganizer() throws Exception {
        this.mockMvc
                .perform(get("/appointments/organizers/" + guest.getUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }


    @Test(dependsOnMethods = "setAdminAuthentication")
    public void subscribeToAppointment() throws Exception {
        this.mockMvc
                .perform(put("/appointments/" + appointment.getId() + "/attendees/subscribe")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }


    @Test(dependsOnMethods = "subscribeToAppointment")
    public void attendToAppointment() throws Exception {
        this.mockMvc
                .perform(put("/appointments/" + appointment.getId() + "/attendees/" + admin.getUID() + "/attend")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Assert.assertEquals(attendanceProvider.findByAttendee(UUID.fromString(admin.getUID())).size(), 1);
    }


    @Test(dependsOnMethods = "subscribeToAppointment")
    public void attendToInvalidAppointment() throws Exception {
        this.mockMvc
                .perform(put("/appointments/" + appointment.getId() + "/attendees/" + guest.getUID() + "/attend")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        Assert.assertEquals(attendanceProvider.findByAttendee(UUID.fromString(guest.getUID())).size(), 0);
    }


    @Test(dependsOnMethods = "attendToAppointment")
    public void unattendToAppointment() throws Exception {
        this.mockMvc
                .perform(put("/appointments/" + appointment.getId() + "/attendees/" + admin.getUID() + "/unattend")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Assert.assertEquals(attendanceProvider.findByAttendee(UUID.fromString(admin.getUID())).size(), 0);
    }


    @Test(dependsOnMethods = "attendToAppointment")
    public void findAppointments() throws Exception {
        final MvcResult createResult = this.mockMvc
                .perform(get("/appointments/find/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("lowerTimeBoundary", "2024-03-27T00:00:00.000Z")
                        .param("upperTimeBoundary", "2024-03-28T00:00:00.000Z")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final List<AppointmentDTO> myAppointments =
                Arrays.asList(objectMapper.readValue(createResult.getResponse().getContentAsString(), AppointmentDTO[].class));
        Assert.assertEquals(myAppointments.size(), 1);
    }


    @Test(dependsOnMethods = "attendToAppointment")
    public void findOthersAppointments() throws Exception {
        final MvcResult createResult = this.mockMvc
                .perform(get("/appointments/find")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("lowerTimeBoundary", "2024-03-27T00:00:00.000Z")
                        .param("upperTimeBoundary", "2024-03-28T00:00:00.000Z")
                        .param("organizer", admin.getUID())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final List<AppointmentDTO> myAppointments =
                Arrays.asList(objectMapper.readValue(createResult.getResponse().getContentAsString(), AppointmentDTO[].class));
        Assert.assertEquals(myAppointments.size(), 1);
    }


    @Test(dependsOnMethods = "attendToAppointment")
    public void findAllAppointments() throws Exception {
        final MvcResult createResult = this.mockMvc
                .perform(get("/appointments/find/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final List<AppointmentDTO> myAppointments =
                Arrays.asList(objectMapper.readValue(createResult.getResponse().getContentAsString(), AppointmentDTO[].class));
        Assert.assertEquals(myAppointments.size(), 3);
    }


    @Test(dependsOnMethods = "attendToAppointment")
    public void unattendToInvalidAppointment() throws Exception {
        System.out.println(" #------------------------------------ Expected Exception ----------------------------");
        this.mockMvc
                .perform(put("/appointments/" + appointment.getId() + "/attendees/" + guest.getUID() + "/unattend")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
        System.out.println(" #--------------------------------- End of Expected Exception -------------------------");
    }


    @Test(dependsOnMethods = {"subscribeToAppointment", "attendToAppointment", "attendToInvalidAppointment",
            "unattendToInvalidAppointment", "unattendToAppointment", "findAppointments"})
    public void unsubscribeToAppointment() throws Exception {
        this.mockMvc
                .perform(put("/appointments/" + appointment.getId() + "/attendees/unsubscribe")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }


    @Test(dependsOnMethods = "setAdminAuthentication")
    public void getAdminAppointmentsFromToday() throws Exception {
        final MvcResult createResult = this.mockMvc
                .perform(get("/appointments/today/users/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final List<AppointmentDTO> adminAppointmentsOnToday =
                Arrays.asList(objectMapper.readValue(createResult.getResponse().getContentAsString(), AppointmentDTO[].class));
        Assert.assertEquals(adminAppointmentsOnToday.size(), 1);
    }


    @Test(dependsOnMethods = "setGuestAuthentication")
    public void getGuestAppointmentsFromToday() throws Exception {
        final MvcResult createResult = this.mockMvc
                .perform(get("/appointments/today/users/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + guestJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final List<AppointmentDTO> guestAppointmentsOnToday =
                Arrays.asList(objectMapper.readValue(createResult.getResponse().getContentAsString(), AppointmentDTO[].class));
        Assert.assertEquals(guestAppointmentsOnToday.size(), 0);
    }


    @Test(dependsOnMethods = "setAdminAuthentication")
    public void getAdminAppointmentsFromFuture() throws Exception {
        final MvcResult createResult = this.mockMvc
                .perform(get("/appointments/future/next/users/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final AppointmentDTO adminNextAppointment =
                objectMapper.readValue(createResult.getResponse().getContentAsString(), AppointmentDTO.class);
        Assert.assertEquals(adminNextAppointment.getTitle(), APPOINTMENT_3_TITLE);
    }


    @Test(dependsOnMethods = "setGuestAuthentication")
    public void getGuestAppointmentsFromFuture() throws Exception {
        final MvcResult createResult = this.mockMvc
                .perform(get("/appointments/future/next/users/current")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + guestJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        Assert.assertTrue(createResult.getResponse().getContentAsString().isBlank());
    }

}
