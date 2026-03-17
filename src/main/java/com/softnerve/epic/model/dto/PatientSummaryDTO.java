package com.softnerve.epic.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Summary of patient information from Epic")
public class PatientSummaryDTO {

    @Schema(description = "Epic Patient ID", example = "erXuFYUfucBZaryVksYEcMg3")
    private String patientId;

    @Schema(description = "Full name of the patient", example = "John Doe")
    private String fullName;

    @Schema(description = "First name of the patient", example = "John")
    private String firstName;

    @Schema(description = "Last name of the patient", example = "Doe")
    private String lastName;

    @Schema(description = "Gender of the patient", example = "male")
    private String gender;

    @Schema(description = "Birth date of the patient", example = "1985-10-25")
    private LocalDate birthDate;

    @Schema(description = "Age of the patient", example = "38")
    private Integer age;

    @Schema(description = "Status of the patient record", example = "true")
    private Boolean active;

    @Schema(description = "Phone number", example = "555-010-9999")
    private String phone;

    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "City", example = "Madison")
    private String city;

    @Schema(description = "State/Province", example = "WI")
    private String state;

    @Schema(description = "Country", example = "USA")
    private String country;

    @Schema(description = "Postal code", example = "53715")
    private String postalCode;

    @Schema(description = "Primary care provider name", example = "Dr. Alice Smith")
    private String primaryCareProvider;

    @Schema(description = "Organization managing the record", example = "Epic Health Services")
    private String managingOrganization;

    private String timeTaken;

    public String getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(String timeTaken) {
        this.timeTaken = timeTaken;
    }
}

