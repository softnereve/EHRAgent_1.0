package com.softnerve.epic.model.dto;

import com.softnerve.epic.model.dao.ImmunizationChart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicableImmunizationDTO {
    private String immunizationId;
    private String immunizationName;
    private double immunizationCost;
    private String companyName;
    private ImmunizationChart.ImmunizationType immunizationType;

}
