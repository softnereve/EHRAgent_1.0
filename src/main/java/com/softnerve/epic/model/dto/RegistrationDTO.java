package com.softnerve.epic.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.softnerve.epic.model.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.common.value.qual.StringVal;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {
    @JsonIgnore
    private String patientId;
    @NotBlank
    private String patientUserName;
    @NotBlank
    private String patientFirstName;
    @NotBlank
    private String patientLastName;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String countryCode;
    @NotBlank
    @Size(max = 12, min = 10)
    private String phoneNumber;
    @Field(targetType = FieldType.STRING)
    private Gender gender;
    private String dob;
}
