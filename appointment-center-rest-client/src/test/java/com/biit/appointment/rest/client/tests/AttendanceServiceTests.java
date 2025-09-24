package com.biit.appointment.rest.client.tests;

import com.biit.appointment.core.models.QrCodeDTO;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.core.providers.AppointmentTypeProvider;
import com.biit.appointment.core.providers.AttendanceProvider;
import com.biit.appointment.core.providers.ExaminationTypeProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.rest.client.AppointmentCenterClient;
import com.biit.appointment.rest.client.Server;
import com.biit.server.security.model.AuthRequest;
import com.biit.server.security.model.IAuthenticatedUser;
import com.biit.usermanager.client.providers.AuthenticatedUserProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = DEFINED_PORT, classes = Server.class)
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Test(groups = {"appointmentClient"})
public class AttendanceServiceTests extends AbstractTestNGSpringContextTests {
    private static final String USER_NAME = "user";
    private static final String GUEST_NAME = "guest";
    private static final String USER_PASSWORD = "password";
    private static final String ORGANIZATION_ID = "The Organization";
    private static final String TEST_TYPE_NAME = "basic";
    private static final String APPOINTMENT_TITLE = "The Appointment";
    private static final String APPOINTMENT_SPECIALTY = "Physical";

    private static final String JWT_SALT = "4567";

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AppointmentTypeProvider appointmentTypeProvider;

    @Autowired
    private ExaminationTypeProvider examinationTypeProvider;

    @Autowired
    private AppointmentProvider appointmentProvider;

    @Autowired
    private AppointmentCenterClient appointmentCenterClient;

    @Autowired
    private AttendanceProvider attendanceProvider;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private ExaminationType type;

    private Appointment appointment;

    private IAuthenticatedUser admin;
    private IAuthenticatedUser guest;

    private <T> String toJson(T object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
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
        final Appointment newAppointment = new Appointment();
        newAppointment.setTitle(APPOINTMENT_TITLE);
        newAppointment.setStartTime(LocalDateTime.of(2024, 3, 27, 16, 38, 3));
        newAppointment.setEndTime(LocalDateTime.of(2024, 3, 27, 18, 38, 3));
        newAppointment.setOrganizer(UUID.fromString(admin.getUID()));
        this.appointment = appointmentProvider.save(newAppointment);
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

        final String adminJwtToken = createResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        Assert.assertNotNull(adminJwtToken);
    }


    @Test
    public void attend() {
        final Optional<QrCodeDTO> qrCode = appointmentCenterClient.getQrCode(appointment.getId());
        Assert.assertTrue(qrCode.isPresent());
        appointmentCenterClient.attendByQrCode(appointment.getId(), qrCode.get());

        //Check user is marked as attending the process.
        Assert.assertEquals(attendanceProvider.findByAttendee(UUID.fromString(guest.getUID())).size(), 1);
    }
}
