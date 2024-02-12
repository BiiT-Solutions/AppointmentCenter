package com.biit.appointment.core.models;

import com.biit.server.controllers.models.ElementDTO;

import java.util.Set;

public class AppointmentTemplateDTO extends ElementDTO<Long> {

    private Long id;

    private String title;

    private String description;

    private int duration;

    private Long organizationId;

    private ExaminationTypeDTO examinationType;

    private Set<Long> speakers;

    private Long cost;

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

    public ExaminationTypeDTO getExaminationType() {
        return examinationType;
    }

    public void setExaminationType(ExaminationTypeDTO examinationType) {
        this.examinationType = examinationType;
    }

    public Set<Long> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(Set<Long> speakers) {
        this.speakers = speakers;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }
}
