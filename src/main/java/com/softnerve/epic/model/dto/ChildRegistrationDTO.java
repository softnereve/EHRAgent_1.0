package com.softnerve.epic.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.softnerve.epic.model.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildRegistrationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 35498498984557L;

    @JsonIgnore
    private String childId;

    @NotBlank(message = "First name must not be blank")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    private String lastName;

    @NotBlank(message = "Father's name must not be blank")
    private String fatherName;

    @NotBlank(message = "Mother's name must not be blank")
    private String motherName;

    @NotBlank(message = "Date of birth must not be blank")
    @Pattern(regexp = "^\\d{2}/\\d{2}/\\d{4}$", message = "Invalid date format (DD/MM/YYYY)")
    private String dob; // Date of Birth

    @NotBlank(message = "Date of test must not be blank")
    private String dot; // Date of Test

    @NotBlank(message = "Date of place must not be blank")
    private String dop; // Date of Place

    @NotNull(message = "Gender must not be null")
    @Field(targetType = FieldType.STRING)
    private Gender gender;

    private float weight;

    private float height;

    private String documents;

}
