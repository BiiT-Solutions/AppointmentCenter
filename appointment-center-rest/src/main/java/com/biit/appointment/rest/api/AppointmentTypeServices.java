package com.biit.appointment.rest.api;

import com.biit.appointment.core.controllers.AppointmentTypeController;
import com.biit.appointment.core.converters.AppointmentTypeConverter;
import com.biit.appointment.core.converters.models.AppointmentTypeConverterRequest;
import com.biit.appointment.core.models.AppointmentTypeDTO;
import com.biit.appointment.core.providers.AppointmentTypeProvider;
import com.biit.appointment.persistence.entities.AppointmentType;
import com.biit.appointment.persistence.repositories.AppointmentTypeRepository;
import com.biit.server.rest.ElementServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/appointments-types")
public class AppointmentTypeServices extends ElementServices<AppointmentType, Long, AppointmentTypeDTO, AppointmentTypeRepository,
        AppointmentTypeProvider, AppointmentTypeConverterRequest, AppointmentTypeConverter, AppointmentTypeController> {

    protected AppointmentTypeServices(AppointmentTypeController controller) {
        super(controller);
    }

    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a list of appointments that are defined on the past.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/names/{name}/organizations/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AppointmentTypeDTO getPrevious(@Parameter(description = "Name of the appointment type")
                                          @PathVariable(name = "name") String name,
                                          @Parameter(description = "Id of an existing organization")
                                          @PathVariable(name = "organizationId") String organizationId,
                                          HttpServletRequest request) {
        return getController().findByNameAndOrganizationId(name, organizationId);
    }
}
