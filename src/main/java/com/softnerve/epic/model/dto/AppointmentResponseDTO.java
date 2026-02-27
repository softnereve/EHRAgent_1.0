package com.softnerve.epic.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDTO {

    private String appointmentId;
    private String patientId;
    private String practitionerId;
    private String status;
    private String start;
    private String end;
}
