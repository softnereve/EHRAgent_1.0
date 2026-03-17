package com.softnerve.epic.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softnerve.epic.model.dto.*;
import com.softnerve.epic.service.EpicPatientService;
import com.softnerve.epic.service.EpicPractitionerService;
import com.softnerve.epic.service.EpicSandboxDiscoveryService;
import com.softnerve.epic.service.ObservationService;
import com.softnerve.epic.utils.EpicPatientRequestMapper;
import com.softnerve.epic.exception.EpicDataMappingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/epic")
@Tag(name = "Epic Patient & Practitioner API", description = "Endpoints for interacting with Epic FHIR Sandbox for Patient and Practitioner data")
public class EpicPatientController {

    private final EpicPatientService epicPatientService;
    private final ObjectMapper objectMapper;
    private final ObservationService observationService;
    private final EpicSandboxDiscoveryService epicSandboxDiscoveryService;

    public EpicPatientController(EpicPatientService service, ObjectMapper objectMapper, ObservationService observationService,
                                 EpicSandboxDiscoveryService epicSandboxDiscoveryService, EpicPractitionerService practitionerService, EpicSandboxDiscoveryService sandboxDiscoveryService) {
        this.epicPatientService = service;
        this.objectMapper = objectMapper;
        this.observationService = observationService;
        this.epicSandboxDiscoveryService = epicSandboxDiscoveryService;
        this.practitionerService = practitionerService;
        this.sandboxDiscoveryService = sandboxDiscoveryService;
    }

    /** Discover Patient/Practitioner/Slot IDs from Epic sandbox (for testing). */
    @Operation(summary = "Discover Sandbox IDs", description = "Fetches a list of available Patient, Practitioner, and Slot IDs from the Epic sandbox for testing purposes.")
    @ApiResponse(responseCode = "200", description = "Successfully discovered IDs", content = @Content(schema = @Schema(implementation = EpicSandboxIdsDTO.class)))
    @GetMapping("/sandbox/discover")
    public Mono<ResponseEntity<EpicSandboxIdsDTO>> discoverSandboxIds() {
        long start = System.currentTimeMillis();
        return epicSandboxDiscoveryService.discoverIds()
                .map(dto -> {
                    dto.setTimeTaken((System.currentTimeMillis() - start) + "ms");
                    log.info("Request /sandbox/discover processed in {}", dto.getTimeTaken());
                    return ResponseEntity.ok(dto);
                });
    }
    private final EpicPractitionerService practitionerService;
    private final EpicSandboxDiscoveryService sandboxDiscoveryService;

    /** Full practitioner details (summary + roles) from Epic. */
    @Operation(summary = "Get Practitioner Summary", description = "Fetches comprehensive details for a practitioner by their Epic ID.")
    @ApiResponse(responseCode = "200", description = "Practitioner found", content = @Content(schema = @Schema(implementation = PractitionerSummaryDTO.class)))
    @ApiResponse(responseCode = "404", description = "Practitioner not found")
    @GetMapping("/practitioner/{id}/summary")
    public Mono<ResponseEntity<PractitionerSummaryDTO>> getPractitioner(
            @Parameter(description = "Epic Practitioner ID", example = "eb55Xa5E7EWWnV5eRuWsfKQ3") @PathVariable String id) {
        log.info("📥 Received request to fetch practitioner summary. id={}", id);
        long start = System.currentTimeMillis();
        return practitionerService.getPractitionerById(id)
                .map(dto -> {
                    dto.setTimeTaken((System.currentTimeMillis() - start) + "ms");
                    log.info("Request /practitioner/{}/summary processed in {}", id, dto.getTimeTaken());
                    return ResponseEntity.ok(dto);
                });
    }

