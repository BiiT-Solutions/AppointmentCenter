package com.biit.appointment.core.models;

import com.biit.server.controllers.models.ElementDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class RecurrenceDTO extends ElementDTO<Long> {

    private Long id;

    @NotNull
    private UUID organizer;

    @NotNull
    @Size(min = ElementDTO.MIN_FIELD_LENGTH)
    private String organizationId;

    @Valid
    @NotNull
    private ExaminationTypeDTO examinationType;

    private Set<AppointmentDTO> appointments;

    @NotNull
    private RecurrenceFrequency frequency;

    @NotNull
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

    public UUID getOrganizer() {
        return organizer;
    }

    public ExaminationTypeDTO getExaminationType() {
        return examinationType;
    }

    public void setExaminationType(ExaminationTypeDTO examinationType) {
        this.examinationType = examinationType;
    }

    public void setOrganizer(UUID organizer) {
        this.organizer = organizer;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Set<LocalDate> getSkippedIterations() {
        return skippedIterations;
    }

    public void setSkippedIterations(Set<LocalDate> skippedIterations) {
        this.skippedIterations = skippedIterations;
    }
}
