package com.softnerve.epic.model.dao;

import com.softnerve.epic.model.dto.AddressDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "epic-patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDocument {

    @Id
    private String id; // Mongo _id

    private String epicPatientId;

    private String firstName;
    private String lastName;
    private String gender;
    private LocalDate dob;
    private Boolean isVerified;
    // 🔐 NEW (hashed password only)
    private String passwordHash;
    private String countryCode;
    private String phone;
    private String email;
    private List<AddressDTO> addressDTO;
    private String registrationToken;

    private Instant createdAt;


}
