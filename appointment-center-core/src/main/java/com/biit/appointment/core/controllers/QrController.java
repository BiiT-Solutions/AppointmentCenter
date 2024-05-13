package com.biit.appointment.core.controllers;

import com.biit.appointment.core.models.QrCodeDTO;
import com.biit.appointment.core.providers.QrProvider;
import com.biit.server.exceptions.UnexpectedValueException;
import com.biit.server.exceptions.UserNotFoundException;
import com.biit.server.security.IAuthenticatedUser;
import com.biit.server.security.IAuthenticatedUserProvider;
import org.springframework.stereotype.Controller;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Optional;

@Controller
public class QrController {

    private static final String LOGO_RESOURCE = "/biiticon.svg";
    private static final String QR_FORMAT = "png";
    private static final Integer QR_SIZE = 500;
    private static final Color QR_COLOR = Color.decode("#F20D5E");

    private final QrProvider qrProvider;

    private final IAuthenticatedUserProvider userManagerClient;

    public QrController(QrProvider qrProvider, IAuthenticatedUserProvider userManagerClient) {
        this.qrProvider = qrProvider;
        this.userManagerClient = userManagerClient;
    }

    public QrCodeDTO generateUserAppointmentAttendanceCode(String username, Long appointmentId) {
        final Optional<IAuthenticatedUser> user = this.userManagerClient.findByUsername(username);
        if (user.isPresent()) {
            return generateUserAppointmentAttendanceCode(user.get(), appointmentId);
        }
        throw new UserNotFoundException(this.getClass(), "No user found with username '" + username + "'.");
    }

    public QrCodeDTO generateUserAppointmentAttendanceCode(IAuthenticatedUser user, Long appointmentId) {
        try {
            final String link = "not-implemented";
            final BufferedImage qrCode = qrProvider.getQr(link, QR_SIZE, QR_COLOR, LOGO_RESOURCE);
            final QrCodeDTO qrCodeDTO = new QrCodeDTO();
            qrCodeDTO.setData(toByteArray(qrCode, QR_FORMAT));
            qrCodeDTO.setLink(link);
            return qrCodeDTO;
        } catch (IOException e) {
            throw new UnexpectedValueException(this.getClass(), e);
        }
    }

    // convert BufferedImage to byte[]
    public static byte[] toByteArray(BufferedImage bufferedImage, String format) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, format, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
