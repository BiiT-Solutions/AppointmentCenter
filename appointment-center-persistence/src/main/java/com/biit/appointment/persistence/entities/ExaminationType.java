package com.biit.appointment.persistence.entities;

import com.biit.database.encryption.DoubleCryptoConverter;
import com.biit.server.persistence.entities.Element;
import jakarta.persistence.Cacheable;
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
public class ExaminationType extends Element<Long> implements Comparable<ExaminationType> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = MAX_UNIQUE_COLUMN_LENGTH)
    private String name;

    @Column(nullable = false, length = MAX_UNIQUE_COLUMN_LENGTH)
    private String translation;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "appointment_type")
    private AppointmentType appointmentType;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @Convert(converter = DoubleCryptoConverter.class)
    private Double price;

    private boolean deleted;

    @Column(name = "appointment_overlaps_allowed")
    private boolean appointmentOverlapsAllowed = false;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ExaminationType() {
        super();
        setDeleted(false);
    }

    public ExaminationType(String name, String translation, Long organizationId, AppointmentType appointmentType) {
        this();
        setName(name);
        setTranslation(translation);
        setAppointmentType(appointmentType);
        setOrganizationId(organizationId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
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
        return getTranslation().compareTo(arg0.getTranslation());
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
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
