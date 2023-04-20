package com.biit.appointment.persistence.entities;

import com.biit.server.persistence.entities.Element;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Table(name = "appointment_type", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "organization_id"})})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AppointmentType extends Element implements Comparable<AppointmentType> {

    @Column(nullable = false)
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
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (int) (organizationId ^ (organizationId >>> 32));
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
