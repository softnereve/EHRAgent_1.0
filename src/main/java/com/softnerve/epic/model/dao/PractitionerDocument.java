package com.softnerve.epic.model.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "epic-practitioners")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PractitionerDocument {

    @Id
    private String id; // Internal doctorId (DOCxxxxxxxxxx)

    private String epicPractitionerId;
    private String firstName;
    private String lastName;
    private String gender;
    private String birthDate;
    private List<String> specialties;
    private List<String> telecom;
    
    private Instant createdAt;
    @Builder.Default
    private boolean isFromEpic = true;
}
