package com.biit.appointment.core.converters;

import com.biit.appointment.core.converters.models.AttendanceConverterRequest;
import com.biit.appointment.core.models.AttendanceDTO;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.persistence.entities.Attendance;
import com.biit.server.controller.converters.ElementConverter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class AttendanceConverter extends ElementConverter<Attendance, AttendanceDTO, AttendanceConverterRequest> {

    private final AppointmentProvider appointmentProvider;


    public AttendanceConverter(AppointmentProvider appointmentProvider) {
        this.appointmentProvider = appointmentProvider;
    }

    @Override
    protected AttendanceDTO convertElement(AttendanceConverterRequest from) {
        final AttendanceDTO attendanceDTO = new AttendanceDTO();
        if (from.getEntity() != null) {
            BeanUtils.copyProperties(from.getEntity(), attendanceDTO);
            if (from.getEntity().getAppointment() != null) {
                attendanceDTO.setAppointmentId(from.getEntity().getAppointment().getId());
            }
        }
        return attendanceDTO;
    }

    @Override
    public Attendance reverse(AttendanceDTO to) {
        if (to == null) {
            return null;
        }
        final Attendance attendance = new Attendance();
        BeanUtils.copyProperties(to, attendance);
        if (to.getAppointmentId() != null) {
            attendance.setAppointment(appointmentProvider.findById(to.getAppointmentId()).orElse(null));
        }
        return attendance;
    }
}
