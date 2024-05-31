package com.biit.appointment.core.converters.models;

import com.biit.appointment.persistence.entities.Attendance;
import com.biit.server.converters.models.ConverterRequest;

public class AttendanceConverterRequest extends ConverterRequest<Attendance> {

    public AttendanceConverterRequest(Attendance attendance) {
        super(attendance);
    }
}
