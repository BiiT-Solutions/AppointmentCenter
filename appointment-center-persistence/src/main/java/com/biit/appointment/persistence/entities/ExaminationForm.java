package com.biit.appointment.persistence.entities;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


@Entity
@Table(name = "examination_form", uniqueConstraints = {@UniqueConstraint(columnNames = {"file_name", "version", "organization_id"})})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ExaminationForm extends ImportedForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Duration in minutes
    private Integer duration;

    @Column(name = "fill_plugin", columnDefinition = "LONGTEXT")
    private String fillPlugin;

    @Column(name = "fill_configuration", columnDefinition = "LONGTEXT")
    private String fillConfiguration;

    // Order in the tests tabs
    @Column(name = "test_order")
    private int testOrder = 0;

    @Column(name = "mandatory", nullable = false)
    private boolean mandatory = false;

    @Column(name = "cost", nullable = false)
    private Double cost = 0d;

    public ExaminationForm() {
        super();
    }

    public ExaminationForm(String name, String displayName, Integer duration, Long version, Long organization, Double cost) {
        super();
        setFileName(name);
        setDisplayName(displayName);
        setVersion(version);
        setDuration(duration);
        setOrganizationId(organization);
        setCost(cost);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return super.toString() + " (" + duration + "m)";
    }

    public Integer getDuration() {
        if (duration == null) {
            return 0;
        }
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getFillPlugin() {
        return fillPlugin;
    }

    public void setFillPlugin(String fillPlugin) {
        this.fillPlugin = fillPlugin;
    }

    public String getFillConfiguration() {
        return fillConfiguration;
    }

    public void setFillConfiguration(String fillConfiguration) {
        this.fillConfiguration = fillConfiguration;
    }

    public int getTestOrder() {
        return testOrder;
    }

    public void setTestOrder(int order) {
        this.testOrder = order;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

}
