package com.biit.appointment.persistence.entities;

import com.biit.server.persistence.entities.Element;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


@Entity
@Table(name = "appointment_type", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "organization_id"})},
        indexes = {
                @Index(name = "ind_name", columnList = "name"),
                @Index(name = "ind_organization", columnList = "organization_id"),
        })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AppointmentType extends Element<Long> implements Comparable<AppointmentType> {
    private static final int HASH_SEED = 31;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "organization_id", nullable = false)
    private long organizationId;

    public AppointmentType() {
        super();
    }

    public AppointmentType(String name, long organizationId) {
        this();
        setName(name);
        setOrganizationId(organizationId);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public String toString() {
        return "{" + getName() + " (" + getOrganizationId() + ")}";
    }

    @Override
    public int hashCode() {
        final int prime = HASH_SEED;
        int result = super.hashCode();
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (int) (organizationId ^ (organizationId >>> (HASH_SEED + 1)));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AppointmentType other = (AppointmentType) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (organizationId != other.organizationId) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(AppointmentType appointmentType) {
        return getName().compareTo(appointmentType.getName());
    }

}
