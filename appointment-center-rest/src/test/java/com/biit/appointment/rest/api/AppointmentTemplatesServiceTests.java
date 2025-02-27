package com.biit.appointment.rest.api;

import com.biit.appointment.core.converters.AppointmentTemplateConverter;
import com.biit.appointment.core.converters.models.AppointmentTemplateConverterRequest;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.models.AppointmentTemplateAvailabilityDTO;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.core.providers.AppointmentTemplateProvider;
import com.biit.appointment.core.providers.AppointmentTypeProvider;
import com.biit.appointment.core.providers.ExaminationTypeProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentTemplate;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.rest.Server;
import com.biit.usermanager.client.providers.AuthenticatedUserProvider;
import com.biit.server.security.model.AuthRequest;
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
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = Server.class)
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Test(groups = {"appointmentTemplatesServiceTests"})
public class AppointmentTemplatesServiceTests extends AbstractTestNGSpringContextTests {

    private final static String USER_NAME = "user";
    private final static String USER_PASSWORD = "password";
    private final static String JWT_SALT = "4567";

    private final static String ORGANIZATION_ID = "The Organization";
    private static final String TEST_TYPE_NAME = "basic";

    private final static int TEMPLATE_DURATION = 90;
    private final static double TEMPLATE_COST = 100D;
    private static final String APPOINTMENT_TITLE = "The Template";
    private static final String APPOINTMENT_SPECIALTY = "Physical";
    private static final String APPOINTMENT_DESCRIPTION = "Template Description";

    private static final Set<UUID> SPEAKERS = new HashSet<>(Arrays.asList(UUID.randomUUID(), UUID.randomUUID(),
            UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID()));

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
    private AppointmentTemplateProvider appointmentTemplateProvider;

    @Autowired
    private AppointmentTemplateConverter appointmentTemplateConverter;

    @Autowired
    private AppointmentProvider appointmentProvider;

    private MockMvc mockMvc;

    private String jwtToken;

    private ExaminationType type;

    private AppointmentTemplate appointmentTemplate;


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

    @BeforeClass(dependsOnMethods = "generateExaminationType")
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

    @BeforeClass
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @BeforeClass
    public void addUser() {
        //Create the admin user
        authenticatedUserProvider.createUser(USER_NAME, USER_NAME, USER_PASSWORD);
    }

