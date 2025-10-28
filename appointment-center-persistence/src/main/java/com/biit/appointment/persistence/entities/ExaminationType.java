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

import com.biit.database.encryption.DoubleCryptoConverter;
import com.biit.server.persistence.entities.Element;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


@Entity
@Table(name = "examination_type", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "organization_id"})},
        indexes = {
                @Index(name = "ind_name", columnList = "name"),
                @Index(name = "ind_organization", columnList = "organization_id"),
        })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ExaminationType extends Element<String> implements Comparable<ExaminationType> {

    @Id
    @Column(nullable = false, length = MAX_UNIQUE_COLUMN_LENGTH)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "appointment_type")
    private AppointmentType appointmentType;

    @Column(name = "organization_id")
    private String organizationId;

    @Convert(converter = DoubleCryptoConverter.class)
    private Double price;

    private boolean deleted;

    @Column(name = "appointment_overlaps_allowed")
    private boolean appointmentOverlapsAllowed = false;

    @Override
    public String getId() {
        return name;
    }

    @Override
    public void setId(String name) {
        this.name = name;
    }

    public ExaminationType() {
        super();
        setDeleted(false);
    }

    public ExaminationType(String name, String organizationId, AppointmentType appointmentType) {
        this();
        setName(name);
        setAppointmentType(appointmentType);
        setOrganizationId(organizationId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return getName() + " (" + getAppointmentType() + ")";
    }

    @Override
    public int compareTo(ExaminationType arg0) {
        return getName().compareTo(arg0.name);
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public AppointmentType getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(AppointmentType appointmentType) {
        this.appointmentType = appointmentType;
    }

    public boolean isAppointmentOverlapsAllowed() {
        return appointmentOverlapsAllowed;
    }

    public void setAppointmentOverlapsAllowed(boolean appointmentOverlapsAllowed) {
        this.appointmentOverlapsAllowed = appointmentOverlapsAllowed;
    }

}
