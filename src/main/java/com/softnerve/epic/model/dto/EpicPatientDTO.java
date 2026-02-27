package com.softnerve.epic.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EpicPatientDTO {

    // Always "Patient"
    @NotBlank
    private String resourceType;

    private List<IdentifierDTO> identifier;

    private List<NameDTO> name;

    private List<TelecomDTO> telecom;

    /**
     * FHIR gender
     * male | female | other | unknown
     */
    @Pattern(
            regexp = "male|female|other|unknown",
            message = "Gender must be male, female, other, or unknown"
    )
    private String gender;

    /**
     * FHIR birthDate (YYYY-MM-DD)
     */
    @Pattern(
            regexp = "\\d{4}-\\d{2}-\\d{2}",
            message = "birthDate must be YYYY-MM-DD"
    )
    private String birthDate;

    private List<AddressDTO> address;

    private MaritalStatusDTO maritalStatus;
}