    @Test
    public void checkAuthentication() {
        //Check the admin user
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(USER_NAME, JWT_SALT + USER_PASSWORD));
    }

    @Test
    public void setAuthentication() throws Exception {
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

        jwtToken = createResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
        Assert.assertNotNull(jwtToken);
    }

    @Test(dependsOnMethods = "setAuthentication")
    public void getDefaultSchedule() throws Exception {
        //Range is one week
        final LocalDateTime lowerTimeBoundary = LocalDateTime.of(2024, 2, 12, 0, 0);
        final LocalDateTime upperTimeBoundary = LocalDateTime.of(2024, 2, 18, 23, 59);
        final MvcResult createResult = mockMvc.perform(
                        get("/appointment-templates/lower-time-boundary/"
                                + lowerTimeBoundary.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                + "/upper-time-boundary/"
                                + upperTimeBoundary.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                + "?templateId=" + appointmentTemplate.getId())
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + jwtToken)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final List<AppointmentTemplateAvailabilityDTO> appointmentTemplateScheduleDTOS =
                Arrays.asList(objectMapper.readValue(createResult.getResponse().getContentAsString(), AppointmentTemplateAvailabilityDTO[].class));

        Assert.assertEquals(appointmentTemplateScheduleDTOS.get(0).getAppointmentTemplate().getId(), appointmentTemplate.getId());
        Assert.assertEquals(appointmentTemplateScheduleDTOS.get(0).getAvailability().size(), 1);
        Assert.assertEquals(appointmentTemplateScheduleDTOS.get(0).getAvailability().get(0).lowerBound(), lowerTimeBoundary);
        Assert.assertEquals(appointmentTemplateScheduleDTOS.get(0).getAvailability().get(0).upperBound(), upperTimeBoundary);
    }


    @Test(dependsOnMethods = "getDefaultSchedule")
    public void getScheduleWithExistingAppointment() throws Exception {
        //Set an existing appointment
        final Appointment existingAppointment = new Appointment();
        existingAppointment.setStartTime(LocalDateTime.of(2124, 2, 15, 17, 0));
        existingAppointment.setEndTime(LocalDateTime.of(2124, 2, 15, 19, 30));
        //One speaker is also present on this appointment
        existingAppointment.setSpeakers(Collections.singleton(SPEAKERS.iterator().next()));
        appointmentProvider.save(existingAppointment);

        //Set another existing appointment
        final Appointment anotherExistingAppointment = new Appointment();
        anotherExistingAppointment.setStartTime(LocalDateTime.of(2124, 2, 15, 10, 0));
        anotherExistingAppointment.setEndTime(LocalDateTime.of(2124, 2, 15, 12, 0));
        //No speaker is also present on this appointment. Must not change the schedule ranges.
        anotherExistingAppointment.setSpeakers(Collections.singleton(UUID.randomUUID()));
        appointmentProvider.save(anotherExistingAppointment);

        //Range is one week
        final LocalDateTime lowerTimeBoundary = LocalDateTime.of(2124, 2, 12, 0, 0);
        final LocalDateTime upperTimeBoundary = LocalDateTime.of(2124, 2, 18, 23, 59);
        final MvcResult createResult = mockMvc.perform(
                        get("/appointment-templates/lower-time-boundary/"
                                + lowerTimeBoundary.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                + "/upper-time-boundary/"
                                + upperTimeBoundary.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                + "?templateId=" + appointmentTemplate.getId())
                                .header(HttpHeaders.AUTHORIZATION,
                                        "Bearer " + jwtToken)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final List<AppointmentTemplateAvailabilityDTO> appointmentTemplateScheduleDTOS =
                Arrays.asList(objectMapper.readValue(createResult.getResponse().getContentAsString(), AppointmentTemplateAvailabilityDTO[].class));

        Assert.assertEquals(appointmentTemplateScheduleDTOS.get(0).getAppointmentTemplate().getId(), appointmentTemplate.getId());
        Assert.assertEquals(appointmentTemplateScheduleDTOS.get(0).getAvailability().size(), 2);
        Assert.assertEquals(appointmentTemplateScheduleDTOS.get(0).getAvailability().get(0).lowerBound(), lowerTimeBoundary);
        Assert.assertEquals(appointmentTemplateScheduleDTOS.get(0).getAvailability().get(0).upperBound(), LocalDateTime.of(2124, 2, 15, 17, 0));
        Assert.assertEquals(appointmentTemplateScheduleDTOS.get(0).getAvailability().get(1).lowerBound(), LocalDateTime.of(2124, 2, 15, 19, 30));
        Assert.assertEquals(appointmentTemplateScheduleDTOS.get(0).getAvailability().get(1).upperBound(), upperTimeBoundary);
    }


    @Test(dependsOnMethods = "setAuthentication")
    public void createAppointmentFromTemplate() throws Exception {
        //Appointment generation time
        final LocalDateTime appointmentStartingTime = LocalDateTime.of(2035, 3, 3, 3, 3);

        final MvcResult createResult = mockMvc.perform(
                        post("/appointments/templates/starting-time/"
                                + appointmentStartingTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toJson(appointmentTemplateConverter.convert(new AppointmentTemplateConverterRequest(appointmentTemplate))))
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        final AppointmentDTO generatedAppointment = objectMapper.readValue(createResult.getResponse().getContentAsString(), AppointmentDTO.class);
        Assert.assertEquals(generatedAppointment.getStartTime(), appointmentStartingTime);
    }

    @Test(dependsOnMethods = "createAppointmentFromTemplate")
    public void getAppointmentFromTemplate() throws Exception {
        final MvcResult createResult = mockMvc.perform(
                        get("/appointments/templates/" + appointmentTemplate.getId())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final List<AppointmentDTO> appointmentsFromTemplate =
                Arrays.asList(objectMapper.readValue(createResult.getResponse().getContentAsString(), AppointmentDTO[].class));
        Assert.assertEquals(appointmentsFromTemplate.size(), 1);
    }


    @Test(dependsOnMethods = "createAppointmentFromTemplate")
    public void getAppointmentFromTemplateList() throws Exception {
        final MvcResult createResult = mockMvc.perform(
                        get("/appointments/templates?templateId=" + appointmentTemplate.getId())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final List<AppointmentDTO> appointmentsFromTemplate =
                Arrays.asList(objectMapper.readValue(createResult.getResponse().getContentAsString(), AppointmentDTO[].class));
        Assert.assertEquals(appointmentsFromTemplate.size(), 1);
    }


    @Test(dependsOnMethods = {"getAppointmentFromTemplate", "getAppointmentFromTemplateList", "getScheduleWithExistingAppointment"})
    public void deletingATemplateDoesNotDeleteTheAppointment() throws Exception {
        mockMvc.perform(
                        delete("/appointment-templates/" + appointmentTemplate.getId())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                                .accept(MediaType.APPLICATION_JSON)
                                .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andReturn();

        Assert.assertEquals(appointmentTemplateProvider.count(), 0);
        //Two starting appointments + 1 appointment created by a test.
        Assert.assertEquals(appointmentProvider.count(), 3);
    }

}
