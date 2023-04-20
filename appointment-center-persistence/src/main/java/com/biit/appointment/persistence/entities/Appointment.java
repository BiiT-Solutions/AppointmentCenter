package com.biit.appointment.persistence.entities;

import com.biit.appointment.logger.AppointmentCenterLogger;
import com.biit.database.encryption.StringCryptoConverter;
import com.biit.server.persistence.entities.Element;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Appointment extends Element implements Comparable<Appointment> {
    public static final int CONCLUSION_MAX_LENGTH = 10000;
    public static final int FLOWABLE_ID_LENGTH = 64;

    @Column(name = "doctor_id")
    private Long doctorId;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "examination_type")
    private ExaminationType examinationType;

    private Long customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.NOT_STARTED;

    @Column(name = "anamnese_form")
    private String anamneseForm = null;

    @Column(name = "anamnese_version")
    private Integer anamneseVersion = null;

    @Column(name = "flowable_process_instance_id", unique = true, length = FLOWABLE_ID_LENGTH)
    private String flowableProcessInstanceId;

    @Column(name = "orbeon_document_id")
    private String orbeonDocumentId;

    @Column(name = "intake_form_id")
    private String intakeFormId;

    private Long cost;

    private boolean deleted = false;

    @Column(name = "finished_time")
    private LocalDateTime finishedTime = null;

    @Lob
    @Column(length = CONCLUSION_MAX_LENGTH)
    @Convert(converter = StringCryptoConverter.class)
    private String conclusion;

    public Appointment() {
        super();
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

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long physiotherapistId) {
        this.doctorId = physiotherapistId;
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

    public boolean isExaminationClosed() {
        return status.isStatusPassed(AppointmentStatus.EXAMINATION_CLOSED);
    }

    public boolean isReportClosed() {
        return status.isStatusPassed(AppointmentStatus.REPORT_CLOSED);
    }

    public String getFlowableProcessInstanceId() {
        return flowableProcessInstanceId;
    }

    public void setFlowableProcessInstanceId(String flowableProcessId) {
        this.flowableProcessInstanceId = flowableProcessId;
    }

    public boolean isStarted() {
        return status.isStatusPassed(AppointmentStatus.STARTED);
    }

    public boolean isInEdition() {
        return status.equals(AppointmentStatus.EDITION_STARTED)
                || status.equals(AppointmentStatus.EXAMINATION_EDITIONS_CLOSED);
    }

    public boolean isFinished() {
        return status.isStatusPassed(AppointmentStatus.REPORT_CLOSED);
    }

    @Override
    public String toString() {
        return "{Appointment Id '" + getId() + "', examination '" + getExaminationType() + "', organizationId '"
                + getOrganizationId() + "', physioterapistId '" + getDoctorId() + "', startTime '"
                + getStartTime() + "', endTime '" + getEndTime() + "', finished '" + isExaminationClosed()
                + "', reportSent '" + isReportClosed() + "', status '" + status + "'}";
    }

    public Integer getAnamneseVersion() {
        return anamneseVersion;
    }

    public void setAnamneseFormVersion(String anameneseVersion) {
        try {
            this.anamneseVersion = Integer.parseInt(anameneseVersion);
        } catch (Exception e) {
            AppointmentCenterLogger.warning(this.getClass().getName(), "Invalid orbeon version!");
        }
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customer) {
        this.customerId = customer;
    }

    public String getOrbeonDocumentId() {
        return orbeonDocumentId;
    }

    public void setOrbeonDocumentId(String orbeonDocumentId) {
        this.orbeonDocumentId = orbeonDocumentId;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isRejected() {
        return doctorId == null;

    }

    public void setFinishedTime() {
        if (finishedTime == null) {
            finishedTime = LocalDateTime.now();
        }
    }

    public Duration getDuration() {
        if (finishedTime == null) {
            return null;
        }
        return Duration.between(startTime, finishedTime);
    }

    @Override
    public int compareTo(Appointment appointment) {
        // This comparator is used in the order for FMS charts and BMI.
        return getStartTime().compareTo(appointment.getStartTime());
    }

    public String getAnamneseForm() {
        return anamneseForm;
    }

    public void setAnamneseFormName(String anamneseForm) {
        this.anamneseForm = anamneseForm;
    }

    public String getIntakeFormId() {
        return intakeFormId;
    }

    public void setIntakeFormId(String intakeFormId) {
        this.intakeFormId = intakeFormId;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

}
