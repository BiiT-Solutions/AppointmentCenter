package com.biit.appointment.core.models;

/*-
 * #%L
 * AppointmentCenter (DTO)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import com.biit.appointment.core.exceptions.InvalidFormatException;
import com.biit.appointment.core.values.ImageFormat;
import com.biit.server.controllers.models.ElementDTO;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ImageDTO extends ElementDTO<Long> {

    private Long id;

    private static final String IMAGE_PNG_BASE_64 = "data:image/png;base64,";
    private static final String IMAGE_JPG_BASE_64 = "data:image/jpeg;base64,";

    private byte[] data;

    @NotNull
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
