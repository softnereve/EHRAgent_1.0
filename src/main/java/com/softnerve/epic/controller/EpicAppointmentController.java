// package com.softnerve.epic.controller;
 
// import com.softnerve.epic.model.dto.AppointmentBookRequestDTO;
// import com.softnerve.epic.model.dto.AppointmentFindRequestDTO;
// import com.softnerve.epic.model.dto.AppointmentRequestDTO;
// import com.softnerve.epic.model.dto.AppointmentResponseDTO;
// import com.softnerve.epic.model.dto.SlotDTO;
// import com.softnerve.epic.service.EpicAppointmentService;
// import jakarta.validation.Valid;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import reactor.core.publisher.Mono;
 
// import java.util.List;
 
// @Slf4j
// @RestController
// @RequestMapping("/epic/appointment")
// public class EpicAppointmentController {
 
//     private final EpicAppointmentService appointmentService;
 
//     public EpicAppointmentController(EpicAppointmentService appointmentService) {
//         this.appointmentService = appointmentService;
//         log.info("EpicAppointmentController initialized");
//     }
 
//     /** Book an appointment in Epic (patientId, practitionerId, slotId must be Epic FHIR IDs). */
//     @PostMapping
//     public Mono<ResponseEntity<AppointmentResponseDTO>> book(
//             @RequestBody @Valid AppointmentRequestDTO dto) {
//         log.info("📥 Request to book appointment");
//         long start = System.currentTimeMillis();
//         return appointmentService.bookAppointment(dto)
//                 .map(resp -> {
//                     resp.setTimeTaken((System.currentTimeMillis() - start) + "ms");
//                     log.info("Request /epic/appointment (book) processed in {}", resp.getTimeTaken());
//                     return ResponseEntity.ok(resp);
//                 });
//     }
 
//     /** Find potential appointment slots (STU3 $find). */
//    @PostMapping("/find")
//     public Mono<ResponseEntity<List<AppointmentResponseDTO>>> find(
//             @RequestBody @Valid AppointmentFindRequestDTO dto) {
//         log.info("📥 Request to find appointments");
//         long start = System.currentTimeMillis();
//         return appointmentService.findAppointments(dto)
//                 .map(list -> {
//                     String timeTaken = (System.currentTimeMillis() - start) + "ms";
//                     list.forEach(resp -> resp.setTimeTaken(timeTaken));
//                     log.info("Request /epic/appointment/find processed in {}", timeTaken);
//                     return ResponseEntity.ok(list);
//                 });
//     }
 
//     /** Book an appointment (STU3 $book). */
//     @PostMapping("/book/stu3")
//     public Mono<ResponseEntity<AppointmentResponseDTO>> bookStu3(
//             @RequestBody @Valid AppointmentBookRequestDTO dto) {
//         log.info("📥 Request to book appointment (STU3)");
//         long start = System.currentTimeMillis();
//         return appointmentService.bookAppointmentSTU3(dto)
//                 .map(resp -> {
//                     resp.setTimeTaken((System.currentTimeMillis() - start) + "ms");
//                     log.info("Request /epic/appointment/book/stu3 processed in {}", resp.getTimeTaken());
//                     return ResponseEntity.ok(resp);
//                 });
//     }
 
//     @PutMapping("/{id}/cancel")
//     public Mono<ResponseEntity<String>> cancel(@PathVariable String id) {
//         log.info("📥 Request to cancel appointment id={}", id);
//         return appointmentService.cancelAppointment(id)
//                 .thenReturn(ResponseEntity.ok("Appointment cancelled"));
//     }
 
//     @GetMapping("/patient/{patientId}")
//     public Mono<ResponseEntity<List<AppointmentResponseDTO>>> getByPatient(
//             @PathVariable String patientId) {
//         log.info("📥 Request to fetch appointments for patientId={}", patientId);
//         long start = System.currentTimeMillis();
//         return appointmentService.getAppointmentsByPatient(patientId)
//                 .map(list -> {
//                     String timeTaken = (System.currentTimeMillis() - start) + "ms";
//                     list.forEach(resp -> resp.setTimeTaken(timeTaken));
//                     log.info("Request /epic/appointment/patient/{} processed in {}", patientId, timeTaken);
//                     return ResponseEntity.ok(list);
//                 });
//     }
 
//     @GetMapping("/slots/{practitionerId}")
//     public Mono<ResponseEntity<List<SlotDTO>>> getSlots(
//             @PathVariable String practitionerId) {
//         log.info("📥 Request to fetch slots for practitionerId={}", practitionerId);
//         long start = System.currentTimeMillis();
//         return appointmentService.findSlots(practitionerId)
//                 .map(list -> {
//                     String timeTaken = (System.currentTimeMillis() - start) + "ms";
//                     list.forEach(resp -> resp.setTimeTaken(timeTaken));
//                     log.info("Request /epic/appointment/slots/{} processed in {}", practitionerId, timeTaken);
//                     return ResponseEntity.ok(list);
//                 });
//     }
// }