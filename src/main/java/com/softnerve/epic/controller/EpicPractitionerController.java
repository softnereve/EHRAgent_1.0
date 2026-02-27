//package com.softnerve.epic.controller;
//
//import com.softnerve.epic.model.dto.*;
//import com.softnerve.epic.service.EpicPractitionerService;
//import com.softnerve.epic.service.EpicSandboxDiscoveryService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import reactor.core.publisher.Mono;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Slf4j
//@RestController
//@RequestMapping("epic/practitioner")
//public class EpicPractitionerController {
//
//    private final EpicPractitionerService practitionerService;
//    private final EpicSandboxDiscoveryService sandboxDiscoveryService;
//
//    public EpicPractitionerController(EpicPractitionerService practitionerService,
//                                      EpicSandboxDiscoveryService sandboxDiscoveryService) {
//        this.practitionerService = practitionerService;
//        this.sandboxDiscoveryService = sandboxDiscoveryService;
//    }
//
//    /** Discover all practitioner IDs from sandbox, fetch full details for each, and return the list. */
////    @GetMapping("/all")
////    public Mono<ResponseEntity<List<DoctorDTO>>> getAllDoctors() {
////        log.info("📥 Request to fetch ALL doctors from Epic sandbox");
////
////        return practitionerService.getAllDoctorsFromSandbox()
////                .map(ResponseEntity::ok);
////    }
//
//    /** Full practitioner details (summary + roles) from Epic. */
//    @GetMapping("/{id}/summary")
//    public Mono<ResponseEntity<PractitionerSummaryDTO>> getPractitioner(
//            @PathVariable String id) {
//        log.info("📥 Received request to fetch practitioner summary. id={}", id);
//        return practitionerService.getPractitionerById(id)
//                .map(ResponseEntity::ok);
//    }
//
//    /** Practitioner roles and specialties from Epic. */
//    @GetMapping("/{id}/roles")
//    public Mono<ResponseEntity<List<PractitionerRoleDTO>>> getPractitionerRoles(
//            @PathVariable String id) {
//        log.info("📥 Received request to fetch practitioner roles. practitionerId={}", id);
//        return practitionerService.getPractitionerRoles(id)
//                .map(ResponseEntity::ok);
//    }
//
//    /** Search practitioners by name or specialty (Epic FHIR search). */
//    @GetMapping("/search")
//    public Mono<ResponseEntity<List<PractitionerSummaryDTO>>> searchPractitioners(
//            @RequestParam(required = false) String name,
//            @RequestParam(required = false) String specialty) {
//        log.info("📥 Received request to search practitioners. name={}, specialty={}", name, specialty);
//        return practitionerService.searchPractitioners(name, specialty)
//                .map(ResponseEntity::ok);
//    }
//
//    /** Simple doctor info by Epic Practitioner ID. */
//    @GetMapping("/{id}")
//    public Mono<ResponseEntity<DoctorDTO>> getDoctor(@PathVariable String id) {
//        return practitionerService.getDoctorById(id)
//                .map(ResponseEntity::ok);
//    }
//
//    @GetMapping("/{id}/slots")
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
//
//    /** Batch fetch multiple practitioners by their Epic FHIR IDs. */
//    @PostMapping("/batch")
//    public Mono<ResponseEntity<List<DoctorDTO>>> getPractitionersByIds(
//            @RequestBody List<String> ids) {
//        log.info("📥 Batch fetch request for {} doctor IDs", ids.size());
//        return practitionerService.getPractitionersByIds(ids)
//                .map(ResponseEntity::ok);
//    }
//
//    @GetMapping("/all")
//    public Mono<ResponseEntity<List<DoctorDTO>>> getAllPractitioners() {
//        log.info("📥 Request received to fetch ALL doctors from Epic sandbox");
//
//        return practitionerService.getAllDoctorsFromSandbox()
//                .map(ResponseEntity::ok)
//                .doOnSuccess(res -> log.info("✅ Successfully returned all doctors"))
//                .doOnError(err -> log.error("❌ Failed to fetch doctors", err));
//    }
//
//}
