package com.biit.appointment.core.models;

import com.biit.appointment.persistence.entities.RecurrenceFrequency;
import com.biit.server.controllers.models.ElementDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public class RecurrenceDTO extends ElementDTO<Long> {

    private Long id;

    private Long organizerId;

    private Long organizationId;

    private ExaminationTypeDTO examinationType;

    private Set<AppointmentDTO> appointments;

    private RecurrenceFrequency frequency;

    private LocalDateTime startsAt;

    private LocalDateTime endsAt;

    private Set<LocalDate> skippedIterations;


    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Set<AppointmentDTO> getAppointments() {
        return appointments;
    }

    public void setAppointments(Set<AppointmentDTO> appointments) {
        this.appointments = appointments;
    }

    public RecurrenceFrequency getFrequency() {
        return frequency;
    }

    public void setFrequency(RecurrenceFrequency frequency) {
        this.frequency = frequency;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(LocalDateTime startsAt) {
        this.startsAt = startsAt;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(LocalDateTime endsAt) {
        this.endsAt = endsAt;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public ExaminationTypeDTO getExaminationType() {
        return examinationType;
    }

    public void setExaminationType(ExaminationTypeDTO examinationType) {
        this.examinationType = examinationType;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Set<LocalDate> getSkippedIterations() {
        return skippedIterations;
    }

    public void setSkippedIterations(Set<LocalDate> skippedIterations) {
        this.skippedIterations = skippedIterations;
    }
}
