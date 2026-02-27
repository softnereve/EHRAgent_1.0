package com.softnerve.epic.model.dao;

import com.softnerve.epic.model.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Child {

    private String childId;

    private String firstName;

    private String lastName;

    private String fatherName;

    private String motherName;

    private String dob; // Date of Birth

    private String dot; // Date of time

    private String dop; // Date of Place

    private Gender gender;

    private float weight;

    private float height;

    private String documents;

}
