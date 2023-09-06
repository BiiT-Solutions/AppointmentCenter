package com.biit.appointment.persistence.repositories;

import com.biit.appointment.persistence.entities.ExaminationForm;
import com.biit.server.persistence.repositories.ElementRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExaminationFormRepository extends ElementRepository<ExaminationForm, Long> {
}
