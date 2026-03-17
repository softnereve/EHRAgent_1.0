// package com.softnerve.epic.controller;
 
// import com.softnerve.epic.model.dto.*;
// import com.softnerve.epic.service.EpicPractitionerService;
// import com.softnerve.epic.service.EpicSandboxDiscoveryService;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import reactor.core.publisher.Mono;
 
// import java.time.LocalDate;
// import java.util.List;
 
// @Slf4j
// @RestController
// @RequestMapping("epic/practitioner")
// public class EpicPractitionerController {
 
//     private final EpicPractitionerService practitionerService;
//     private final EpicSandboxDiscoveryService sandboxDiscoveryService;
 
//     public EpicPractitionerController(EpicPractitionerService practitionerService,
//                                       EpicSandboxDiscoveryService sandboxDiscoveryService) {
//         this.practitionerService = practitionerService;
//         this.sandboxDiscoveryService = sandboxDiscoveryService;
//     }
 
//     /** Full practitioner details (summary + roles) from Epic. */
//     @GetMapping("/{id}/summary")
//     public Mono<ResponseEntity<PractitionerSummaryDTO>> getPractitioner(
//             @PathVariable String id) {
//         log.info("📥 Received request to fetch practitioner summary. id={}", id);
//         long start = System.currentTimeMillis();
//         return practitionerService.getPractitionerById(id)
//                 .map(dto -> {
//                     dto.setTimeTaken((System.currentTimeMillis() - start) + "ms");
//                     log.info("Request /epic/practitioner/{}/summary processed in {}", id, dto.getTimeTaken());
//                     return ResponseEntity.ok(dto);
//                 });
//     }
 
//     /** Practitioner roles and specialties from Epic. */
//     @GetMapping("/{id}/roles")
//     public Mono<ResponseEntity<List<PractitionerRoleDTO>>> getPractitionerRoles(
//             @PathVariable String id) {
//         log.info("📥 Received request to fetch practitioner roles. practitionerId={}", id);
//         long start = System.currentTimeMillis();
//         return practitionerService.getPractitionerRoles(id)
//                 .map(list -> {
//                     String timeTaken = (System.currentTimeMillis() - start) + "ms";
//                     list.forEach(dto -> dto.setTimeTaken(timeTaken));
//                     log.info("Request /epic/practitioner/{}/roles processed in {}", id, timeTaken);
//                     return ResponseEntity.ok(list);
//                 });
//     }
 
//     /** Search practitioners by name or specialty (Epic FHIR search). */
//     @GetMapping("/search")
//     public Mono<ResponseEntity<List<PractitionerSummaryDTO>>> searchPractitioners(
//             @RequestParam(required = false) String name,
//             @RequestParam(required = false) String specialty) {
//         log.info("📥 Received request to search practitioners. name={}, specialty={}", name, specialty);
//         long start = System.currentTimeMillis();
//         return practitionerService.searchPractitioners(name, specialty)
//                 .map(list -> {
//                     String timeTaken = (System.currentTimeMillis() - start) + "ms";
//                     list.forEach(dto -> dto.setTimeTaken(timeTaken));
//                     log.info("Request /epic/practitioner/search processed in {}", timeTaken);
//                     return ResponseEntity.ok(list);
//                 });
//     }
 
//     /** Simple doctor info by Epic Practitioner ID. */
//     @GetMapping("/{id}")
//     public Mono<ResponseEntity<DoctorDTO>> getDoctor(@PathVariable String id) {
//         long start = System.currentTimeMillis();
//         return practitionerService.getDoctorById(id)
//                 .map(dto -> {
//                     dto.setTimeTaken((System.currentTimeMillis() - start) + "ms");
//                     log.info("Request /epic/practitioner/{} processed in {}", id, dto.getTimeTaken());
//                     return ResponseEntity.ok(dto);
//                 });
//     }
 
//     @GetMapping("/{id}/slots")
//     public Mono<ResponseEntity<SlotBundleDTO>> getSlots(
//             @PathVariable String id,
//             @RequestParam(required = false) String date) {
 
//         log.info("📥 Request to fetch slots for practitionerId={}, date={}", id, date);
//         long start = System.currentTimeMillis();
//         LocalDate targetDate = (date != null) ? LocalDate.parse(date) : LocalDate.now().plusDays(1);
 
//         return practitionerService
//                 .getAvailableSlots(id, targetDate)
//                 .map(dto -> {
//                     dto.setTimeTaken((System.currentTimeMillis() - start) + "ms");
//                     log.info("Request /epic/practitioner/{}/slots processed in {}", id, dto.getTimeTaken());
//                     return ResponseEntity.ok(dto);
//                 });
//     }
 
//     /** Batch fetch multiple practitioners by their Epic FHIR IDs. */
//     @PostMapping("/batch")
//     public Mono<ResponseEntity<List<DoctorDTO>>> getPractitionersByIds(
//             @RequestBody List<String> ids) {
//         log.info("📥 Batch fetch request for {} doctor IDs", ids.size());
//         long start = System.currentTimeMillis();
//         return practitionerService.getPractitionersByIds(ids)
//                 .map(list -> {
//                     String timeTaken = (System.currentTimeMillis() - start) + "ms";
//                     list.forEach(dto -> dto.setTimeTaken(timeTaken));
//                     log.info("Request /epic/practitioner/batch processed in {}", timeTaken);
//                     return ResponseEntity.ok(list);
//                 });
//     }
 
//     @GetMapping("/all")
//     public Mono<ResponseEntity<List<DoctorDTO>>> getAllPractitioners() {
//         log.info("📥 Request received to fetch ALL doctors from Epic sandbox");
//         long start = System.currentTimeMillis();
//         return practitionerService.getAllDoctorsFromSandbox()
//                 .map(list -> {
//                     String timeTaken = (System.currentTimeMillis() - start) + "ms";
//                     list.forEach(dto -> dto.setTimeTaken(timeTaken));
//                     log.info("Request /epic/practitioner/all processed in {}", timeTaken);
//                     return ResponseEntity.ok(list);
//                 });
//     }
// }
