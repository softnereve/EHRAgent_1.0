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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDTO {

    private String patientFirstName;

    private String patientLastName;

    @Email
    private String email;

    private String password;

    @Size(max = 12, min = 10)
    private String phoneNumber;

    @NotBlank
    private String firstLane;

    private String secondLane;

    private String city;

    @Size(max = 8, min = 5)
    private Integer pinCode;

    @NotNull
    private Integer age;

    @Field(targetType = FieldType.STRING)
    private Gender gender;

}
