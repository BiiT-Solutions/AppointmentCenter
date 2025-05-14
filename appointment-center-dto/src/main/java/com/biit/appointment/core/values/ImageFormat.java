package com.biit.appointment.core.values;

public enum ImageFormat {
    RAW,
    BASE64;

    public static ImageFormat getFormat(String name) {
        for (final ImageFormat format : ImageFormat.values()) {
            if (format.name().equalsIgnoreCase(name)) {
                return format;
            }
        }
        return null;
    }
}
