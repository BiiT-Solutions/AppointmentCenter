package com.biit.appointment.rest;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.core.env.Environment;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableJpaRepositories(entityManagerFactoryRef = "appointmentCenterFactory", transactionManagerRef = "appointmentCenterTransactionManager", basePackages = {
        DatabaseConfiguration.PACKAGE})
public class DatabaseConfiguration {
    public static final String PACKAGE = "com.biit.appointment.persistence";

    @Autowired
    private Environment environment;

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.appointments.datasource")
    public DataSource appointmentCenterDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean appointmentCenterFactory(EntityManagerFactoryBuilder builder,
                                                                          @Qualifier("appointmentCenterDataSource") DataSource dataSource) {
        final HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", environment.getProperty("spring.appointments.datasource.hibernate.ddl-auto"));
        return builder.dataSource(dataSource).properties(properties).packages(PACKAGE).build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager appointmentCenterTransactionManager(@Qualifier ("appointmentCenterFactory")
                                                                              EntityManagerFactory appointmentCenterFactory) {
        return new JpaTransactionManager(appointmentCenterFactory);
    }
}
