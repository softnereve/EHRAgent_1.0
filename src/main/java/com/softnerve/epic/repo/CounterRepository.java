package com.softnerve.epic.repo;

import com.softnerve.epic.model.Counter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CounterRepository extends MongoRepository<Counter, String> {
    Counter findByCollectionName(String collectionName);
}
