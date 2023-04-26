package com.biit.appointment.rest.api;


import com.biit.server.rest.SecurityService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service("securityService")
public class AppointmentCenterSecurityService extends SecurityService {

    private static final String VIEWER = "AppointmentCenter_VIEWER";
    private static final String ADMIN = "AppointmentCenter_ADMIN";
    private static final String EDITOR = "AppointmentCenter_EDITOR";

    private String viewerPrivilege = null;
    private String adminPrivilege = null;
    private String editorPrivilege = null;

    public String getViewerPrivilege() {
        if (viewerPrivilege == null) {
            viewerPrivilege = VIEWER.toUpperCase();
        }
        return viewerPrivilege;
    }

    public String getAdminPrivilege() {
        if (adminPrivilege == null) {
            adminPrivilege = ADMIN.toUpperCase();
        }
        return adminPrivilege;
    }

    public String getEditorPrivilege() {
        if (editorPrivilege == null) {
            editorPrivilege = EDITOR.toUpperCase();
        }
        return editorPrivilege;
    }
}
