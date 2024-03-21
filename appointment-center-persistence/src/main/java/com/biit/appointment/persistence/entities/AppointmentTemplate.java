package com.biit.appointment.persistence.entities;

import com.biit.database.encryption.DoubleCryptoConverter;
import com.biit.database.encryption.IntegerCryptoConverter;
import com.biit.database.encryption.LongCryptoConverter;
import com.biit.database.encryption.StringCryptoConverter;
import com.biit.server.persistence.entities.Element;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Set;

@Entity
@Table(name = "appointment_templates")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AppointmentTemplate extends Element<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    @Convert(converter = StringCryptoConverter.class)
    private String title;

    @Lob
    @Column(name = "description")
    @Convert(converter = StringCryptoConverter.class)
    private String description;

    //In minutes
    @Column(name = "duration")
    @Convert(converter = IntegerCryptoConverter.class)
    private int duration;

    @Column(name = "organization_id")
    @Convert(converter = LongCryptoConverter.class)
    private Long organizationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "examination_type")
    private ExaminationType examinationType;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "appointment_template_speakers")
    @Fetch(value = FetchMode.SUBSELECT)
    @Column(name = "speaker_id", nullable = false)
    private Set<Long> speakers;

    @Convert(converter = DoubleCryptoConverter.class)
    @Column(name = "cost")
    private Double cost;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public ExaminationType getExaminationType() {
        return examinationType;
    }

    public void setExaminationType(ExaminationType examinationType) {
        this.examinationType = examinationType;
    }

    public Set<Long> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(Set<Long> speakers) {
        this.speakers = speakers;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}
