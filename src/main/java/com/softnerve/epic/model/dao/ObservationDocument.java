package com.softnerve.epic.model.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.OffsetDateTime;

@Document(collection = "observations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ObservationDocument {

    @Id
    private String id;

    private String observationId;
    private String testName;
    private String category;
    private String status;

    private String patientId;
    private String encounterId;

    // 🔥 CHANGE HERE
    private OffsetDateTime effectiveDate;
    private OffsetDateTime issuedDate;

    private String value;
    private String unit;
    private String referenceRange;
    private String interpretation;

    private Instant createdAt;
}
