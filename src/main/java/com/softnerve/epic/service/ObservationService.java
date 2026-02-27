package com.softnerve.epic.service;

import com.softnerve.epic.model.dto.CreateObservationRequest;
import com.softnerve.epic.model.dto.ObservationBundleDTO;
import reactor.core.publisher.Mono;

public interface ObservationService {
    Mono<ObservationBundleDTO> saveObservation(CreateObservationRequest request);
    Mono<ObservationBundleDTO> getObservationsFromMongo(String patientId);
}

