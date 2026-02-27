package com.softnerve.epic.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/** IDs and summaries discovered from Epic FHIR sandbox (Patient, Practitioner, Slot) for testing. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpicSandboxIdsDTO {
    @Builder.Default
    private List<String> patientIds = new ArrayList<>();
    @Builder.Default
    private List<String> practitionerIds = new ArrayList<>();
    /** Practitioner id + name from search response; use these when GET /practitioner/{id} returns 404. */
    @Builder.Default
    private List<EpicPractitionerSummaryItem> practitionerSummaries = new ArrayList<>();
    @Builder.Default
    private List<String> slotIds = new ArrayList<>();
    @Builder.Default
    private List<String> messages = new ArrayList<>();
}
