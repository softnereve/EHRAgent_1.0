package com.softnerve.epic.service;

import com.softnerve.epic.model.Counter;
import com.softnerve.epic.repo.CounterRepository;
import org.springframework.stereotype.Service;

@Service
public class CounterService {

    private final CounterRepository counterRepository;

    public CounterService(CounterRepository counterRepository) {
        this.counterRepository = counterRepository;
    }

    public String getNextPatientId() {
        Counter counter = counterRepository.findByCollectionName("patients");
        if (counter == null) {
            counter = new Counter("patients");
        }
        counter.increment();
        counterRepository.save(counter);
        return "PAT" + String.format("%010d", counter.getValue());
    }

    public String getNextChildId() {
        Counter counter = counterRepository.findByCollectionName("children");
        if (counter == null) {
            counter = new Counter("children");
        }
        counter.increment();
        counterRepository.save(counter);
        return "CHI" + String.format("%010d", counter.getValue());
    }

    public String getNextDoctorId() {
        Counter counter = counterRepository.findByCollectionName("doctors");
        if (counter == null) {
            counter = new Counter("doctors");
        }
        counter.increment();
        counterRepository.save(counter);
        return "DOC" + String.format("%010d", counter.getValue());
    }
}
