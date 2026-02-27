//package com.softnerve.epic.controller;
//
//import com.softnerve.epic.model.dto.AppointmentBookRequestDTO;
//import com.softnerve.epic.model.dto.AppointmentFindRequestDTO;
//import com.softnerve.epic.model.dto.AppointmentRequestDTO;
//import com.softnerve.epic.model.dto.AppointmentResponseDTO;
//import com.softnerve.epic.model.dto.SlotDTO;
//import com.softnerve.epic.service.EpicAppointmentService;
//import jakarta.validation.Valid;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//
//@Slf4j
//@RestController
//@RequestMapping("/epic/appointment")
//public class EpicAppointmentController {
//
//    private final EpicAppointmentService appointmentService;
//
//    public EpicAppointmentController(EpicAppointmentService appointmentService) {
//        this.appointmentService = appointmentService;
//        log.info("EpicAppointmentController initialized");
//    }
//
//    /** Book an appointment in Epic (patientId, practitionerId, slotId must be Epic FHIR IDs). */
//    @PostMapping
//    public Mono<ResponseEntity<AppointmentResponseDTO>> book(
//            @RequestBody @Valid AppointmentRequestDTO dto) {
//
//        return appointmentService.bookAppointment(dto)
//                .map(ResponseEntity::ok);
//    }
//
//    /** Find potential appointment slots (STU3 $find). */
//   @PostMapping("/find")
//    public Mono<ResponseEntity<List<AppointmentResponseDTO>>> find(
//            @RequestBody @Valid AppointmentFindRequestDTO dto) {
//
//        return appointmentService.findAppointments(dto)
//                .map(ResponseEntity::ok);
//    }
//
//    /** Book an appointment (STU3 $book). */
//    @PostMapping("/book/stu3")
//    public Mono<ResponseEntity<AppointmentResponseDTO>> bookStu3(
//            @RequestBody @Valid AppointmentBookRequestDTO dto) {
//
//        return appointmentService.bookAppointmentSTU3(dto)
//                .map(ResponseEntity::ok);
//    }
//
//    @PutMapping("/{id}/cancel")
//    public Mono<ResponseEntity<String>> cancel(@PathVariable String id) {
//        return appointmentService.cancelAppointment(id)
//                .thenReturn(ResponseEntity.ok("Appointment cancelled"));
//    }
//
//    @GetMapping("/patient/{patientId}")
//    public Mono<ResponseEntity<List<AppointmentResponseDTO>>> getByPatient(
//            @PathVariable String patientId) {
//
//        return appointmentService.getAppointmentsByPatient(patientId)
//                .map(ResponseEntity::ok);
//    }
//
//    @GetMapping("/slots/{practitionerId}")
//    public Mono<ResponseEntity<List<SlotDTO>>> getSlots(
//            @PathVariable String practitionerId) {
//        return appointmentService.findSlots(practitionerId)
//                .map(ResponseEntity::ok);
//    }
//}
