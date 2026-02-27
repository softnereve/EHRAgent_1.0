package com.softnerve.epic.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.OffsetDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ObservationSummaryDTO {

    private String observationId;
    private String testName;
    private String category;
    private String status;

    private String patientId;
    private String encounterId;

    private OffsetDateTime effectiveDate;
    private OffsetDateTime issuedDate;

    private String value;        // "5.1", "322 10*3/uL", "Genotype Value"
    private String unit;

    private String referenceRange; // "150 - 399 10*3/uL"
    private String interpretation;

}
