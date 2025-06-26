package com.biit.appointment.core.models;

import com.biit.server.controllers.models.ElementDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class QrCodeDTO extends ImageDTO {

    @NotNull
    @Size(min = ElementDTO.MIN_FIELD_LENGTH)
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
