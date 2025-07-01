package com.biit.appointment.core.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public final class ObjectMapperFactory {
    private static final int SECONDS_MAX_DECIMALS = 9;

    private static ObjectMapper objectMapper;

    private ObjectMapperFactory() {

    }

    public static ObjectMapper getNewObjectMapper() {
        final JavaTimeModule module = new JavaTimeModule();

        final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                .appendFraction(ChronoField.MILLI_OF_SECOND, 0, SECONDS_MAX_DECIMALS, true)
                .toFormatter();

        final LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(formatter);
        module.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
        final ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(module)
                .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        return objectMapper;
    }

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = getNewObjectMapper();
        }
        return objectMapper;
    }
}
