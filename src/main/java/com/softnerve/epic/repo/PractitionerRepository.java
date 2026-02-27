package com.softnerve.epic.repo;

import com.softnerve.epic.model.dao.PractitionerDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PractitionerRepository extends MongoRepository<PractitionerDocument, String> {
    Optional<PractitionerDocument> findByEpicPractitionerId(String epicPractitionerId);
}
