package com.biit.appointment.core.models;

import com.biit.server.controllers.models.ElementDTO;

public class AppointmentTypeDTO extends ElementDTO<Long> {

    private Long id;

    private String name;

    private String organizationId;

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

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
}
