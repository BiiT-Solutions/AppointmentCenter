package com.biit.appointment.core.models;

import com.biit.server.controllers.models.ElementDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProfessionalSpecializationDTO extends ElementDTO<Long> {

    private Long id;

    @NotNull
    @Size(min = ElementDTO.MIN_FIELD_LENGTH, max = ElementDTO.MAX_SMALL_FIELD_LENGTH)
    private String name;

    @Valid
    @NotNull
    private AppointmentTypeDTO appointmentType;

    private String organizationId;

    private Long userId;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AppointmentTypeDTO getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(AppointmentTypeDTO appointmentType) {
        this.appointmentType = appointmentType;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
