package com.biit.appointment.core.models;

import com.biit.server.controllers.models.ElementDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

public class AppointmentTemplateDTO extends ElementDTO<Long> {

    private Long id;

    @NotNull
    @Size(min = ElementDTO.MIN_FIELD_LENGTH, max = ElementDTO.MAX_NORMAL_FIELD_LENGTH)
    private String title;

    @Size(max = ElementDTO.MAX_BIG_FIELD_LENGTH)
    private String description;

    private int duration;

    private String organizationId;

    private ExaminationTypeDTO examinationType;

    private Set<UUID> speakers;

    private Double cost;

    private String colorTheme;

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

    public ExaminationTypeDTO getExaminationType() {
        return examinationType;
    }

    public void setExaminationType(ExaminationTypeDTO examinationType) {
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
}
