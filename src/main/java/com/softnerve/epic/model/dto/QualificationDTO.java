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
public class QualificationDTO {
    private List<IdentifierDTO> identifier;
    private CodeableConceptDTO code;
}
