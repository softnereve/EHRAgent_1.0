package com.softnerve.epic.model.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.softnerve.epic.model.enums.Gender;
import com.softnerve.epic.model.upload_interface.DocumentEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Builder
@Document(collection = "patients")
public class Patient implements DocumentEntity {
    @Id
    @Field("_id")
    private ObjectId id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Indexed(unique = true)
    private String patientId;
    private String epicPatientId;
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
    private String dob;

    @NotBlank
    private String firstLane;
    private String secondLane;
    @NotBlank
    private String city;
    @Size(max = 8, min = 5)
    private Integer pinCode;
    @NotNull
    private Integer age;
    @NotBlank
    @Field(targetType = FieldType.STRING)
    private Gender gender;
    private String registrationToken;
    private boolean isVerified;
    private String otp;
    private List<Child> children = new ArrayList<>();
    private List<Disease> diseases;


    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;

    @Override
    public String getId() {
        return patientId;
    }

    @Override
    public void setId(String id) {
        this.patientId = id;
    }

}
