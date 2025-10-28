package com.biit.appointment.core.test;

/*-
 * #%L
 * AppointmentCenter (Core)
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

import com.biit.appointment.core.converters.ExternalCalendarCredentialsConverter;
import com.biit.appointment.core.converters.models.ExternalCalendarCredentialsConverterRequest;
import com.biit.appointment.core.models.ExternalCalendarCredentialsDTO;
import com.biit.appointment.persistence.entities.ExternalCalendarCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest
@Test(groups = {"externalCredentials"})
public class ExternalCredentialsTests extends AbstractTestNGSpringContextTests {

    private final UUID userUuid = UUID.randomUUID();

    @Autowired
    private ExternalCalendarCredentialsConverter externalCalendarCredentialsConverter;


    @Test
    public void convertAndConvertBack() throws URISyntaxException, IOException {
        ExternalCalendarCredentialsDTO externalCalendarCredentialsDTO = new ExternalCalendarCredentialsDTO();
        externalCalendarCredentialsDTO.setUserId(userUuid);
        externalCalendarCredentialsDTO.setExpiresAt(LocalDateTime.now().plusHours(1));
        externalCalendarCredentialsDTO.setForceRefreshAt(LocalDateTime.now().plusDays(1));
        externalCalendarCredentialsDTO.setUserCredentials(new String(Files.readAllBytes(Paths.get(getClass().getClassLoader()
                .getResource("credential.txt").toURI()))).trim());

        ExternalCalendarCredentials externalCalendarCredentials = externalCalendarCredentialsConverter.reverse(externalCalendarCredentialsDTO);
        ExternalCalendarCredentialsDTO externalCalendarCredentialsDTO2 = externalCalendarCredentialsConverter.convert(new ExternalCalendarCredentialsConverterRequest(externalCalendarCredentials));

        Assert.assertEquals(externalCalendarCredentialsDTO.getCreatedAt(), externalCalendarCredentialsDTO2.getCreatedAt());
    }
}
