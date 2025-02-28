package com.biit.appointment.rest.api;

import com.biit.appointment.core.controllers.UserAvailabilityController;
import com.biit.appointment.core.models.UserAvailabilityDTO;
import com.biit.appointment.rest.api.models.AvailabilitySearch;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/availabilities")
public class UserAvailabilityServices {

    private final UserAvailabilityController userAvailabilityController;

    public UserAvailabilityServices(UserAvailabilityController userAvailabilityController) {
        this.userAvailabilityController = userAvailabilityController;
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets the availability from a user.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/from/{start}/to/{end}/slot-in-minutes/{duration}/slots/{slots}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserAvailabilityDTO> getAvailability(
            @Parameter(description = "UUID of the user.", required = true)
            @PathVariable(name = "uuid") UUID user,
            @Parameter(description = "Lower boundary for search.")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @PathVariable(name = "start") LocalDateTime start,
            @Parameter(description = "Upper boundary for search.")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @PathVariable(name = "end") LocalDateTime end,
            @Parameter(description = "Duration of the requested slot in minutes.", required = true)
            @PathVariable(name = "duration") int slotDuration,
            @Parameter(description = "Number of slots that will return.", required = true)
            @PathVariable(name = "slots") int slots,
            Authentication authentication) {
        return userAvailabilityController.getAvailability(authentication.getName(), start, end, slotDuration, slots);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets the availability from a user.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/users/{uuid}/from/{start}/to/{end}/slot-in-minutes/{duration}/slots/{slots}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserAvailabilityDTO> getAvailability(
            @Parameter(description = "UUID of the user.", required = true)
            @PathVariable(name = "uuid") UUID user,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @PathVariable(name = "start") LocalDateTime start,
            @Parameter(description = "Upper boundary for search.")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @PathVariable(name = "end") LocalDateTime end,
            @Parameter(description = "Duration of the requested slot in minutes.", required = true)
            @PathVariable(name = "duration") int slotDuration,
            @Parameter(description = "Number of slots that will return.", required = true)
            @PathVariable(name = "slots") int slots) {
        return userAvailabilityController.getAvailability(user, start, end, slotDuration, slots);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets the availability from a user.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/own", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<UserAvailabilityDTO> getOwnAvailability(@RequestBody AvailabilitySearch search, Authentication authentication) {
        return userAvailabilityController.getAvailability(authentication.getName(), search.getStart(), search.getEnd(),
                search.getSlotDuration(), search.getSlots());
    }


    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Gets the availability from a user.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<UserAvailabilityDTO> getAvailability(@RequestBody AvailabilitySearch search) {
        return userAvailabilityController.getAvailability(search.getUser(), search.getStart(), search.getEnd(), search.getSlotDuration(), search.getSlots());
    }

}
