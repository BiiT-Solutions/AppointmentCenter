package com.biit.appointment.rest.api;

import com.biit.appointment.core.controllers.AppointmentController;
import com.biit.appointment.core.converters.AppointmentConverter;
import com.biit.appointment.core.converters.models.AppointmentConverterRequest;
import com.biit.appointment.core.models.AppointmentDTO;
import com.biit.appointment.core.providers.AppointmentProvider;
import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.repositories.AppointmentRepository;
import com.biit.server.rest.BasicServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentServices extends BasicServices<Appointment, AppointmentDTO, AppointmentRepository,
        AppointmentProvider, AppointmentConverterRequest, AppointmentConverter, AppointmentController> {

    public AppointmentServices(AppointmentController controller) {
        super(controller);
    }

    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets all appointments from an organizer.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/organizer/{organizerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AppointmentDTO> findByOrganizerId(@Parameter(description = "Id of an existing organizer", required = true)
                                                  @PathVariable("organizerId") Long organizerId, HttpServletRequest request) {
        return controller.findByOrganizerId(organizerId);
    }
}
