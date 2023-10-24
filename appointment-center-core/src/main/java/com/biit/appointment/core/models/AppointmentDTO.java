package com.biit.appointment.core.models;

import com.biit.appointment.persistence.entities.AppointmentStatus;
import com.biit.server.controllers.models.ElementDTO;

import java.time.LocalDateTime;

public class AppointmentDTO extends ElementDTO<Long> {

    private Long doctorId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long organizationId;

    private ExaminationTypeDTO examinationType;

    private Long customerId;

    private AppointmentStatus status = AppointmentStatus.NOT_STARTED;

    private String anamneseForm = null;

    private Integer anamneseVersion = null;

    private String flowableProcessInstanceId;

    private String orbeonDocumentId;

    private String intakeFormId;

    private Long cost;

    private boolean deleted = false;

    private LocalDateTime finishedTime = null;

    private String conclusion;

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
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

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getAnamneseForm() {
        return anamneseForm;
    }

    public void setAnamneseForm(String anamneseForm) {
        this.anamneseForm = anamneseForm;
    }

    public Integer getAnamneseVersion() {
        return anamneseVersion;
    }

    public void setAnamneseVersion(Integer anamneseVersion) {
        this.anamneseVersion = anamneseVersion;
    }

    public String getFlowableProcessInstanceId() {
        return flowableProcessInstanceId;
    }

    public void setFlowableProcessInstanceId(String flowableProcessInstanceId) {
        this.flowableProcessInstanceId = flowableProcessInstanceId;
    }

    public String getOrbeonDocumentId() {
        return orbeonDocumentId;
    }

    public void setOrbeonDocumentId(String orbeonDocumentId) {
        this.orbeonDocumentId = orbeonDocumentId;
    }

    public String getIntakeFormId() {
        return intakeFormId;
    }

    public void setIntakeFormId(String intakeFormId) {
        this.intakeFormId = intakeFormId;
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

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }
}
