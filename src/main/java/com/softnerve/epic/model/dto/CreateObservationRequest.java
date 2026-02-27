package com.softnerve.epic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request object for creating a new observation/test result")
public class CreateObservationRequest {

    @NotBlank
    @Schema(description = "Unique ID for the observation", example = "obs-12345")
    private String observationId;

    @NotBlank
    @Schema(description = "Name of the test or observation", example = "Blood Glucose")
    private String testName;

    @Schema(description = "Category of the observation", example = "vital-signs")
    private String category;

    @Schema(description = "Status of the observation", example = "final")
    private String status;

    @NotBlank
    @Schema(description = "Patient ID associated with this observation", example = "E8... (Epic ID)")
    private String patientId;

    @Schema(description = "Encounter ID associated with this observation", example = "enc-555")
    private String encounterId;

    @Schema(description = "Date and time the observation was effective", example = "2023-10-25T10:00:00Z")
    private OffsetDateTime effectiveDate;

    @Schema(description = "Date and time the observation was issued", example = "2023-10-25T11:00:00Z")
    private OffsetDateTime issuedDate;

    @Schema(description = "Result value", example = "95")
    private String value;

    @Schema(description = "Unit of measurement", example = "mg/dL")
    private String unit;

    @Schema(description = "Reference range for the test", example = "70-99 mg/dL")
    private String referenceRange;

    @Schema(description = "Interpretation of the result", example = "Normal")
    private String interpretation;
}
