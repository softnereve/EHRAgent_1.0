package com.softnerve.epic.repo;

import com.softnerve.epic.model.dao.ObservationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ObservationRepository
        extends MongoRepository<ObservationDocument, String> {

    List<ObservationDocument> findByPatientId(String patientId);
}

