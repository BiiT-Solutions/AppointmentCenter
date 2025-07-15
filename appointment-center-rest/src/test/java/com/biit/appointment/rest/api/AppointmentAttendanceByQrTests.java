package com.biit.appointment.rest.api;

import com.biit.appointment.core.converters.models.AttendanceRequest;
import com.biit.appointment.core.models.QrCodeDTO;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.core.providers.AppointmentTypeProvider;
import com.biit.appointment.core.providers.AttendanceProvider;
import com.biit.appointment.core.providers.ExaminationTypeProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.rest.Server;
import com.biit.server.security.model.IAuthenticatedUser;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = Server.class)
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Test(groups = {"appointmentAttendanceByQrTests"})
public class AppointmentAttendanceByQrTests extends AbstractTestNGSpringContextTests {
    private final static String USER_NAME = "user";
    private final static String GUEST_NAME = "guest";
    private final static String USER_PASSWORD = "password";
    private final static String JWT_SALT = "4567";
    private final static String ORGANIZATION_ID = "The Organization";
    private static final String TEST_TYPE_NAME = "basic";
    private static final String APPOINTMENT_TITLE = "The Appointment";
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

    private QrCodeDTO qrCodeDTO;


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
        this.appointment = appointmentProvider.save(appointment);
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


    @Test(dependsOnMethods = "setGuestAuthentication")
    public void subscribeToAppointment() throws Exception {
        this.mockMvc
                .perform(put("/appointments/" + appointment.getId() + "/attendees/subscribe")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + guestJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }


    @Test(dependsOnMethods = "subscribeToAppointment")
    public void generateQrAttendanceCode() throws Exception {
        final MvcResult qrCode = this.mockMvc
                .perform(get("/qr/appointment/" + appointment.getId() + "/attendance")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + guestJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        qrCodeDTO = fromJson(qrCode.getResponse().getContentAsString(), QrCodeDTO.class);
        final AttendanceRequest attendanceRequest = AttendanceRequest.decode(qrCodeDTO.getContent());
        Assert.assertEquals(attendanceRequest.getAppointmentId(), appointment.getId());
        Assert.assertEquals(attendanceRequest.getAttender(), UUID.fromString(guest.getUID()));
    }


    @Test(dependsOnMethods = "generateQrAttendanceCode")
    public void checkThatIAmNotAttendingTheAppointment() throws Exception {
        this.mockMvc
                .perform(get("/appointments/" + appointment.getId() + "/attending")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + guestJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andReturn();
    }


    @Test(dependsOnMethods = "checkThatIAmNotAttendingTheAppointment")
    public void attendToDifferentAppointmentUsingQrCode() throws Exception {
        this.mockMvc
                .perform(put("/appointments/42/attend/text")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(qrCodeDTO.getContent())
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }


    @Test(dependsOnMethods = "attendToDifferentAppointmentUsingQrCode")
    public void attendToAppointmentUsingQrCode() throws Exception {
        this.mockMvc
                .perform(put("/appointments/" + appointment.getId() + "/attend/text")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(qrCodeDTO.getContent())
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        //Check user is marked as attending the process.
        Assert.assertEquals(attendanceProvider.findByAttendee(UUID.fromString(guest.getUID())).size(), 1);
        Assert.assertEquals(attendanceProvider.findByAttendee(UUID.fromString(admin.getUID())).size(), 0);
    }


    @Test(dependsOnMethods = "attendToAppointmentUsingQrCode")
    public void checkThatIAmAttendingTheAppointment() throws Exception {
        this.mockMvc
                .perform(get("/appointments/" + appointment.getId() + "/attending")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + guestJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn();
    }

}
