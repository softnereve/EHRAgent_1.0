package com.softnerve.epic.utils;

import com.softnerve.epic.model.dto.ClinicalNoteBundleDTO;
import com.softnerve.epic.model.dto.ClinicalNoteDTO;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DocumentReference;
import org.hl7.fhir.r4.model.Reference;

import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

public class ClinicalNoteMapper {

    public static ClinicalNoteBundleDTO toDto(Bundle bundle) {
        ClinicalNoteBundleDTO dto = new ClinicalNoteBundleDTO();
        dto.setType(bundle.getType().toCode());
        dto.setTotal(bundle.getTotal());

        List<ClinicalNoteDTO> notes = bundle.getEntry().stream()
                .filter(e -> e.getResource() instanceof DocumentReference)
                .map(e -> mapDocumentReference((DocumentReference) e.getResource()))
                .collect(Collectors.toList());

        dto.setNotes(notes);
        return dto;
    }

    private static ClinicalNoteDTO mapDocumentReference(DocumentReference doc) {
        ClinicalNoteDTO dto = new ClinicalNoteDTO();
        dto.setId(doc.getIdElement().getIdPart());
        dto.setStatus(doc.getStatus() != null ? doc.getStatus().toCode() : null);

        if (doc.hasType() && doc.getType().hasCoding() && !doc.getType().getCoding().isEmpty()) {
            String display = doc.getType().getCodingFirstRep().getDisplay();
            dto.setType(display != null && !display.isBlank() ? display : doc.getType().getCodingFirstRep().getCode());
        } else if (doc.hasType() && doc.getType().hasText()) {
            dto.setType(doc.getType().getText());
        }

        if (!doc.getCategory().isEmpty()) {
            var cat = doc.getCategoryFirstRep();
            if (cat.hasCoding() && !cat.getCoding().isEmpty()) {
                String display = cat.getCodingFirstRep().getDisplay();
                dto.setCategory(display != null && !display.isBlank() ? display : cat.getCodingFirstRep().getCode());
            } else if (cat.hasText()) {
                dto.setCategory(cat.getText());
            }
        }

        if (doc.hasSubject()) {
            dto.setPatientReference(doc.getSubject().getReference());
        }

        if (doc.hasDate()) {
            dto.setDate(doc.getDate().toInstant().atOffset(ZoneOffset.UTC).toString());
        }

        if (!doc.getAuthor().isEmpty()) {
            Reference authorRef = doc.getAuthorFirstRep();
            dto.setAuthor(authorRef.getDisplay() != null ? authorRef.getDisplay() : authorRef.getReference());
        }

        if (doc.hasCustodian()) {
            dto.setCustodian(doc.getCustodian().getDisplay() != null ? doc.getCustodian().getDisplay() : doc.getCustodian().getReference());
        }

        if (doc.hasDescription()) {
            dto.setDescription(doc.getDescription());
        }

        if (!doc.getContent().isEmpty() && doc.getContentFirstRep().hasAttachment()) {
            var att = doc.getContentFirstRep().getAttachment();
            dto.setContentType(att.getContentType());
            dto.setContentUrl(att.getUrl());
        }

        return dto;
    }
}
