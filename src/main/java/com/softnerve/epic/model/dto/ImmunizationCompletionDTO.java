package com.softnerve.epic.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImmunizationCompletionDTO {
    private String childId;
    private String immunizationId;
    private Long timeStamp;
    private String doctorId;
    private String clinicId;
}
