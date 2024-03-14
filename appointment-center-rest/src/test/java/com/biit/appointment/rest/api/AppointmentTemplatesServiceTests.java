package com.biit.appointment.rest.api;

import com.biit.appointment.core.models.AppointmentTemplateAvailabilityDTO;
import com.biit.appointment.core.providers.AppointmentTemplateProvider;
import com.biit.appointment.core.providers.AppointmentTypeProvider;
import com.biit.appointment.core.providers.ExaminationTypeProvider;
import com.biit.appointment.persistence.entities.AppointmentTemplate;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.rest.Server;
import com.biit.server.security.AuthenticatedUserProvider;
import com.biit.server.security.model.AuthRequest;
import com.biit.usermanager.dto.ApplicationBackendServiceRoleDTO;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = Server.class)
@ExtendWith(MockitoExtension.class)
@Test(groups = {"appointmentTemplatesServiceTests"})
public class AppointmentTemplatesServiceTests extends AbstractTestNGSpringContextTests {

    private final static String USER_NAME = "user";
    private final static String USER_PASSWORD = "password";
    private final static String JWT_SALT = "4567";


    private final static Long ORGANIZATION_ID = 43L;
    private static final String TEST_TYPE_NAME = "basic";

    private final static int TEMPLATE_DURATION = 90;
    private final static double TEMPLATE_COST = 100D;
    private static final String APPOINTMENT_TITLE = "The Template";
    private static final String APPOINTMENT_SPECIALTY = "Physical";
    private static final String APPOINTMENT_DESCRIPTION = "Template Description";

    private static final Set<Long> SPEAKERS = new HashSet<>(Arrays.asList(1L, 2L, 3L, 4L, 5L));

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
    public void getDefaultAvailability() throws Exception {
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

        final List<AppointmentTemplateAvailabilityDTO> appointmentTemplateAvailabilityDTOS =
                Arrays.asList(objectMapper.readValue(createResult.getResponse().getContentAsString(), AppointmentTemplateAvailabilityDTO[].class));

        Assert.assertEquals(appointmentTemplateAvailabilityDTOS.get(0).getAppointmentTemplate().getId(), appointmentTemplate.getId());
        Assert.assertEquals(appointmentTemplateAvailabilityDTOS.get(0).getAvailability().get(0).lowerBound(), lowerTimeBoundary);
        Assert.assertEquals(appointmentTemplateAvailabilityDTOS.get(0).getAvailability().get(0).upperBound(), upperTimeBoundary);
    }


}
