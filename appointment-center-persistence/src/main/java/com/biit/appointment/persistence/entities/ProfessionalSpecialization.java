package com.biit.appointment.persistence.entities;

import com.biit.server.persistence.entities.Element;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.UUID;

@Entity
@Table(name = "professional_specialization", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "organization_id"})},
        indexes = {
                @Index(name = "ind_name", columnList = "name"),
                @Index(name = "ind_organization", columnList = "organization_id"),
        })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ProfessionalSpecialization extends Element<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "appointment_type")
    private AppointmentType appointmentType;

    @Column(name = "organization_id")
    private String organizationId;

    @Column(name = "user_uuid", nullable = false)
    private UUID user;

    public ProfessionalSpecialization() {
        super();
    }

    public ProfessionalSpecialization(String name, AppointmentType appointmentType, String organizationId, UUID user) {
        this();
        this.name = name;
        this.appointmentType = appointmentType;
        this.organizationId = organizationId;
        this.user = user;
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

    public AppointmentType getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(AppointmentType appointmentType) {
        this.appointmentType = appointmentType;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public UUID getUser() {
        return user;
    }

    public void setUser(UUID userUUID) {
        this.user = userUUID;
    }

    @Override
    public String toString() {
        return "ProfessionalSpecialization{"
                + "appointmentType=" + appointmentType
                + ", userId=" + user
                + '}';
    }
}
