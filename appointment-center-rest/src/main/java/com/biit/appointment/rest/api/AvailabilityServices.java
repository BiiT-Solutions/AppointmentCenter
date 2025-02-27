package com.biit.appointment.rest.api;

import com.biit.appointment.core.controllers.AvailabilityController;
import com.biit.appointment.core.converters.AvailabilityConverter;
import com.biit.appointment.core.converters.models.AvailabilityConverterRequest;
import com.biit.appointment.core.models.AvailabilityDTO;
import com.biit.appointment.core.models.AvailabilityRangeDTO;
import com.biit.appointment.core.providers.AvailabilityProvider;
import com.biit.appointment.persistence.entities.Availability;
import com.biit.appointment.persistence.repositories.AvailabilityRepository;
import com.biit.server.rest.ElementServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/availabilities")
public class AvailabilityServices extends ElementServices<Availability, Long, AvailabilityDTO, AvailabilityRepository,
        AvailabilityProvider, AvailabilityConverterRequest, AvailabilityConverter, AvailabilityController> {

    public AvailabilityServices(AvailabilityController controller) {
        super(controller);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Add a range to the availability of a user.", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping(value = "/users/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AvailabilityDTO addAvailabilityRange(@Parameter(description = "UUID of the user.")
                                                @PathVariable(name = "uuid") UUID uuid,
                                                @RequestBody Collection<AvailabilityRangeDTO> availabilityRangeDTOs,
                                                Authentication authentication,
                                                HttpServletRequest request) {
        return getController().add(availabilityRangeDTOs, uuid, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Sets the set of ranges for the availability of a user.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/users/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AvailabilityDTO setAvailabilityRange(@Parameter(description = "UUID of the user.")
                                                @PathVariable(name = "uuid") UUID uuid,
                                                @RequestBody Collection<AvailabilityRangeDTO> availabilityRangeDTOs,
                                                Authentication authentication,
                                                HttpServletRequest request) {
        return getController().set(availabilityRangeDTOs, uuid, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Removes an availability range from a user. Any existing range will be adjusted or remove to not overlap."
            + "the provided range to delete.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(value = "/users/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AvailabilityDTO removeAvailabilityRange(@Parameter(description = "UUID of the user.")
                                                   @PathVariable(name = "uuid") UUID uuid,
                                                   @RequestBody Collection<AvailabilityRangeDTO> availabilityRangeDTOs,
                                                   Authentication authentication,
                                                   HttpServletRequest request) {
        return getController().remove(availabilityRangeDTOs, uuid, authentication.getName());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Removes all the availability from a user. "
            + "the provided range to delete.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping(value = "/users/{uuid}/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public AvailabilityDTO removeAllAvailabilityRange(@Parameter(description = "UUID of the user.")
                                                   @PathVariable(name = "uuid") UUID uuid,
                                                   Authentication authentication,
                                                   HttpServletRequest request) {
        return getController().removeAll(uuid, authentication.getName());
    }
}
