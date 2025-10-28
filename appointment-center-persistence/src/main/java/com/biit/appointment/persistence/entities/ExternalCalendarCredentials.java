package com.biit.appointment.persistence.entities;

/*-
 * #%L
 * AppointmentCenter (Persistence)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


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
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "external_calendar_credentials", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "provider"})},
        indexes = {
                @Index(name = "ind_user", columnList = "user_id"),
        })
public class ExternalCalendarCredentials extends Element<Long> {

    @Serial
    private static final long serialVersionUID = -5593134183953887764L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "provider", nullable = false)
    @Enumerated(EnumType.STRING)
    private CalendarProvider calendarProvider;

    @Lob
    @Column(name = "user_credentials", columnDefinition = "TEXT")
    @Convert(converter = StringCryptoConverter.class)
    private String userCredentials;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "force_refresh_at")
    private LocalDateTime forceRefreshAt;

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

    public String getUserCredentials() {
        return userCredentials;
    }

    public void setUserCredentials(String credentials) {
        this.userCredentials = credentials;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public CalendarProvider getCalendarProvider() {
        return calendarProvider;
    }

    public void setCalendarProvider(CalendarProvider calendarProvider) {
        this.calendarProvider = calendarProvider;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean hasExpired() {
        if (expiresAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public LocalDateTime getForceRefreshAt() {
        return forceRefreshAt;
    }

    public void setForceRefreshAt(LocalDateTime forceRefreshAt) {
        this.forceRefreshAt = forceRefreshAt;
    }
}
