package com.softnerve.epic.service;

import com.softnerve.epic.model.dto.AppointmentBookRequestDTO;
import com.softnerve.epic.model.dto.AppointmentFindRequestDTO;
import com.softnerve.epic.model.dto.AppointmentRequestDTO;
import com.softnerve.epic.model.dto.AppointmentResponseDTO;
import com.softnerve.epic.model.dto.SlotDTO;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface EpicAppointmentService {
    Mono<List<SlotDTO>> findSlots(String practitionerId);

    Mono<AppointmentResponseDTO> bookAppointment(@Valid AppointmentRequestDTO dto);

    // Mono<List<AppointmentResponseDTO>> findAppointments(@Valid AppointmentFindRequestDTO dto);

    Mono<AppointmentResponseDTO> bookAppointmentSTU3(@Valid AppointmentBookRequestDTO dto);

    Mono<Void> cancelAppointment(String id);

    Mono<List<AppointmentResponseDTO>> getAppointmentsByPatient(String patientId);
    Mono<List<AppointmentResponseDTO>> findAppointments(AppointmentFindRequestDTO dto);


    // Service interface for Epic appointment functionality
    // Currently minimal - methods removed as requested
}