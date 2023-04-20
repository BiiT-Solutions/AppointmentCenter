package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.ExaminationForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExaminationFormRepository extends JpaRepository<ExaminationForm, Long> {
}
