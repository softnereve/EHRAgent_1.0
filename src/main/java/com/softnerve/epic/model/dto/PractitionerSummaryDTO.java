package com.softnerve.epic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Summary of practitioner/doctor information")
public class PractitionerSummaryDTO {
    @Schema(description = "Local database ID", example = "pract-123")
    private String doctorId;

    @Schema(description = "Epic Practitioner ID", example = "eb55Xa5E7EWWnV5eRuWsfKQ3")
    private String epicPractitionerId;

    @Schema(description = "Full name of the practitioner", example = "Dr. Alice Smith")
    private String fullName;

    @Schema(description = "Gender", example = "female")
    private String gender;

    @Schema(description = "List of specialties", example = "[\"Internal Medicine\"]")
    private List<String> specialties;

    @Schema(description = "Contact details", example = "[\"phone: 555-555-5555\"]")
    private List<String> telecom;

    @Builder.Default
    @Schema(description = "Flag indicating if the record is from Epic", example = "true")
    private boolean isFromEpic = true;

    private String timeTaken;

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }
}
