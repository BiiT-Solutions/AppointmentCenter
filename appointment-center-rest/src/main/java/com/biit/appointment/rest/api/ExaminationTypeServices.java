package com.biit.appointment.rest.api;

import com.biit.appointment.core.controllers.ExaminationTypeController;
import com.biit.appointment.core.converters.ExaminationTypeConverter;
import com.biit.appointment.core.converters.models.ExaminationTypeConverterRequest;
import com.biit.appointment.core.models.ExaminationTypeDTO;
import com.biit.appointment.core.providers.ExaminationTypeProvider;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.repositories.ExaminationTypeRepository;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/examinations-types")
public class ExaminationTypeServices extends ElementServices<ExaminationType, Long, ExaminationTypeDTO, ExaminationTypeRepository,
        ExaminationTypeProvider, ExaminationTypeConverterRequest, ExaminationTypeConverter, ExaminationTypeController> {

    protected ExaminationTypeServices(ExaminationTypeController controller) {
        super(controller);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a list of appointments that are defined on the past.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/names/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ExaminationTypeDTO> getByName(@Parameter(description = "Name of the examination type")
                                              @PathVariable(name = "name") String name,
                                              @Parameter(description = "If it is deleted or not")
                                              @RequestParam(name = "deleted") Optional<Boolean> deleted,
                                              HttpServletRequest request) {
        return getController().findByNameAndDeleted(name, deleted.orElse(false));
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a list of appointments that are defined on the past.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/names/{name}/organizations/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ExaminationTypeDTO getByNameAndOrganization(@Parameter(description = "Name of the examination type")
                                                       @PathVariable(name = "name") String name,
                                                       @Parameter(description = "Id of an existing organization")
                                                       @PathVariable(name = "organizationId") Long organizationId,
                                                       @Parameter(description = "If it is deleted or not")
                                                       @RequestParam(name = "deleted") Optional<Boolean> deleted,
                                                       HttpServletRequest request) {
        return getController().findByNameAndOrganizationId(name, organizationId, deleted.orElse(false));
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a list of appointments that are defined on the past.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/organizations/{organizationId}/appointment-type/{appointmentTypeName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ExaminationTypeDTO> getByOrganizationAndAppointmentType(
            @Parameter(description = "Id of an existing organization")
            @PathVariable(name = "organizationId") Long organizationId,
            @Parameter(description = "Name of the appointment type")
            @PathVariable(name = "appointmentTypeName") String appointmentTypeName,
            @Parameter(description = "If it is deleted or not")
            @RequestParam(name = "deleted") Optional<Boolean> deleted,
            HttpServletRequest request) {
        return getController().findAllByOrOrganizationIdAndAppointmentTypeAndDeleted(organizationId, appointmentTypeName, deleted.orElse(false));
    }

    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a list of appointments that are defined on the past.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/past/organizations/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ExaminationTypeDTO> getByOrganizationAndAppointmentTypes(
            @Parameter(description = "Id of an existing organization")
            @PathVariable(name = "organizationId") Long organizationId,
            @Parameter(description = "Name of the appointment types")
            @RequestParam(name = "appointmentTypeNames") Optional<Collection<String>> appointmentTypeNames,
            @Parameter(description = "If it is deleted or not")
            @RequestParam(name = "deleted") Optional<Boolean> deleted,
            HttpServletRequest request) {
        return getController().findAllByOrOrganizationIdAndAppointmentTypeInAndDeletedUsingNames(organizationId, appointmentTypeNames.orElse(null),
                deleted.orElse(false));
    }
}
