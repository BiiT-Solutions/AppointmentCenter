package com.biit.appointment.rest.api;

/*-
 * #%L
 * AppointmentCenter (Rest)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import com.biit.appointment.core.models.ScheduleRangeExclusionDTO;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
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
@Test(groups = {"scheduleRangeExclusionServiceTests"})
public class ScheduleRangeExclusionServiceTests extends AbstractTestNGSpringContextTests {
    private static final String USER_NAME = "user";
    private static final String USER_PASSWORD = "password";
    private static final String JWT_SALT = "4567";

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
    public void setUserScheduleRangeExclusion() throws Exception {
        this.mockMvc
                .perform(put("/availabilities/exceptions/users/" + admin.getUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(List.of(new ScheduleRangeExclusionDTO(UUID.fromString(admin.getUID()), LocalDate.now()))))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();


        final MvcResult result = this.mockMvc
                .perform(get("/availabilities/exceptions/users/" + admin.getUID())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final List<ScheduleRangeExclusionDTO> userScheduleExceptions = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), ScheduleRangeExclusionDTO[].class));
        Assert.assertEquals(userScheduleExceptions.size(), 1);
        Assert.assertEquals(userScheduleExceptions.get(0).getUser(), UUID.fromString(admin.getUID()));
        Assert.assertEquals(userScheduleExceptions.get(0).getStartTime(), LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
        Assert.assertEquals(userScheduleExceptions.get(0).getEndTime(), LocalDateTime.of(LocalDate.now(), LocalTime.MAX.truncatedTo(ChronoUnit.SECONDS)));
    }


    @Test(dependsOnMethods = "setUserScheduleRangeExclusion")
    public void addUserScheduleRangeExclusion() throws Exception {
        this.mockMvc
                .perform(post("/availabilities/exceptions/users/" + admin.getUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(List.of(new ScheduleRangeExclusionDTO(UUID.fromString(admin.getUID()), LocalDate.now().plusDays(1)))))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();


        final MvcResult result = this.mockMvc
                .perform(get("/availabilities/exceptions/users/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final List<ScheduleRangeExclusionDTO> userScheduleExceptions = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), ScheduleRangeExclusionDTO[].class));
        Assert.assertEquals(userScheduleExceptions.size(), 2);
        Assert.assertEquals(userScheduleExceptions.get(0).getUser(), UUID.fromString(admin.getUID()));
        Assert.assertEquals(userScheduleExceptions.get(0).getStartTime(), LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
        Assert.assertEquals(userScheduleExceptions.get(0).getEndTime(), LocalDateTime.of(LocalDate.now(), LocalTime.MAX.truncatedTo(ChronoUnit.SECONDS)));
        Assert.assertEquals(userScheduleExceptions.get(1).getUser(), UUID.fromString(admin.getUID()));
        Assert.assertEquals(userScheduleExceptions.get(1).getStartTime(), LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN));
        Assert.assertEquals(userScheduleExceptions.get(1).getEndTime(), LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MAX.truncatedTo(ChronoUnit.SECONDS)));
    }


    @Test(dependsOnMethods = "addUserScheduleRangeExclusion")
    public void deleteUserScheduleRangeExclusion() throws Exception {
        this.mockMvc
                .perform(delete("/availabilities/exceptions/users/me/all")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();


        final MvcResult result = this.mockMvc
                .perform(get("/availabilities/exceptions/users/" + admin.getUID())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtToken)
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        final List<ScheduleRangeExclusionDTO> userScheduleExceptions = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), ScheduleRangeExclusionDTO[].class));
        Assert.assertEquals(userScheduleExceptions.size(), 0);
    }

}
