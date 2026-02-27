package com.softnerve.epic.service;

import com.softnerve.epic.model.dto.AddressDTO;
import com.softnerve.epic.model.dto.ObservationBundleDTO;
import com.softnerve.epic.model.dto.PatientSummaryDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
@Service
public interface EpicPatientService {
    Mono<ResponseEntity<String>> createObservation(String observationJson);
    Mono<ResponseEntity<String>> createPatient(String patientJson, String password, String email, List<AddressDTO> addressList);
    Mono<PatientSummaryDTO> getPatient(String patientId);
    Mono<PatientSummaryDTO> getPatientByEmail(String email);
    Mono<ObservationBundleDTO> getObservations(String patientId);
    Mono<ObservationBundleDTO> getObservationsByEmail(String email);
}

