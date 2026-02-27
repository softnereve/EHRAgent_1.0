package com.softnerve.epic.utils;



import com.softnerve.epic.model.dto.PatientSummaryDTO;
import org.hl7.fhir.r4.model.Patient;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneOffset;

public class PatientMapper {

    public static PatientSummaryDTO toDto(Patient patient) {

        PatientSummaryDTO dto = new PatientSummaryDTO();

        dto.setPatientId(patient.getIdElement().getIdPart());
        dto.setActive(patient.getActive());

        // Name
        if (!patient.getName().isEmpty()) {
            var name = patient.getName().get(0);
            dto.setFullName(name.getText());
            dto.setFirstName(
                    name.getGiven().isEmpty() ? null : name.getGiven().get(0).getValue()
            );
            dto.setLastName(name.getFamily());
        }

        // Gender
        if (patient.hasGender()) {
            dto.setGender(patient.getGender().toCode());
        }

        // Birth date + Age
        if (patient.hasBirthDate()) {
            LocalDate dob = patient.getBirthDate()
                    .toInstant()
                    .atOffset(ZoneOffset.UTC)
                    .toLocalDate();

            dto.setBirthDate(dob);
            dto.setAge(Period.between(dob, LocalDate.now()).getYears());
        }

        // Telecom
        patient.getTelecom().forEach(cp -> {
            if ("phone".equals(cp.getSystem().toCode()) && dto.getPhone() == null) {
                dto.setPhone(cp.getValue());
            }
            if ("email".equals(cp.getSystem().toCode()) && dto.getEmail() == null) {
                dto.setEmail(cp.getValue());
            }
        });

        // Address
        if (!patient.getAddress().isEmpty()) {
            var addr = patient.getAddress().get(0);
            dto.setCity(addr.getCity());
            dto.setState(addr.getState());
            dto.setCountry(addr.getCountry());
            dto.setPostalCode(addr.getPostalCode());
        }

        // Primary Care Provider
        if (!patient.getGeneralPractitioner().isEmpty()) {
            dto.setPrimaryCareProvider(
                    patient.getGeneralPractitioner().get(0).getDisplay()
            );
        }

        // Managing Organization
        if (patient.hasManagingOrganization()) {
            dto.setManagingOrganization(
                    patient.getManagingOrganization().getDisplay()
            );
        }

        return dto;
    }
}

