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
import com.biit.database.encryption.IntegerCryptoConverter;
import com.biit.database.encryption.SHA512HashGenerator;
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
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "appointment_templates", uniqueConstraints = {@UniqueConstraint(columnNames = {"title", "organization_id"})},
        indexes = {
                @Index(name = "ind_title", columnList = "title"),
                @Index(name = "ind_organization", columnList = "organization_id"),
        })
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AppointmentTemplate extends Element<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    @Convert(converter = StringCryptoConverter.class)
    private String title;

    @Column(name = "title_hash", length = SHA512HashGenerator.ALGORITHM_LENGTH)
    @Convert(converter = SHA512HashGenerator.class)
    private String titleHash;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    @Convert(converter = StringCryptoConverter.class)
    private String description;

    //In minutes
    @Column(name = "duration")
    @Convert(converter = IntegerCryptoConverter.class)
    private int duration;

    @Column(name = "organization_id")
    private String organizationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "examination_type")
    private ExaminationType examinationType;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "appointment_template_speakers")
    @Fetch(value = FetchMode.SUBSELECT)
    @Column(name = "speaker_uuids", nullable = false)
    private Set<UUID> speakers;

    @Convert(converter = DoubleCryptoConverter.class)
    @Column(name = "cost")
    private Double cost;

    //Is a code from the UX to store the color theme for the template.
    @Convert(converter = StringCryptoConverter.class)
    @Column(name = "color_theme")
    private String colorTheme;

    @Convert(converter = StringCryptoConverter.class)
    @Column(name = "infographic_template")
    private String infographicTemplate;


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
        setTitleHash(title);
        if (infographicTemplate == null) {
            setInfographicTemplate(infographicTemplate);
        }
    }

    public String getTitleHash() {
        return titleHash;
    }

    public void setTitleHash(String title) {
        this.titleHash = title;
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

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public ExaminationType getExaminationType() {
        return examinationType;
    }

    public void setExaminationType(ExaminationType examinationType) {
        this.examinationType = examinationType;
    }

    public Set<UUID> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(Set<UUID> speakers) {
        this.speakers = speakers;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getColorTheme() {
        return colorTheme;
    }

    public void setColorTheme(String colorTheme) {
        this.colorTheme = colorTheme;
    }

    public String getInfographicTemplate() {
        return infographicTemplate;
    }

    public void setInfographicTemplate(String infographicTemplate) {
        this.infographicTemplate = infographicTemplate;
    }

    @Override
    public void populateHash() {
        super.populateHash();
        setTitleHash(getTitle());
    }
}
