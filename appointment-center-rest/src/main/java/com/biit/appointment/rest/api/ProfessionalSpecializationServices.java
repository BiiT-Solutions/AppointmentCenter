package com.biit.appointment.rest.api;

import com.biit.appointment.core.controllers.ProfessionalSpecializationController;
import com.biit.appointment.core.converters.ProfessionalSpecializationConverter;
import com.biit.appointment.core.converters.models.ProfessionalSpecializationConverterRequest;
import com.biit.appointment.core.models.ProfessionalSpecializationDTO;
import com.biit.appointment.core.providers.ProfessionalSpecializationProvider;
import com.biit.appointment.persistence.entities.ProfessionalSpecialization;
import com.biit.appointment.persistence.repositories.ProfessionalSpecializationRepository;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/professional-specializations")
public class ProfessionalSpecializationServices extends ElementServices<ProfessionalSpecialization, Long, ProfessionalSpecializationDTO,
        ProfessionalSpecializationRepository, ProfessionalSpecializationProvider, ProfessionalSpecializationConverterRequest,
        ProfessionalSpecializationConverter, ProfessionalSpecializationController> {

    protected ProfessionalSpecializationServices(ProfessionalSpecializationController controller) {
        super(controller);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a list of professional specializations by its names.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/names/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProfessionalSpecializationDTO> getByName(@Parameter(description = "Name of the examination type")
                                                         @PathVariable(name = "name") String name,
                                                         HttpServletRequest request) {
        return getController().findByName(name);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "GGets a professional specializations by its names and organization.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/names/{name}/organizations/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ProfessionalSpecializationDTO getByNameAndOrganization(@Parameter(description = "Name of the examination type")
                                                                  @PathVariable(name = "name") String name,
                                                                  @Parameter(description = "Id of an existing organization")
                                                                  @PathVariable(name = "organizationId") Long organizationId,
                                                                  HttpServletRequest request) {
        return getController().findByNameAndOrganizationId(name, organizationId);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a list of professional specializations by its organization and the appointment type name.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/organizations/{organizationId}/appointment-types/{appointmentTypeName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProfessionalSpecializationDTO> getByOrganizationAndAppointmentType(
            @Parameter(description = "Id of an existing organization")
            @PathVariable(name = "organizationId") Long organizationId,
            @Parameter(description = "Name of the appointment type")
            @PathVariable(name = "appointmentTypeName") String appointmentTypeName,
            HttpServletRequest request) {
        return getController().findAllByOrOrganizationIdAndAppointmentType(organizationId, appointmentTypeName);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a list of professional specializations by its organization and a set of appointments types.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/organizations/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProfessionalSpecializationDTO> getByOrganization(
            @Parameter(description = "Id of an existing organization")
            @PathVariable(name = "organizationId") Long organizationId,
            @Parameter(description = "Name of the appointment types")
            @RequestParam(name = "appointmentTypeNames") Optional<Collection<String>> appointmentTypeNames,
            HttpServletRequest request) {
        return getController().findAllByOrOrganizationIdAndAppointmentTypeInUsingNames(organizationId, appointmentTypeNames.orElse(null));
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a list of professional specializations by a user.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/users/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProfessionalSpecializationDTO> getByUser(
            @Parameter(description = "Collection of users ids")
            @RequestParam(name = "usersIds") Optional<Collection<Long>> usersIds,
            HttpServletRequest request) {
        if (usersIds.isPresent()) {
            return getController().findByUserId(usersIds.get());
        } else {
            return new ArrayList<>();
        }
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a list of professional specializations by a user in an organization.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/organizations/{organizationId}/users/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProfessionalSpecializationDTO> getByUser(
            @Parameter(description = "Id of an existing organization")
            @PathVariable(name = "organizationId") Long organizationId,
            @Parameter(description = "Collection of users ids")
            @RequestParam(name = "usersIds") Optional<Collection<Long>> usersIds,
            HttpServletRequest request) {
        if (usersIds.isPresent()) {
            return getController().findByUserIdAndOrganizationId(usersIds.get(), organizationId);
        } else {
            return new ArrayList<>();
        }
    }
}
