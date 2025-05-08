package com.biit.appointment.persistence.entities;


import com.biit.database.encryption.StringCryptoConverter;
import com.biit.server.persistence.entities.Element;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.io.Serial;
import java.util.UUID;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "external_calendar_credentials", uniqueConstraints = {@UniqueConstraint(columnNames = {"userId", "provider"})},
        indexes = {
                @Index(name = "ind_user", columnList = "userId"),
        })
public class ExternalCalendarCredentials extends Element<Long> {

    @Serial
    private static final long serialVersionUID = -5593134183953887764L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user", nullable = false)
    private UUID userId;

    @Column(name = "provider", nullable = false)
    @Enumerated(EnumType.STRING)
    private CalendarProvider calendarProvider;

    @Lob
    @Column(name = "credentials")
    @Convert(converter = StringCryptoConverter.class)
    private String credentials;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public CalendarProvider getProvider() {
        return calendarProvider;
    }

    public void setProvider(CalendarProvider calendarProvider) {
        this.calendarProvider = calendarProvider;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
