package com.biit.appointment.core.models;

import com.biit.server.controllers.models.ElementDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ExaminationTypeDTO extends ElementDTO<String> {

    @NotNull
    @Size(min = ElementDTO.MIN_FIELD_LENGTH, max = ElementDTO.MAX_SMALL_FIELD_LENGTH)
    private String name;

    @Valid
    @NotNull
    private AppointmentTypeDTO appointmentType;

    private String organizationId;

    private Double price;

    private boolean deleted;

    private boolean appointmentOverlapsAllowed = false;

    @JsonIgnore
    @Override
    public String getId() {
        return getName();
    }

    @JsonIgnore
    @Override
    public void setId(String id) {
        setName(name);
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isAppointmentOverlapsAllowed() {
        return appointmentOverlapsAllowed;
    }

    public void setAppointmentOverlapsAllowed(boolean appointmentOverlapsAllowed) {
        this.appointmentOverlapsAllowed = appointmentOverlapsAllowed;
    }

}
