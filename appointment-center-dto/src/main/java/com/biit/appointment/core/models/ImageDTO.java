package com.biit.appointment.core.models;


import com.biit.appointment.core.exceptions.InvalidFormatException;
import com.biit.appointment.core.values.ImageFormat;
import com.biit.server.controllers.models.ElementDTO;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ImageDTO extends ElementDTO<Long> {

    private Long id;

    private static final String IMAGE_PNG_BASE_64 = "data:image/png;base64,";
    private static final String IMAGE_JPG_BASE_64 = "data:image/jpeg;base64,";

    private byte[] data;
    private ImageFormat imageFormat;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @JsonGetter
    public String getBase64() {
        if (data == null) {
            return null;
        }
        return IMAGE_PNG_BASE_64 + Base64.getEncoder().encodeToString(data);
    }

    @JsonSetter
    public void setBase64(String base64) {
        if (base64 != null) {
            if (base64.startsWith(IMAGE_PNG_BASE_64)) {
                this.data = Base64.getDecoder().decode(base64.replaceFirst(IMAGE_PNG_BASE_64, "").getBytes(StandardCharsets.UTF_8));
            } else if (base64.startsWith(IMAGE_JPG_BASE_64)) {
                this.data = Base64.getDecoder().decode(base64.replaceFirst(IMAGE_JPG_BASE_64, "").getBytes(StandardCharsets.UTF_8));
            } else {
                throw new InvalidFormatException(this.getClass(), "Invalid image format.");
            }
        }
    }

    public ImageFormat getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(ImageFormat imageFormat) {
        this.imageFormat = imageFormat;
    }
}
