package com.biit.appointment.persistence.repositories;

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

import com.biit.appointment.persistence.entities.ExaminationType;
import com.biit.appointment.persistence.entities.Recurrence;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public class RecurrenceRepositoryImpl implements CustomRecurrenceRepository {

    @PersistenceContext
    @Qualifier(value = "appointmentCenterFactory")
    private EntityManager entityManager;

    @Override
    public List<Recurrence> findBy(String organizationId, UUID organizer, String createdBy, Collection<ExaminationType> examinationTypes,
                                   LocalDateTime lowerTimeBoundary, LocalDateTime upperTimeBoundary) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Recurrence> query = criteriaBuilder.createQuery(Recurrence.class);
        final Root<Recurrence> root = query.from(Recurrence.class);

        final List<Predicate> predicates = new ArrayList<>();
        if (organizationId != null) {
            predicates.add(criteriaBuilder.equal(root.get("organizationId"), organizationId));
        }
        if (organizer != null) {
            predicates.add(criteriaBuilder.equal(root.get("organizer"), organizer));
        }
        if (examinationTypes != null && !examinationTypes.isEmpty()) {
            predicates.add(root.get("examinationType").in(examinationTypes));
        }
        if (lowerTimeBoundary != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("endsAt"), lowerTimeBoundary));
        }
        if (upperTimeBoundary != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("startsAt"), upperTimeBoundary));
        }
        if (createdBy != null) {
            predicates.add(criteriaBuilder.equal(root.get("createdBy"), createdBy));
        }

        root.fetch("appointments", JoinType.LEFT);
        query.select(root).where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(query).getResultList();
    }
}
