package com.softnerve.epic.model.dto;

import com.softnerve.epic.model.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FetchPatientDTO {
    private String patientId;
    private String patientUserName;
    private String patientFirstName;
    private String patientLastName;
    private String email;
    private String countryCode;
    private String phoneNumber;
    private String firstLane;
    private String secondLane;
    private String city;
    private Integer pinCode;
    private Integer age;
    @Field(targetType = FieldType.STRING)
    private Gender gender;
}
