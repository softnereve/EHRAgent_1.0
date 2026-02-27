package com.softnerve.epic.utils;

import com.softnerve.epic.model.dto.*;

import java.util.List;
import java.util.UUID;

public class EpicPatientRequestMapper {

    public static EpicPatientDTO toEpicPatient(CreatePatientRequest req) {

        return EpicPatientDTO.builder()
                .resourceType("Patient")

                // 🔒 Hardcoded identifiers (Epic controlled)
                .identifier(List.of(
                        IdentifierDTO.builder()
                                .use("usual")
                                .system("urn:oid:2.16.840.1.113883.4.1")
                                .value("999-99-9999")
                                .build(),
                        IdentifierDTO.builder()
                                .use("usual")
                                .system("urn:oid:1.2.840.114350.1.13.861.1.7.5.737384.27000")
                                .value(UUID.randomUUID().toString())
                                .build()
                ))

                // ✅ Name from frontend
                .name(List.of(
                        NameDTO.builder()
                                .use("official")
                                .family(req.getLastName())
                                .given(List.of(req.getFirstName()))
                                .build()
                ))

                // ✅ Contact
                .telecom(List.of(
                        TelecomDTO.builder()
                                .system("phone")
                                .value(req.getPhone())
                                .use("home")
                                .build(),
                        TelecomDTO.builder()
                                .system("email")
                                .value(req.getEmail())
                                .build()
                ))

                .gender(req.getGender())
                .birthDate(req.getBirthDate())

                // 🔒 Address: hardcoded OR partial from frontend
                .address(
                        req.getAddress() != null && !req.getAddress().isEmpty()
                                ? req.getAddress()
                                .stream()
                                .map(a -> AddressDTO.builder()
                                        .use(a.getUse() != null ? a.getUse() : "home")
                                        .line(a.getLine())
                                        .city(a.getCity())
                                        .state(a.getState())
                                        .postalCode(a.getPostalCode())
                                        .country(a.getCountry())
                                        .build()
                                )
                                .toList()
                                : List.of(
                                AddressDTO.builder()
                                        .use("home")
                                        .line(List.of("100 Default Street"))
                                        .city("Verona")
                                        .state("WI")
                                        .postalCode("53593")
                                        .country("USA")
                                        .build()
                        )
                )


                // 🔒 Hardcoded marital status
                .maritalStatus(
                        MaritalStatusDTO.builder()
                                .coding(List.of(
                                        CodingDTO.builder()
                                                .system("http://terminology.hl7.org/CodeSystem/v3-MaritalStatus")
                                                .code("S")
                                                .build()
                                ))
                                .text("Single")
                                .build()
                )

                .build();
    }
}
