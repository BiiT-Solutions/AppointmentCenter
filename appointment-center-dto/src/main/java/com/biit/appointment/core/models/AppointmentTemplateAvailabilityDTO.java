package com.biit.appointment.core.models;

import com.biit.utils.date.range.LocalDateTimeRange;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class AppointmentTemplateAvailabilityDTO {

    @NotNull
    private AppointmentTemplateDTO appointmentTemplate;

    private List<LocalDateTimeRange> availability;

    public AppointmentTemplateAvailabilityDTO() {
        super();
    }


    public AppointmentTemplateAvailabilityDTO(AppointmentTemplateDTO appointmentTemplate) {
        this();
        this.appointmentTemplate = appointmentTemplate;
    }

    public void setAppointmentTemplate(AppointmentTemplateDTO appointmentTemplate) {
        this.appointmentTemplate = appointmentTemplate;
    }

    public AppointmentTemplateDTO getAppointmentTemplate() {
        return appointmentTemplate;
    }

    public List<LocalDateTimeRange> getAvailability() {
        return availability;
    }

    public void setAvailability(List<LocalDateTimeRange> availability) {
        this.availability = availability;
    }
}
