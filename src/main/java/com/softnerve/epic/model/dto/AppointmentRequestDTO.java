package com.softnerve.epic.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppointmentRequestDTO {

    @NotBlank
    private String patientId;

    @NotBlank
    private String practitionerId;

    @NotBlank
    private String slotId;

    private String reason;
}

