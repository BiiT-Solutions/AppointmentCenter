package com.biit.appointment.core.test;

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
