package com.softnerve.epic.repo;

import com.softnerve.epic.model.dao.PatientDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EpicPatientRepository
        extends MongoRepository<PatientDocument, String> {

    Optional<PatientDocument> findByEpicPatientId(String epicPatientId);

    Optional<PatientDocument> findFirstByEmailOrderByCreatedAtDesc(String email);

    PatientDocument findByEmail(String email);
//    Optional<PatientDocument> findFirstByEmailOrderByCreatedAtDesc(String email);

//    Optional<PatientDocument> findByEmail(String email);
}

