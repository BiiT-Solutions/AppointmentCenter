package com.biit.appointment.rest;

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

    @Bean(name = "appointmentCenterFactory")
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