    /** Practitioner roles and specialties from Epic. */
    @Operation(summary = "Get Practitioner Roles", description = "Fetches the roles and specialties associated with a practitioner.")
    @ApiResponse(responseCode = "200", description = "Roles fetched successfully")
    @GetMapping("/practitioner/{id}/roles")
    public Mono<ResponseEntity<List<PractitionerRoleDTO>>> getPractitionerRoles(
            @Parameter(description = "Epic Practitioner ID", example = "eb55Xa5E7EWWnV5eRuWsfKQ3") @PathVariable String id) {
        log.info("📥 Received request to fetch practitioner roles. practitionerId={}", id);
        long start = System.currentTimeMillis();
        return practitionerService.getPractitionerRoles(id)
                .map(list -> {
                    String time = (System.currentTimeMillis() - start) + "ms";
                    list.forEach(item -> item.setTimeTaken(time));
                    log.info("Request /practitioner/{}/roles processed in {}", id, time);
                    return ResponseEntity.ok(list);
                });
    }
    /** Search practitioners by name or specialty (Epic FHIR search). */
    @Operation(summary = "Search Practitioners", description = "Searches for practitioners in Epic by name or specialty.")
    @ApiResponse(responseCode = "200", description = "Search results returned")
    @GetMapping("/practitioner/search")
    public Mono<ResponseEntity<List<PractitionerSummaryDTO>>> searchPractitioners(
            @Parameter(description = "Practitioner name", example = "Albert Johnson") @RequestParam(required = false) String name,
            @Parameter(description = "Practitioner specialty", example = "Internal Medicine") @RequestParam(required = false) String specialty) {
        log.info("📥 Received request to search practitioners. name={}, specialty={}", name, specialty);
        long start = System.currentTimeMillis();
        return practitionerService.searchPractitioners(name, specialty)
                .map(list -> {
                    String time = (System.currentTimeMillis() - start) + "ms";
                    list.forEach(item -> item.setTimeTaken(time));
                    log.info("Request /practitioner/search processed in {}", time);
                    return ResponseEntity.ok(list);
                });
    }

    /** Simple doctor info by Epic Practitioner ID. */
    @Operation(summary = "Get Doctor Info", description = "Fetches basic doctor information for the provided Epic Practitioner ID.")
    @ApiResponse(responseCode = "200", description = "Doctor info found", content = @Content(schema = @Schema(implementation = DoctorDTO.class)))
    @GetMapping("/practitioner/{id}")
    public Mono<ResponseEntity<DoctorDTO>> getDoctor(@Parameter(description = "Epic Practitioner ID", example = "T... (Epic ID)") @PathVariable String id) {
        long start = System.currentTimeMillis();
        return practitionerService.getDoctorById(id)
                .map(dto -> {
                    dto.setTimeTaken((System.currentTimeMillis() - start) + "ms");
                    log.info("Request /practitioner/{} processed in {}", id, dto.getTimeTaken());
                    return ResponseEntity.ok(dto);
                });
    }
//not working
//    @GetMapping("/practitioner/{id}/slots")
//    public Mono<ResponseEntity<SlotBundleDTO>> getSlots(
//            @PathVariable String id,
//            @RequestParam(required = false) String date) {
//
//        LocalDate targetDate = (date != null) ? LocalDate.parse(date) : LocalDate.now().plusDays(1);
//
//        return practitionerService
//                .getAvailableSlots(id, targetDate)
//                .map(ResponseEntity::ok);
//    }



