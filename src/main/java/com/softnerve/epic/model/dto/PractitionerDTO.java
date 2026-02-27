package com.softnerve.epic.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PractitionerDTO {
    private String resourceType;
    private String id;
    private Boolean active;
    private List<IdentifierDTO> identifier;
    private List<NameDTO> name;
    private List<TelecomDTO> telecom;
    private String gender;
    private String birthDate;
    private List<AttachmentDTO> photo;
    private List<QualificationDTO> qualification;
    private List<CodeableConceptDTO> communication;
}
