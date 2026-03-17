package com.softnerve.epic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Summary of a clinical note (DocumentReference)")
public class ClinicalNoteSummaryDTO {

    @Schema(description = "Epic DocumentReference ID", example = "e8-p-X-A-B-C")
    private String documentId;

    @Schema(description = "Title of the note", example = "Progress Note")
    private String title;

    @Schema(description = "Type of the note", example = "Physician Progress Note")
    private String type;

    @Schema(description = "Status of the document", example = "current")
    private String status;

    @Schema(description = "Date and time the note was created")
    private OffsetDateTime date;

    @Schema(description = "Epic Patient ID", example = "erXuFYUfucBZaryVksYEcMg3")
    private String patientId;

    @Schema(description = "Author of the note", example = "Dr. Albert Johnson")
    private String author;

    @Schema(description = "Associated encounter ID", example = "e12345")
    private String encounterId;
}
