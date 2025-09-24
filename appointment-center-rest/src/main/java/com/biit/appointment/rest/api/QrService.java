package com.biit.appointment.rest.api;

import com.biit.appointment.core.controllers.QrController;
import com.biit.appointment.core.models.QrCodeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/qr")
public class QrService {

    private final QrController qrController;

    public QrService(QrController qrController) {
        this.qrController = qrController;
    }

    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Generates a QR code with the credentials to access to a workshop.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/appointments/{appointmentId}/attendance", produces = MediaType.APPLICATION_JSON_VALUE)
    public QrCodeDTO generateQrForAttendance(@Parameter(description = "Id of an existing appointment", required = true)
                                             @PathVariable("appointmentId") Long appointmentId, Authentication authentication,
                                             HttpServletResponse response, HttpServletRequest request) {
        return qrController.generateUserAppointmentAttendanceCode(authentication.getName(), appointmentId);
    }


    @PreAuthorize("hasAnyAuthority(@securityService.viewerPrivilege, @securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Generates a QR code with the credentials to access to a workshop.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/appointments/{appointmentId}/attendance/image", produces = MediaType.IMAGE_PNG_VALUE)
    public byte[] generateQrForAttendanceImage(@Parameter(description = "Id of an existing appointment", required = true)
                                               @PathVariable("appointmentId") Long appointmentId, Authentication authentication,
                                               HttpServletResponse response, HttpServletRequest request) {

        final ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename("Appointment Attendance - QR.png").build();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

        return qrController.generateUserAppointmentAttendanceCode(authentication.getName(), appointmentId).getData();
    }

    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Generates a QR code with the content.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "", produces = MediaType.IMAGE_PNG_VALUE, consumes = MediaType.TEXT_PLAIN_VALUE)
    public byte[] generateQrForAttendanceImage(@NotBlank @RequestBody String content,
                                               HttpServletResponse response, HttpServletRequest request) {

        final ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename("QR.png").build();
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

        return qrController.generateQrCode(content).getData();
    }

    @PreAuthorize("hasAnyAuthority(@securityService.editorPrivilege, @securityService.adminPrivilege)")
    @Operation(summary = "Generates a QR as SVG image with the content.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/svg", consumes = MediaType.TEXT_PLAIN_VALUE, produces = {MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public String generateQrForAttendanceSvg(@NotBlank @RequestBody String content,
                                               HttpServletResponse response, HttpServletRequest request) {

        final ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename("QR.svg").build();
        response.setHeader(HttpHeaders.CONTENT_TYPE, "image/svg+xml");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

        return new String(qrController.generateQrCodeAsSvg(content).getData(), StandardCharsets.UTF_8);
    }
}
