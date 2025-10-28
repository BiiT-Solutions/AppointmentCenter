package com.biit.appointment.persistence.entities;

/*-
 * #%L
 * AppointmentCenter (Persistence)
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

import com.biit.database.encryption.StringCryptoConverter;
import com.biit.server.persistence.entities.StorableObject;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.springframework.beans.BeanUtils;

@Entity
@Table(name = "custom_properties", indexes = {
        @Index(name = "ind_key", columnList = "property_key"),
        @Index(name = "ind_value", columnList = "property_value"),
})
public class CustomProperty extends StorableObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment", nullable = false)
    private Appointment appointment;

    @Column(name = "property_key")
    @Convert(converter = StringCryptoConverter.class)
    private String key;

    @Column(name = "property_value")
    @Convert(converter = StringCryptoConverter.class)
    private String value;

    public CustomProperty() {
        super();
    }

    public CustomProperty(Appointment appointment, String key, String value) {
        this();
        this.appointment = appointment;
        this.key = key;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static CustomProperty copy(CustomProperty source) {
        final CustomProperty customProperty = new CustomProperty();
        BeanUtils.copyProperties(source, customProperty);
        customProperty.setId(null);
        customProperty.setAppointment(source.getAppointment());
        return customProperty;
    }
}
