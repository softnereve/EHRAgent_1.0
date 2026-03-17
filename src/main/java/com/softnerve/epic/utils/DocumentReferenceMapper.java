package com.softnerve.epic.utils;

import com.softnerve.epic.model.dto.ClinicalNoteBundleDTO;
import com.softnerve.epic.model.dto.ClinicalNoteDTO;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DocumentReference;

import java.util.List;
import java.util.stream.Collectors;

public class DocumentReferenceMapper {

    public static ClinicalNoteBundleDTO toDto(Bundle bundle) {
        ClinicalNoteBundleDTO dto = new ClinicalNoteBundleDTO();
        dto.setType(bundle.getType().toCode());
        dto.setTotal(bundle.getTotal());

        List<ClinicalNoteDTO> notes = bundle.getEntry().stream()
                .filter(e -> e.getResource() instanceof DocumentReference)
                .map(e -> mapDocRef((DocumentReference) e.getResource()))
                .collect(Collectors.toList());
        dto.setNotes(notes);
        return dto;
    }

    public static ClinicalNoteDTO mapDocRef(DocumentReference dr) {
        ClinicalNoteDTO dto = new ClinicalNoteDTO();
        dto.setId(dr.getIdElement().getIdPart());
        if (dr.hasStatus()) {
            dto.setStatus(dr.getStatus().toCode());
        }
        if (dr.hasType()) {
            if (dr.getType().hasText()) {
                dto.setType(dr.getType().getText());
            } else if (!dr.getType().getCoding().isEmpty()) {
                var c = dr.getType().getCoding().get(0);
                dto.setType(c.getDisplay() != null ? c.getDisplay() : c.getCode());
            }
        }
        if (!dr.getCategory().isEmpty() && dr.getCategoryFirstRep() != null) {
            var cat = dr.getCategoryFirstRep();
            dto.setCategory(cat.getText());
        }
        if (dr.hasSubject() && dr.getSubject().hasReference()) {
            dto.setPatientReference(dr.getSubject().getReference());
        }
        if (dr.hasDate()) {
            dto.setDate(dr.getDateElement().asStringValue());
        }
        if (!dr.getAuthor().isEmpty()) {
            dto.setAuthor(dr.getAuthorFirstRep().getDisplay());
        }
        if (dr.hasCustodian()) {
            dto.setCustodian(dr.getCustodian().getDisplay());
        }
        if (dr.hasDescription()) {
            dto.setDescription(dr.getDescription());
        }
        if (!dr.getContent().isEmpty()) {
            var content = dr.getContentFirstRep();
            if (content.hasAttachment()) {
                dto.setContentType(content.getAttachment().getContentType());
                dto.setContentUrl(content.getAttachment().getUrl());
            }
        }
        return dto;
    }
}
