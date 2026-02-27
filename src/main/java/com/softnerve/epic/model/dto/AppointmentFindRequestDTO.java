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
public class AppointmentFindRequestDTO {

    @NotBlank
    private String patientId;

    @NotBlank
    private String start;  // ISO: 2026-02-18T00:00:00Z

    @NotBlank
    private String end;

    private String practitionerId;
}
