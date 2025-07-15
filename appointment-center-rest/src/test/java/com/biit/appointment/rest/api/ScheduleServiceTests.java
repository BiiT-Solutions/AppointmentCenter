package com.biit.appointment.rest.api;

import com.biit.appointment.core.models.ScheduleDTO;
import com.biit.appointment.core.models.ScheduleRangeDTO;
import com.biit.appointment.core.providers.ScheduleProvider;
import com.biit.appointment.persistence.entities.Schedule;
import com.biit.appointment.persistence.entities.ScheduleRange;
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
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = Server.class)
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Test(groups = {"scheduleServiceTests"})
public class ScheduleServiceTests extends AbstractTestNGSpringContextTests {
    private final static String USER_NAME = "user";
    private final static String USER_PASSWORD = "password";
    private final static String JWT_SALT = "4567";

    @Autowired
    private AuthenticatedUserProvider authenticatedUserProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private String adminJwtToken;

    private IAuthenticatedUser admin;

    @Autowired
    private ScheduleProvider scheduleProvider;


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


    @Test(dependsOnMethods = "setAdminAuthentication")
    public void setUserSchedule() throws Exception {
        final MvcResult result = this.mockMvc
                .perform(put("/schedules/users/" + admin.getUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(List.of(new ScheduleRangeDTO(DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(13, 0)))))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final ScheduleDTO userSchedule = objectMapper.readValue(result.getResponse().getContentAsString(), ScheduleDTO.class);
        Assert.assertEquals(userSchedule.getUser(), UUID.fromString(admin.getUID()));
        Assert.assertEquals(userSchedule.getRanges().size(), 1);
        Assert.assertEquals(userSchedule.getRanges().get(0).getDayOfWeek(), DayOfWeek.FRIDAY);
        Assert.assertEquals(userSchedule.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(userSchedule.getRanges().get(0).getEndTime(), LocalTime.of(13, 0));
    }


    @Test(dependsOnMethods = "setUserSchedule")
    public void addExtraSchedule() throws Exception {
        MvcResult result = this.mockMvc
                .perform(post("/schedules/users/" + admin.getUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(List.of(new ScheduleRangeDTO(DayOfWeek.FRIDAY, LocalTime.of(15, 0), LocalTime.of(17, 0)))))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ScheduleDTO userSchedule = objectMapper.readValue(result.getResponse().getContentAsString(), ScheduleDTO.class);
        Assert.assertEquals(userSchedule.getUser(), UUID.fromString(admin.getUID()));
        Assert.assertEquals(userSchedule.getRanges().size(), 2);
        Assert.assertEquals(userSchedule.getRanges().get(0).getDayOfWeek(), DayOfWeek.FRIDAY);
        Assert.assertEquals(userSchedule.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(userSchedule.getRanges().get(0).getEndTime(), LocalTime.of(13, 0));
        Assert.assertEquals(userSchedule.getRanges().get(1).getStartTime(), LocalTime.of(15, 0));
        Assert.assertEquals(userSchedule.getRanges().get(1).getEndTime(), LocalTime.of(17, 0));


        result = this.mockMvc
                .perform(post("/schedules/users/" + admin.getUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(List.of(new ScheduleRangeDTO(DayOfWeek.FRIDAY, LocalTime.of(20, 0), LocalTime.of(21, 0)))))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        userSchedule = objectMapper.readValue(result.getResponse().getContentAsString(), ScheduleDTO.class);
        Assert.assertEquals(userSchedule.getUser(), UUID.fromString(admin.getUID()));
        Assert.assertEquals(userSchedule.getRanges().size(), 3);
        Assert.assertEquals(userSchedule.getRanges().get(0).getDayOfWeek(), DayOfWeek.FRIDAY);
        Assert.assertEquals(userSchedule.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(userSchedule.getRanges().get(0).getEndTime(), LocalTime.of(13, 0));
        Assert.assertEquals(userSchedule.getRanges().get(1).getStartTime(), LocalTime.of(15, 0));
        Assert.assertEquals(userSchedule.getRanges().get(1).getEndTime(), LocalTime.of(17, 0));
        Assert.assertEquals(userSchedule.getRanges().get(2).getStartTime(), LocalTime.of(20, 0));
        Assert.assertEquals(userSchedule.getRanges().get(2).getEndTime(), LocalTime.of(21, 0));

        result = this.mockMvc
                .perform(post("/schedules/users/" + admin.getUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(List.of(new ScheduleRangeDTO(DayOfWeek.FRIDAY, LocalTime.of(22, 0), LocalTime.of(23, 0)))))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();


        userSchedule = objectMapper.readValue(result.getResponse().getContentAsString(), ScheduleDTO.class);
        Assert.assertEquals(userSchedule.getUser(), UUID.fromString(admin.getUID()));
        Assert.assertEquals(userSchedule.getRanges().size(), 4);
        Assert.assertEquals(userSchedule.getRanges().get(0).getDayOfWeek(), DayOfWeek.FRIDAY);
        Assert.assertEquals(userSchedule.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(userSchedule.getRanges().get(0).getEndTime(), LocalTime.of(13, 0));
        Assert.assertEquals(userSchedule.getRanges().get(1).getStartTime(), LocalTime.of(15, 0));
        Assert.assertEquals(userSchedule.getRanges().get(1).getEndTime(), LocalTime.of(17, 0));
        Assert.assertEquals(userSchedule.getRanges().get(2).getStartTime(), LocalTime.of(20, 0));
        Assert.assertEquals(userSchedule.getRanges().get(2).getEndTime(), LocalTime.of(21, 0));
        Assert.assertEquals(userSchedule.getRanges().get(3).getStartTime(), LocalTime.of(22, 0));
        Assert.assertEquals(userSchedule.getRanges().get(3).getEndTime(), LocalTime.of(23, 0));

        result = this.mockMvc
                .perform(post("/schedules/users/" + admin.getUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(List.of(new ScheduleRangeDTO(DayOfWeek.FRIDAY, LocalTime.of(21, 30), LocalTime.of(23, 30)))))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();


        userSchedule = objectMapper.readValue(result.getResponse().getContentAsString(), ScheduleDTO.class);
        Assert.assertEquals(userSchedule.getUser(), UUID.fromString(admin.getUID()));
        Assert.assertEquals(userSchedule.getRanges().size(), 4);
        Assert.assertEquals(userSchedule.getRanges().get(0).getDayOfWeek(), DayOfWeek.FRIDAY);
        Assert.assertEquals(userSchedule.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(userSchedule.getRanges().get(0).getEndTime(), LocalTime.of(13, 0));
        Assert.assertEquals(userSchedule.getRanges().get(1).getStartTime(), LocalTime.of(15, 0));
        Assert.assertEquals(userSchedule.getRanges().get(1).getEndTime(), LocalTime.of(17, 0));
        Assert.assertEquals(userSchedule.getRanges().get(2).getStartTime(), LocalTime.of(20, 0));
        Assert.assertEquals(userSchedule.getRanges().get(2).getEndTime(), LocalTime.of(21, 0));
        Assert.assertEquals(userSchedule.getRanges().get(3).getStartTime(), LocalTime.of(21, 30));
        Assert.assertEquals(userSchedule.getRanges().get(3).getEndTime(), LocalTime.of(23, 30));
    }


    @Test(dependsOnMethods = "setUserSchedule")
    public void getSchedule() throws Exception {
        final MvcResult result = this.mockMvc
                .perform(get("/schedules/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final ScheduleDTO userSchedule = objectMapper.readValue(result.getResponse().getContentAsString(), ScheduleDTO.class);
        Assert.assertEquals(userSchedule.getUser(), UUID.fromString(admin.getUID()));
        Assert.assertEquals(userSchedule.getRanges().size(), 4);
        Assert.assertEquals(userSchedule.getRanges().get(0).getDayOfWeek(), DayOfWeek.FRIDAY);
        Assert.assertEquals(userSchedule.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(userSchedule.getRanges().get(0).getEndTime(), LocalTime.of(13, 0));
        Assert.assertEquals(userSchedule.getRanges().get(1).getStartTime(), LocalTime.of(15, 0));
        Assert.assertEquals(userSchedule.getRanges().get(1).getEndTime(), LocalTime.of(17, 0));
        Assert.assertEquals(userSchedule.getRanges().get(2).getStartTime(), LocalTime.of(20, 0));
        Assert.assertEquals(userSchedule.getRanges().get(2).getEndTime(), LocalTime.of(21, 0));
        Assert.assertEquals(userSchedule.getRanges().get(3).getStartTime(), LocalTime.of(21, 30));
        Assert.assertEquals(userSchedule.getRanges().get(3).getEndTime(), LocalTime.of(23, 30));
    }


    @Test(dependsOnMethods = "setUserSchedule")
    public void getScheduleFromUser() throws Exception {
        final MvcResult result = this.mockMvc
                .perform(get("/schedules/users/" + admin.getUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final ScheduleDTO userSchedule = objectMapper.readValue(result.getResponse().getContentAsString(), ScheduleDTO.class);
        Assert.assertEquals(userSchedule.getUser(), UUID.fromString(admin.getUID()));
        Assert.assertEquals(userSchedule.getRanges().size(), 4);
        Assert.assertEquals(userSchedule.getRanges().get(0).getDayOfWeek(), DayOfWeek.FRIDAY);
        Assert.assertEquals(userSchedule.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(userSchedule.getRanges().get(0).getEndTime(), LocalTime.of(13, 0));
        Assert.assertEquals(userSchedule.getRanges().get(1).getStartTime(), LocalTime.of(15, 0));
        Assert.assertEquals(userSchedule.getRanges().get(1).getEndTime(), LocalTime.of(17, 0));
        Assert.assertEquals(userSchedule.getRanges().get(2).getStartTime(), LocalTime.of(20, 0));
        Assert.assertEquals(userSchedule.getRanges().get(2).getEndTime(), LocalTime.of(21, 0));
        Assert.assertEquals(userSchedule.getRanges().get(3).getStartTime(), LocalTime.of(21, 30));
        Assert.assertEquals(userSchedule.getRanges().get(3).getEndTime(), LocalTime.of(23, 30));
    }


    @Test(dependsOnMethods = "setUserSchedule")
    public void getScheduleFromInvalidUser() throws Exception {
        final MvcResult result = this.mockMvc
                .perform(get("/schedules/users/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final ScheduleDTO userSchedule = objectMapper.readValue(result.getResponse().getContentAsString(), ScheduleDTO.class);
        Assert.assertEquals(userSchedule.getRanges().size(), 0);
    }


    @Test(dependsOnMethods = {"addExtraSchedule", "getSchedule", "getScheduleFromUser"})
    public void removeScheduleRange() throws Exception {
        final MvcResult result = this.mockMvc
                .perform(delete("/schedules/users/" + admin.getUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(List.of(new ScheduleRangeDTO(DayOfWeek.FRIDAY, LocalTime.of(12, 0), LocalTime.of(16, 0)))))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final ScheduleDTO userSchedule = objectMapper.readValue(result.getResponse().getContentAsString(), ScheduleDTO.class);
        Assert.assertEquals(userSchedule.getUser(), UUID.fromString(admin.getUID()));
        Assert.assertEquals(userSchedule.getRanges().size(), 4);
        Assert.assertEquals(userSchedule.getRanges().get(0).getDayOfWeek(), DayOfWeek.FRIDAY);
        Assert.assertEquals(userSchedule.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(userSchedule.getRanges().get(0).getEndTime(), LocalTime.of(12, 0));
        Assert.assertEquals(userSchedule.getRanges().get(1).getStartTime(), LocalTime.of(16, 0));
        Assert.assertEquals(userSchedule.getRanges().get(1).getEndTime(), LocalTime.of(17, 0));
        Assert.assertEquals(userSchedule.getRanges().get(2).getStartTime(), LocalTime.of(20, 0));
        Assert.assertEquals(userSchedule.getRanges().get(2).getEndTime(), LocalTime.of(21, 0));
        Assert.assertEquals(userSchedule.getRanges().get(3).getStartTime(), LocalTime.of(21, 30));
        Assert.assertEquals(userSchedule.getRanges().get(3).getEndTime(), LocalTime.of(23, 30));
    }


    @Test(dependsOnMethods = {"removeScheduleRange"})
    public void removeExactScheduleRange() throws Exception {
        final MvcResult result = this.mockMvc
                .perform(delete("/schedules/users/" + admin.getUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(List.of(new ScheduleRangeDTO(DayOfWeek.FRIDAY, LocalTime.of(21, 30), LocalTime.of(23, 30)))))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final ScheduleDTO userSchedule = objectMapper.readValue(result.getResponse().getContentAsString(), ScheduleDTO.class);
        Assert.assertEquals(userSchedule.getUser(), UUID.fromString(admin.getUID()));
        Assert.assertEquals(userSchedule.getRanges().size(), 3);
        Assert.assertEquals(userSchedule.getRanges().get(0).getDayOfWeek(), DayOfWeek.FRIDAY);
        Assert.assertEquals(userSchedule.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(userSchedule.getRanges().get(0).getEndTime(), LocalTime.of(12, 0));
        Assert.assertEquals(userSchedule.getRanges().get(1).getStartTime(), LocalTime.of(16, 0));
        Assert.assertEquals(userSchedule.getRanges().get(1).getEndTime(), LocalTime.of(17, 0));
        Assert.assertEquals(userSchedule.getRanges().get(2).getStartTime(), LocalTime.of(20, 0));
        Assert.assertEquals(userSchedule.getRanges().get(2).getEndTime(), LocalTime.of(21, 0));
    }


    @Test(dependsOnMethods = {"removeExactScheduleRange"})
    public void removeScheduleRangeById() throws Exception {
        Schedule schedule = scheduleProvider.findByUser(UUID.fromString(admin.getUID())).orElse(null);
        Assert.assertNotNull(schedule);

        this.mockMvc
                .perform(delete("/schedules/users/me/ids?id=" + schedule.getRanges().get(2).getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        schedule = scheduleProvider.findByUser(UUID.fromString(admin.getUID())).orElse(null);
        Assert.assertNotNull(schedule);

        Assert.assertEquals(schedule.getUser(), UUID.fromString(admin.getUID()));
        Assert.assertEquals(schedule.getRanges().size(), 2);
        Assert.assertEquals(schedule.getRanges().get(0).getDayOfWeek(), DayOfWeek.FRIDAY);
        Assert.assertEquals(schedule.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(schedule.getRanges().get(0).getEndTime(), LocalTime.of(12, 0));
        Assert.assertEquals(schedule.getRanges().get(1).getStartTime(), LocalTime.of(16, 0));
        Assert.assertEquals(schedule.getRanges().get(1).getEndTime(), LocalTime.of(17, 0));
    }


    @Test(dependsOnMethods = {"removeScheduleRangeById"})
    public void updateScheduleRangeNoOverlap() throws Exception {
        Schedule schedule = scheduleProvider.findByUser(UUID.fromString(admin.getUID())).orElse(null);
        Assert.assertNotNull(schedule);

        Assert.assertEquals(schedule.getRanges().size(), 2);
        Assert.assertEquals(schedule.getRanges().get(0).getDayOfWeek(), DayOfWeek.FRIDAY);
        Assert.assertEquals(schedule.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(schedule.getRanges().get(0).getEndTime(), LocalTime.of(12, 0));
        Assert.assertEquals(schedule.getRanges().get(1).getStartTime(), LocalTime.of(16, 0));
        Assert.assertEquals(schedule.getRanges().get(1).getEndTime(), LocalTime.of(17, 0));

        //Change last range.
        final ScheduleRange scheduleRange = schedule.getRanges().get(1);
        scheduleRange.setEndTime(LocalTime.of(18, 30));

        this.mockMvc
                .perform(put("/schedules/users/me/ranges")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(scheduleRange))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        schedule = scheduleProvider.findByUser(UUID.fromString(admin.getUID())).orElse(null);
        Assert.assertNotNull(schedule);

        Assert.assertEquals(schedule.getUser(), UUID.fromString(admin.getUID()));
        Assert.assertEquals(schedule.getRanges().size(), 2);
        Assert.assertEquals(schedule.getRanges().get(0).getDayOfWeek(), DayOfWeek.FRIDAY);
        Assert.assertEquals(schedule.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(schedule.getRanges().get(0).getEndTime(), LocalTime.of(12, 0));
        Assert.assertEquals(schedule.getRanges().get(1).getStartTime(), LocalTime.of(16, 0));
        Assert.assertEquals(schedule.getRanges().get(1).getEndTime(), LocalTime.of(18, 30));
    }


    @Test(dependsOnMethods = {"updateScheduleRangeNoOverlap"})
    public void updateScheduleRangeWithOverlap() throws Exception {
        Schedule schedule = scheduleProvider.findByUser(UUID.fromString(admin.getUID())).orElse(null);
        Assert.assertNotNull(schedule);

        Assert.assertEquals(schedule.getRanges().size(), 2);
        Assert.assertEquals(schedule.getRanges().get(0).getDayOfWeek(), DayOfWeek.FRIDAY);
        Assert.assertEquals(schedule.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(schedule.getRanges().get(0).getEndTime(), LocalTime.of(12, 0));
        Assert.assertEquals(schedule.getRanges().get(1).getStartTime(), LocalTime.of(16, 0));
        Assert.assertEquals(schedule.getRanges().get(1).getEndTime(), LocalTime.of(18, 30));

        //Change last range.
        final ScheduleRange scheduleRange = schedule.getRanges().get(0);
        scheduleRange.setEndTime( LocalTime.of(16, 30));

        this.mockMvc
                .perform(put("/schedules/users/me/ranges")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(scheduleRange))
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        schedule = scheduleProvider.findByUser(UUID.fromString(admin.getUID())).orElse(null);
        Assert.assertNotNull(schedule);

        Assert.assertEquals(schedule.getUser(), UUID.fromString(admin.getUID()));
        Assert.assertEquals(schedule.getRanges().size(), 1);
        Assert.assertEquals(schedule.getRanges().get(0).getDayOfWeek(), DayOfWeek.FRIDAY);
        Assert.assertEquals(schedule.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(schedule.getRanges().get(0).getEndTime(), LocalTime.of(18, 30));
    }


    @Test(dependsOnMethods = "updateScheduleRangeWithOverlap")
    public void removeAllScheduleRange() throws Exception {
        final MvcResult result = this.mockMvc
                .perform(delete("/schedules/users/" + admin.getUID() + "/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final ScheduleDTO userSchedule = objectMapper.readValue(result.getResponse().getContentAsString(), ScheduleDTO.class);
        Assert.assertEquals(userSchedule.getUser(), UUID.fromString(admin.getUID()));
        Assert.assertEquals(userSchedule.getRanges().size(), 0);
    }


    @Test(dependsOnMethods = "setAdminAuthentication")
    public void getDefaultSchedule() throws Exception {
        final MvcResult result = this.mockMvc
                .perform(get("/schedules/default")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final ScheduleDTO userSchedule = objectMapper.readValue(result.getResponse().getContentAsString(), ScheduleDTO.class);
        Assert.assertEquals(userSchedule.getRanges().size(), 5);
        Assert.assertEquals(userSchedule.getRanges().get(0).getDayOfWeek(), DayOfWeek.MONDAY);
        Assert.assertEquals(userSchedule.getRanges().get(0).getStartTime(), LocalTime.of(9, 0));
        Assert.assertEquals(userSchedule.getRanges().get(0).getEndTime(), LocalTime.of(18, 0));
    }


}
