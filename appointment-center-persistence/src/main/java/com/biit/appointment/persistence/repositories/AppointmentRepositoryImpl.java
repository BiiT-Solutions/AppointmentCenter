package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.Appointment;
import com.biit.appointment.persistence.entities.AppointmentStatus;
import com.biit.appointment.persistence.entities.ExaminationType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class AppointmentRepositoryImpl implements CustomAppointmentRepository {


    @PersistenceContext
    @Qualifier(value = "appointmentCenterFactory")
    private EntityManager entityManager;


    @Override
    public List<Appointment> findBy(String organizationId, UUID organizer, UUID attendee, String createdBy, Collection<ExaminationType> examinationTypes,
                                    Collection<AppointmentStatus> appointmentStatuses, LocalDateTime lowerTimeBoundary,
                                    LocalDateTime upperTimeBoundary, Boolean deleted) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Appointment> query = criteriaBuilder.createQuery(Appointment.class);
        final Root<Appointment> root = query.from(Appointment.class);

        final List<Predicate> predicates = new ArrayList<>();
        if (organizationId != null) {
            predicates.add(criteriaBuilder.equal(root.get("organizationId"), organizationId));
        }
        if (organizer != null) {
            predicates.add(criteriaBuilder.equal(root.get("organizer"), organizer));
        }
        if (createdBy != null) {
            predicates.add(criteriaBuilder.equal(root.get("createdByHash"), createdBy));
        }
        if (attendee != null) {
            predicates.add(criteriaBuilder.isMember(attendee, root.get("attendees")));
        }
        if (examinationTypes != null && !examinationTypes.isEmpty()) {
            predicates.add(root.get("examinationType").in(examinationTypes));
        }
        if (appointmentStatuses != null && !appointmentStatuses.isEmpty()) {
            predicates.add(root.get("status").in(appointmentStatuses));
        }
        if (lowerTimeBoundary != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("endTime"), lowerTimeBoundary));
        }
        if (upperTimeBoundary != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("startTime"), upperTimeBoundary));
        }
        if (deleted != null) {
            predicates.add(criteriaBuilder.equal(root.get("deleted"), deleted));
        }
        query.select(root).where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public long count(String organizationId, UUID organizer, UUID attendee, String createdBy, Collection<ExaminationType> examinationTypes,
                      Collection<AppointmentStatus> appointmentStatuses, LocalDateTime lowerTimeBoundary, LocalDateTime upperTimeBoundary,
                      Boolean deleted) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
        final Root<Appointment> root = query.from(Appointment.class);

        final List<Predicate> predicates = new ArrayList<>();
        if (organizationId != null) {
            predicates.add(criteriaBuilder.equal(root.get("organizationId"), organizationId));
        }
        if (organizer != null) {
            predicates.add(criteriaBuilder.equal(root.get("organizer"), organizer));
        }
        if (attendee != null) {
            predicates.add(criteriaBuilder.isMember(attendee, root.get("attendees")));
        }
        if (createdBy != null) {
            predicates.add(criteriaBuilder.equal(root.get("createdByHash"), createdBy));
        }
        if (examinationTypes != null && !examinationTypes.isEmpty()) {
            predicates.add(root.get("examinationType").in(examinationTypes));
        }
        if (appointmentStatuses != null && !appointmentStatuses.isEmpty()) {
            predicates.add(root.get("status").in(appointmentStatuses));
        }
        if (lowerTimeBoundary != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("endTime"), lowerTimeBoundary));
        }
        if (upperTimeBoundary != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("startTime"), upperTimeBoundary));
        }
        if (deleted != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("deleted"), deleted));
        }

        query.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        query.select(criteriaBuilder.count(root));
        return entityManager.createQuery(query).getSingleResult();
    }
}
