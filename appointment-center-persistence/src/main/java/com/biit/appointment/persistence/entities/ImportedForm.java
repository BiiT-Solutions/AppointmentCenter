package com.biit.appointment.persistence.entities;

import com.biit.server.persistence.entities.Element;
import jakarta.persistence.Column;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.MappedSuperclass;

import java.util.Objects;

/**
 * Defines an imported form from a JSON file.
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ImportedForm extends Element<Long> {

    @Column(name = "file_name", nullable = false, length = MAX_UNIQUE_COLUMN_LENGTH)
    private String fileName;

    @Column(name = "display_name", nullable = false, length = MAX_UNIQUE_COLUMN_LENGTH)
    private String displayName;

    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    protected ImportedForm() {
        super();
    }

    protected ImportedForm(String name, String displayName, Long version, Long organization) {
        super();
        setFileName(name);
        setDisplayName(displayName);
        setVersion(version);
        setOrganizationId(organization);
    }

    public String getFileName() {
        return fileName;
    }

    public Long getVersion() {
        return version;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setFileName(String name) {
        this.fileName = name;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public boolean isSameForm(ExaminationForm form) {
        if (form == null) {
            return false;
        }
        return Objects.equals(getFileName(), form.getFileName()) && Objects.equals(getVersion(), form.getVersion())
                && Objects.equals(getOrganizationId(), form.getOrganizationId());
    }

    @Override
    public String toString() {
        return getFileName() + "_v" + getVersion();
    }

    public String getDisplayName() {
        return (displayName != null && displayName.length() > 0 ? displayName : fileName);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
