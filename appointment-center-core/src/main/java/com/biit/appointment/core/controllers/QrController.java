package com.biit.appointment.core.controllers;

import com.biit.appointment.core.converters.models.AttendanceRequest;
import com.biit.appointment.core.models.QrCodeDTO;
import com.biit.appointment.core.providers.QrProvider;
import com.biit.appointment.core.values.ImageFormat;
import com.biit.server.exceptions.UnexpectedValueException;
import com.biit.server.exceptions.UserNotFoundException;
import com.biit.server.security.IAuthenticatedUser;
import com.biit.server.security.IAuthenticatedUserProvider;
import org.springframework.stereotype.Controller;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Controller
public class QrController {

    private static final String LOGO_RESOURCE = "/BIITicon.svg";
    private static final String QR_FORMAT = "png";
    private static final Integer QR_SIZE = 500;
    private static final Color QR_COLOR = Color.decode("#000000");
    private static final Color QR_BACKGROUND = null;
    private static final Color QR_BORDER = null;

    private final QrProvider qrProvider;

    private final IAuthenticatedUserProvider authenticatedUserProvider;

    public QrController(QrProvider qrProvider, IAuthenticatedUserProvider authenticatedUserProvider) {
        this.qrProvider = qrProvider;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    public QrCodeDTO generateQrCode(String content) {
        try {
            final BufferedImage qrCode = qrProvider.getQr(content, QR_SIZE, QR_BORDER, QR_COLOR, QR_BACKGROUND, LOGO_RESOURCE);
            final QrCodeDTO qrCodeDTO = new QrCodeDTO();
            qrCodeDTO.setData(toByteArray(qrCode, QR_FORMAT));
            qrCodeDTO.setImageFormat(ImageFormat.BASE64);
            qrCodeDTO.setContent(content);
            return qrCodeDTO;
        } catch (IOException e) {
            throw new UnexpectedValueException(this.getClass(), e);
        }
    }

    public QrCodeDTO generateQrCodeAsSvg(String content) {
        try {
            final Document qrCode = qrProvider.getQrAsSvg(content, QR_SIZE, QR_BORDER, QR_COLOR, QR_BACKGROUND, LOGO_RESOURCE);
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            final StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(qrCode), new StreamResult(stringWriter));
            final QrCodeDTO qrCodeDTO = new QrCodeDTO();
            qrCodeDTO.setData(stringWriter.toString().getBytes(StandardCharsets.UTF_8));
            qrCodeDTO.setImageFormat(ImageFormat.SVG);
            qrCodeDTO.setContent(content);
            return qrCodeDTO;
        } catch (TransformerException e) {
            throw new UnexpectedValueException(this.getClass(), e);
        }
    }

    public QrCodeDTO generateUserAppointmentAttendanceCode(String username, Long appointmentId) {
        final Optional<IAuthenticatedUser> user = this.authenticatedUserProvider.findByUsername(username);
        if (user.isPresent()) {
            return generateUserAppointmentAttendanceCode(user.get(), appointmentId);
        }
        throw new UserNotFoundException(this.getClass(), "No user found with username '" + username + "'.");
    }

    public QrCodeDTO generateUserAppointmentAttendanceCode(IAuthenticatedUser user, Long appointmentId) {
        return generateQrCode(generateAttendanceRequest(user, appointmentId));
    }

    /**
     * Attendance request is stored as string in Base64.
     *
     * @param user          The attender.
     * @param appointmentId Which appointment is attending.
     * @return the codified attendance request.
     */
    private String generateAttendanceRequest(IAuthenticatedUser user, Long appointmentId) {
        return new AttendanceRequest(appointmentId, UUID.fromString(user.getUID())).code();
    }

    // convert BufferedImage to byte[]
    public static byte[] toByteArray(BufferedImage bufferedImage, String format) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, format, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
