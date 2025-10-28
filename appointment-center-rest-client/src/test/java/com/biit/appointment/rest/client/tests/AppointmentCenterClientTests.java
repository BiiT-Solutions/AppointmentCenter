package com.biit.appointment.rest.client.tests;

/*-
 * #%L
 * AppointmentCenter (Rest Client)
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

import com.biit.appointment.core.models.QrCodeDTO;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.core.providers.AppointmentTypeProvider;
import com.biit.appointment.core.providers.AttendanceProvider;
import com.biit.appointment.core.providers.ExaminationTypeProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.rest.client.AppointmentCenterClient;
import com.biit.server.security.model.IAuthenticatedUser;
import com.biit.usermanager.client.providers.AuthenticatedUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

//@SpringBootTest(webEnvironment = DEFINED_PORT, classes = AppointmentServicesServer.class)
//@Test(groups = {"appointmentClient"})
public class AppointmentCenterClientTests extends AbstractTestNGSpringContextTests {
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
    private AppointmentTypeProvider appointmentTypeProvider;

    @Autowired
    private ExaminationTypeProvider examinationTypeProvider;

    @Autowired
    private AppointmentProvider appointmentProvider;

    @Autowired
    private AppointmentCenterClient appointmentCenterClient;

    @Autowired
    private AttendanceProvider attendanceProvider;

    @Value("${jwt.user:null}")
    private String jwtUser;

    @Value("${jwt.password:null}")
    private String jwtPassword;

    private Appointment appointment;

    private IAuthenticatedUser admin;

    public static ExaminationType generateExaminationType(String name, AppointmentType appointmentType) {
        return new ExaminationType(name, ORGANIZATION_ID, appointmentType);
    }

    @BeforeClass
    public void generateExaminationType() {
        AppointmentType appointmentType = new AppointmentType(APPOINTMENT_SPECIALTY, ORGANIZATION_ID);
        appointmentType = appointmentTypeProvider.save(appointmentType);
        ExaminationType type = generateExaminationType(TEST_TYPE_NAME, appointmentType);
        examinationTypeProvider.save(type);
    }


    @BeforeClass
    public void addUser() {
        //Create the admin user
        admin = authenticatedUserProvider.createUser(jwtUser, jwtUser, jwtPassword);
    }


    @BeforeClass
    public void createAppointment() {
        final Appointment newAppointment = new Appointment();
        newAppointment.setTitle(APPOINTMENT_TITLE);
        newAppointment.setStartTime(LocalDateTime.of(2024, 3, 27, 16, 38, 3));
        newAppointment.setEndTime(LocalDateTime.of(2024, 3, 27, 18, 38, 3));
        newAppointment.setOrganizer(UUID.fromString(admin.getUID()));
        newAppointment.setAttendees(Collections.singleton(UUID.fromString(admin.getUID())));
        this.appointment = appointmentProvider.save(newAppointment);
    }

    @Test
    public void checkAuthentication() {
        //Check the admin user
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtUser, JWT_SALT + jwtPassword));
    }

    @Test
    public void readAppointments() {
        Assert.assertEquals(appointmentCenterClient.findAll().size(), 1);
    }


    @Test
    public void attend() {
        final Optional<QrCodeDTO> qrCode = appointmentCenterClient.getQrCode(appointment.getId(), UUID.fromString(admin.getUID()));
        Assert.assertTrue(qrCode.isPresent());
        appointmentCenterClient.attendByQrCode(appointment.getId(), qrCode.get());

        //Check user is marked as attending the process.
        Assert.assertEquals(attendanceProvider.findByAttendee(UUID.fromString(admin.getUID())).size(), 1);
    }
}
