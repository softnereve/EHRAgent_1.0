package com.softnerve.epic.utils;


import com.softnerve.epic.model.dto.ObservationBundleDTO;
import com.softnerve.epic.model.dto.ObservationSummaryDTO;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Observation;

import java.util.List;
import java.util.stream.Collectors;

public class ObservationMapper {

    public static ObservationBundleDTO toDto(Bundle bundle) {

        ObservationBundleDTO dto = new ObservationBundleDTO();
        dto.setType(bundle.getType().toCode());
        dto.setTotal(bundle.getTotal());

        List<ObservationSummaryDTO> observations =
                bundle.getEntry().stream()
                        .filter(e -> e.getResource() instanceof Observation)
                        .map(e -> mapObservation((Observation) e.getResource()))
                        .collect(Collectors.toList());

        dto.setObservations(observations);
        return dto;
    }

    private static ObservationSummaryDTO mapObservation(Observation obs) {

        ObservationSummaryDTO dto = new ObservationSummaryDTO();

        dto.setObservationId(obs.getIdElement().getIdPart());
        dto.setTestName(obs.getCode().getText());
        dto.setStatus(obs.getStatus().toCode());

        if (!obs.getCategory().isEmpty()) {
            dto.setCategory(obs.getCategory().get(0).getText());
        }

        if (obs.getSubject() != null) {
            dto.setPatientId(obs.getSubject().getReference());
        }

        if (obs.getEncounter() != null) {
            dto.setEncounterId(obs.getEncounter().getReference());
        }

        dto.setEffectiveDate(obs.getEffectiveDateTimeType().getValueAsCalendar().toInstant().atOffset(java.time.ZoneOffset.UTC));
        dto.setIssuedDate(obs.getIssued().toInstant().atOffset(java.time.ZoneOffset.UTC));

        // Value handling (Quantity OR String)
        if (obs.hasValueQuantity()) {
            dto.setValue(String.valueOf(obs.getValueQuantity().getValue()));
            dto.setUnit(obs.getValueQuantity().getUnit());
        } else if (obs.hasValueStringType()) {
            dto.setValue(obs.getValueStringType().getValue());
        }

        // Reference Range
        if (!obs.getReferenceRange().isEmpty()) {
            var range = obs.getReferenceRange().get(0);
            if (range.hasLow() && range.hasHigh()) {
                dto.setReferenceRange(
                        range.getLow().getValue() + " - " +
                                range.getHigh().getValue() + " " +
                                range.getLow().getUnit()
                );
            }
        }

        // Interpretation
        if (!obs.getInterpretation().isEmpty()) {
            dto.setInterpretation(obs.getInterpretation().get(0).getText());
        }

        return dto;
    }
}

