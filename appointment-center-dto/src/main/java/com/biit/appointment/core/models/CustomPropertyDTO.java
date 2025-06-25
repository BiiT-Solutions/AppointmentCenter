package com.biit.appointment.core.models;

import jakarta.validation.constraints.NotNull;

public class CustomPropertyDTO {

    private Long id;

    @NotNull
    private String key;

    @NotNull
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
