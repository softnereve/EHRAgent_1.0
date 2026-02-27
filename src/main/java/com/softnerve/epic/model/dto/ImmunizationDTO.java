package com.softnerve.epic.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.softnerve.epic.model.dao.ImmunizationChart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImmunizationDTO {

    @JsonIgnore
    private String immunizationId;

    private String immunizationName;

    private Integer startBirthMonth;

    private Integer endBirthMonth;

    private Integer priorityForSeq;

    private Integer durationAfterInDays;

    private Double immunizationCost;

    private String companyName;

    private ImmunizationChart.ImmunizationType immunizationType;

}
