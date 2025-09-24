package com.biit.appointment.rest.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Service;

@SpringBootApplication
@Service
@ComponentScan(basePackages = {"com.biit.appointment", "com.biit.server.security", "com.biit.server", "com.biit.messagebird.client", "com.biit.usermanager.client"})
@ConfigurationPropertiesScan({"com.biit.appointment.rest"})
@EntityScan({"com.biit.appointment.persistence.entities", "com.biit.server"})
@EnableJpaRepositories("com.biit.appointment.persistence.repositories")
public class Server {
    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }
}
