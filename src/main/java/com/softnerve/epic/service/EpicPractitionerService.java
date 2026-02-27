package com.softnerve.epic.service;

import com.mongodb.client.MongoIterable;
import com.softnerve.epic.model.dto.DoctorDTO;
import com.softnerve.epic.model.dto.PractitionerSummaryDTO;
import com.softnerve.epic.model.dto.PractitionerRoleDTO;
import com.softnerve.epic.model.dto.SlotBundleDTO;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EpicPractitionerService {
    Mono<DoctorDTO> getDoctorById(String id);

    Mono<SlotBundleDTO> getAvailableSlots(String id, LocalDate parse);
    Mono<PractitionerSummaryDTO> getPractitionerById(String id);
    Mono<List<PractitionerRoleDTO>> getPractitionerRoles(String practitionerId);
    Mono<List<PractitionerSummaryDTO>> searchPractitioners(String name, String specialty);

    Mono<List<DoctorDTO>> getPractitionersByIds(List<String> ids);

//    Mono<List<DoctorDTO>> getAllSandboxDoctors();

    Mono<List<DoctorDTO>> getAllDoctorsFromSandbox();

}
