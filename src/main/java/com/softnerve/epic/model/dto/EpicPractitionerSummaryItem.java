package com.softnerve.epic.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Minimal practitioner info from Epic search (use this when read-by-ID returns 404). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpicPractitionerSummaryItem {
    private String id;
    private String displayName;
}
