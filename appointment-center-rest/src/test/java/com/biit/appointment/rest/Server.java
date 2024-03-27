package com.biit.appointment.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;

@SpringBootApplication
@Service
@ComponentScan({"com.biit.appointment", "com.biit.kafka"})
@ConfigurationPropertiesScan({"com.biit.appointment.rest", "com.biit.server.time"})
@EntityScan({"com.biit.appointment.persistence.entities", "com.biit.server"})
public class Server {
    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }
}
