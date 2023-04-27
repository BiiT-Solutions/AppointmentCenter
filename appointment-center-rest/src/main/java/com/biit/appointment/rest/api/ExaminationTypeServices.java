package com.biit.appointment.rest.api;

import com.biit.appointment.core.controllers.ExaminationTypeController;
import com.biit.appointment.core.converters.ExaminationTypeConverter;
import com.biit.appointment.core.converters.models.ExaminationTypeConverterRequest;
import com.biit.appointment.core.models.ExaminationTypeDTO;
import com.biit.appointment.core.providers.ExaminationTypeProvider;
import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.repositories.ExaminationTypeRepository;
import com.biit.server.rest.BasicServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/examinations-types")
public class ExaminationTypeServices extends BasicServices<ExaminationType, ExaminationTypeDTO, ExaminationTypeRepository,
        ExaminationTypeProvider, ExaminationTypeConverterRequest, ExaminationTypeConverter, ExaminationTypeController> {

    protected ExaminationTypeServices(ExaminationTypeController controller) {
        super(controller);
    }

    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a list of appointments that are defined on the past.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/names/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ExaminationTypeDTO> getPrevious(@Parameter(description = "Name of the examination type")
                                                @PathVariable(name = "name") String name,
                                                @Parameter(description = "If it is deleted or not")
                                                @RequestParam(name = "deleted") Optional<Boolean> deleted,
                                                HttpServletRequest request) {
        return controller.findByNameAndDeleted(name, deleted.orElse(false));
    }

    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets a list of appointments that are defined on the past.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/names/{name}/organizations/{organizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ExaminationTypeDTO getPrevious(@Parameter(description = "Name of the examination type")
                                          @PathVariable(name = "name") String name,
                                          @Parameter(description = "Id of an existing organization")
                                          @PathVariable(name = "organizationId") Long organizationId,
                                          @Parameter(description = "If it is deleted or not")
                                          @RequestParam(name = "deleted") Optional<Boolean> deleted,
                                          HttpServletRequest request) {
        return controller.findByNameAndOrganizationId(name, organizationId, deleted.orElse(false));
    }
}
