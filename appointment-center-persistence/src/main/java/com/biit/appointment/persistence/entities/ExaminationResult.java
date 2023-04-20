package com.biit.appointment.persistence.entities;

import com.biit.database.encryption.StringCryptoConverter;
import com.biit.server.persistence.entities.Element;
import com.biit.utils.pool.PoolElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Table(name = "examination_result", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"appointment", "examination_form"})})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ExaminationResult extends Element implements PoolElement<Long> {
    private static final long serialVersionUID = 2547976709523176154L;
    private static final int MAX_JSON_LENGTH = 100000;

    @Lob
    @Column(name = "json", length = MAX_JSON_LENGTH)
    @Convert(converter = StringCryptoConverter.class)
    private String jsonCode;

    @ManyToOne
    @JoinColumn(name = "appointment")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "examination_form")
    private ExaminationForm examinationForm;

    public Appointment getAppointment() {
        return appointment;
    }

    public ExaminationForm getExaminationForm() {
        return examinationForm;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public void setExaminationForm(ExaminationForm examinationForm) {
        this.examinationForm = examinationForm;
    }

    public String getJsonCode() {
        return jsonCode;
    }

    @Override
    public String toString() {
        if (getExaminationForm() != null) {
            return getExaminationForm().getDisplayName();
        }
        return super.toString();
    }

    @Override
    public Long getUniqueId() {
        return getId();
    }
}
