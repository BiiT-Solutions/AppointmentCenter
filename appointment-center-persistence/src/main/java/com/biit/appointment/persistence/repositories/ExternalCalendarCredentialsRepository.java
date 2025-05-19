package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.CalendarProvider;
import com.biit.appointment.persistence.entities.ExternalCalendarCredentials;
import com.biit.server.persistence.repositories.ElementRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Transactional
@Repository
public interface ExternalCalendarCredentialsRepository extends ElementRepository<ExternalCalendarCredentials, Long> {

    ExternalCalendarCredentials findByUserIdAndCalendarProvider(UUID userId, CalendarProvider calendarProvider);

    void deleteByUserIdAndCalendarProvider(UUID userId, CalendarProvider calendarProvider);

    List<ExternalCalendarCredentials> findByExpiresAtAfter(LocalDateTime expiresAt);

    List<ExternalCalendarCredentials> findByCreatedAtBefore(LocalDateTime createdAt);

    List<ExternalCalendarCredentials> findByExpiresAtBefore(LocalDateTime expiresAt);
}
