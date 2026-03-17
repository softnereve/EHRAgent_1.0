package com.softnerve.epic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Clinical Note mapped from FHIR DocumentReference")
public class ClinicalNoteDTO {
    @Schema(description = "DocumentReference ID")
    private String id;
    @Schema(description = "Status", example = "current")
    private String status;
    @Schema(description = "Type of document (e.g., Clinical Note)")
    private String type;
    @Schema(description = "Category of document")
    private String category;
    @Schema(description = "Patient reference", example = "Patient/123")
    private String patientReference;
    @Schema(description = "Date/Time of the document")
    private String date;
    @Schema(description = "Author display")
    private String author;
    @Schema(description = "Custodian organization")
    private String custodian;
    @Schema(description = "Description or title")
    private String description;
    @Schema(description = "Attachment content type", example = "application/pdf")
    private String contentType;
    @Schema(description = "Attachment URL")
    private String contentUrl;
    private String timeTaken;

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }
}
