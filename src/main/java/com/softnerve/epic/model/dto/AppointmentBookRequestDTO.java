package com.softnerve.epic.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentBookRequestDTO {

    @NotBlank
    private String patientId;

    @NotBlank
    private String appointmentId; // FHIR ID from $find results

    private String note; // Optional free-text note
}