    @Operation(summary = "Get All Practitioners", description = "Fetches all practitioners available in the Epic sandbox.")
    @ApiResponse(responseCode = "200", description = "All practitioners fetched")
    @GetMapping("/practitioner/all")
    public Mono<ResponseEntity<DoctorListResponse>> getAllPractitioners() {
        log.info("📥 Request received to fetch ALL doctors from Epic sandbox");

        long start = System.currentTimeMillis();
        return practitionerService.getAllDoctorsFromSandbox()
                .map(doctors -> {
                    String time = (System.currentTimeMillis() - start) + "ms";
                    DoctorListResponse body = DoctorListResponse.builder()
                            .doctors(doctors)
                            .timeTaken(time)
                            .build();
                    log.info("✅ Successfully returned all doctors in {}", time);
                    return ResponseEntity.ok()
                            .header("X-Time-Taken", time)
                            .body(body);
                })
                .doOnError(err -> log.error("❌ Failed to fetch doctors", err));
    }











/*
Patient APIS
 */




//    @PostMapping("/need-encounter/epic-observation")
//    public Mono<ResponseEntity<String>> createObservation(
//            @RequestBody String observationJson) {
//
//        log.info("➡️ Received request to create Observation in Epic");
//
//        return epicPatientService.createObservation(observationJson);
//    }
    @Operation(summary = "Create Patient", description = "Creates a new patient in Epic using the provided details. This also maps the request to Epic-specific format.")
    @ApiResponse(responseCode = "201", description = "Patient created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @PostMapping("/patient")
    public Mono<ResponseEntity<String>> createPatient(
            @Valid @RequestBody CreatePatientRequest request
    ) {
        try {
            EpicPatientDTO epicDto = EpicPatientRequestMapper.toEpicPatient(request);
            String epicJson = objectMapper.writeValueAsString(epicDto);
            return epicPatientService.createPatient(
                    epicJson,
                    request.getPassword(),
                    request.getEmail(),
                    request.getAddress(),
                    System.currentTimeMillis()
            );
        } catch (Exception e) {
            return Mono.error(new EpicDataMappingException("Failed to serialize patient data", e));
        }
    }

    @Operation(summary = "Get Patient by ID", description = "Fetches patient summary data from Epic using the Epic Patient ID.")
    @ApiResponse(responseCode = "200", description = "Patient found", content = @Content(schema = @Schema(implementation = PatientSummaryDTO.class)))
    @ApiResponse(responseCode = "404", description = "Patient not found")
    @GetMapping("/{patientId}")
    public Mono<ResponseEntity<PatientSummaryDTO>> getPatient(
            @Parameter(description = "Epic Patient ID", example = "erXuFYUfucBZaryVksYEcMg3") @PathVariable String patientId) {

        log.info("📥 Received request to fetch patient. patientId={}", patientId);
        long start = System.currentTimeMillis();
        return epicPatientService.getPatient(patientId)
                .map(dto -> {
                    dto.setTimeTaken((System.currentTimeMillis() - start) + "ms");
                    log.info("Request /{} processed in {}", patientId, dto.getTimeTaken());
                    return ResponseEntity.ok(dto);
                });
    }
    /// custom api
    @Operation(summary = "Get Patient by Email", description = "Fetches patient summary data from Epic using the patient's registerd email address.")
    @ApiResponse(responseCode = "200", description = "Patient found")
    @GetMapping("/patient/{email}")
    public Mono<ResponseEntity<PatientSummaryDTO>> getPatientByEmail(
            @Parameter(description = "Patient email", example = "knixontestemail@epic.com") @PathVariable String email) {
        log.info("📥 Received request to fetch patient. email={}", email);
        long start = System.currentTimeMillis();
        return epicPatientService.getPatientByEmail(email)
                .map(dto -> {
                    dto.setTimeTaken((System.currentTimeMillis() - start) + "ms");
                    log.info("Request /patient/{} processed in {}", email, dto.getTimeTaken());
                    return ResponseEntity.ok(dto);
                });
    }


    @GetMapping("/observation/{patientId}")
    public Mono<ResponseEntity<ObservationBundleDTO>> getObservations(
            @Parameter(description = "Epic Patient ID", example = "erXuFYUfucBZaryVksYEcMg3") @PathVariable String patientId) {
        long start = System.currentTimeMillis();
        return epicPatientService.getObservations(patientId)
                .map(dto -> {
                    dto.setTimeTaken((System.currentTimeMillis() - start) + "ms");
                    log.info("Request /observation/{} processed in {}", patientId, dto.getTimeTaken());
                    return ResponseEntity.ok(dto);
                });
    }
    @Operation(summary = "Get Observations by Email", description = "Fetches a bundle of observations (test results) for a patient from Epic using their email address.")
    @ApiResponse(responseCode = "200", description = "Observations fetched")
    @GetMapping("/observation/by-email/{email}")
    public Mono<ResponseEntity<ObservationBundleDTO>> getObservationsByEmail(
            @Parameter(description = "Patient email", example = "knixontestemail@epic.com") @PathVariable String email) {
        log.info("Fetching observations from Epic for email={}", email);
        long start = System.currentTimeMillis();
        return epicPatientService.getObservationsByEmail(email)
                .map(dto -> {
                    dto.setTimeTaken((System.currentTimeMillis() - start) + "ms");
                    log.info("Request /observation/by-email/{} processed in {}", email, dto.getTimeTaken());
                    return ResponseEntity.ok(dto);
                });
    }
    @Operation(summary = "Create Custom Observation", description = "Saves a custom observation for a patient in the local MongoDB storage.")
    @ApiResponse(responseCode = "200", description = "Observation saved successfully")
    @PostMapping("/custom/observation")
    public Mono<ResponseEntity<ObservationBundleDTO>> createObservation(
            @Valid @RequestBody CreateObservationRequest request) {
 
        log.info("Received request to save observation for patientId={}", request.getPatientId());
        long start = System.currentTimeMillis();
        return observationService.saveObservation(request)
                .map(dto -> {
                    dto.setTimeTaken((System.currentTimeMillis() - start) + "ms");
                    log.info("Request /custom/observation processed in {}", dto.getTimeTaken());
                    return ResponseEntity.ok(dto);
                });
    }
    @Operation(summary = "Get Observations from Mongo", description = "Fetches observations for a specific patient that were saved in the local MongoDB.")
    @ApiResponse(responseCode = "200", description = "Observations fetched from Mongo")
    @GetMapping("/mongo-observation/{patientId}")
    public Mono<ResponseEntity<ObservationBundleDTO>> getObservationsFromMongo(
            @Parameter(description = "Epic Patient ID", example = "erXuFYUfucBZaryVksYEcMg3") @PathVariable String patientId) {
 
        log.info("Fetching observations from Mongo for patientId={}", patientId);
        long start = System.currentTimeMillis();
        return observationService.getObservationsFromMongo(patientId)
                .map(dto -> {
                    dto.setTimeTaken((System.currentTimeMillis() - start) + "ms");
                    log.info("Request /mongo-observation/{} processed in {}", patientId, dto.getTimeTaken());
                    return ResponseEntity.ok(dto);
                });
    }

    @Operation(summary = "Get Clinical Note by DocumentReference ID", description = "Fetch a clinical note (DocumentReference) by its Epic ID.")
    @ApiResponse(responseCode = "200", description = "Clinical note found", content = @Content(schema = @Schema(implementation = ClinicalNoteDTO.class)))
    @GetMapping("/document/{documentId}")
    public Mono<ResponseEntity<ClinicalNoteDTO>> getClinicalNoteById(
            @Parameter(description = "Epic DocumentReference ID", example = "eXR.vCaCsTte6MWYJJim4wA3") @PathVariable String documentId) {
        log.info("Received request to fetch clinical note. documentId={}", documentId);
        long start = System.currentTimeMillis();
        return epicPatientService.getClinicalNoteById(documentId)
                .map(dto -> {
                    dto.setTimeTaken((System.currentTimeMillis() - start) + "ms");
                    log.info("Request /document/{} processed in {}", documentId, dto.getTimeTaken());
                    return ResponseEntity.ok(dto);
                });
    }

    @Operation(summary = "Get Clinical Notes by Patient", description = "Search clinical notes (DocumentReference) for a patient in Epic.")
    @ApiResponse(responseCode = "200", description = "Clinical notes fetched", content = @Content(schema = @Schema(implementation = ClinicalNoteBundleDTO.class)))
    @GetMapping("/document/by-patient/{patientId}")
    public Mono<ResponseEntity<ClinicalNoteBundleDTO>> getClinicalNotesByPatient(
            @Parameter(description = "Epic Patient ID", example = "erXuFYUfucBZaryVksYEcMg3") @PathVariable String patientId) {
        log.info("Received request to fetch clinical notes by patient. patientId={}", patientId);
        long start = System.currentTimeMillis();
        return epicPatientService.getClinicalNotesByPatient(patientId)
                .map(dto -> {
                    dto.setTimeTaken((System.currentTimeMillis() - start) + "ms");
                    log.info("Request /document/by-patient/{} processed in {}", patientId, dto.getTimeTaken());
                    return ResponseEntity.ok(dto);
                });
    }

    @GetMapping(value = "/practitioner/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<DoctorDTO> streamAllPractitioners(@RequestParam(name = "concurrency", defaultValue = "3") int concurrency) {
        log.info("Streaming practitioners with concurrency={}", concurrency);
        return practitionerService.streamAllDoctorsFromSandbox(concurrency);
    }

    @GetMapping("/practitioner/page")
    public Mono<ResponseEntity<DoctorListResponse>> getPractitionersPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        long start = System.currentTimeMillis();
        return practitionerService.getDoctorsPage(page, size)
                .map(doctors -> {
                    String time = (System.currentTimeMillis() - start) + "ms";
                    log.info("Request /practitioner/page?page={}&size={} processed in {}", page, size, time);
                    DoctorListResponse body = DoctorListResponse.builder()
                            .doctors(doctors)
                            .timeTaken(time)
                            .build();
                    return ResponseEntity.ok()
                            .header("X-Time-Taken", time)
                            .body(body);
                });
    }




}

