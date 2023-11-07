package com.biit.appointment.core.models;

import com.biit.appointment.persistence.entities.AppointmentStatus;
import com.biit.server.controllers.models.ElementDTO;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public class AppointmentDTO extends ElementDTO<Long> {

    private Long id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long organizationId;

    private ExaminationTypeDTO examinationType;

    private Set<Long> attendees;

    private AppointmentStatus status = AppointmentStatus.NOT_STARTED;

    private Long cost;

    private boolean deleted = false;

    private LocalDateTime finishedTime = null;

    private Collection<CustomPropertyDTO> customProperties;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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

    public Set<Long> getAttendees() {
        return attendees;
    }

    public void setAttendees(Set<Long> attendees) {
        this.attendees = attendees;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(LocalDateTime finishedTime) {
        this.finishedTime = finishedTime;
    }

    public Collection<CustomPropertyDTO> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Collection<CustomPropertyDTO> customProperties) {
        this.customProperties = customProperties;
    }
}
