package com.softnerve.epic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data Transfer Object for Doctor information")
public class DoctorDTO {

    @Schema(description = "Practitioner ID", example = "eb55Xa5E7EWWnV5eRuWsfKQ3")
    private String id; // Practitioner ID

    @Schema(description = "Full name of the doctor", example = "Dr. John Doe")
    private String fullName;

    @Schema(description = "Qualification", example = "MD, MBBS")
    private String qualification;

    @Schema(description = "Specialization", example = "Cardiology")
    private String specialization;

    @Schema(description = "Email address", example = "dr.john@example.com")
    private String email;

    @Schema(description = "Phone number", example = "555-0199")
    private String phone;

    @Builder.Default
    @Schema(description = "Is this doctor from Epic sandbox?", example = "true")
    private boolean isFromEpic = true;

    private String timeTaken;

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }
}
