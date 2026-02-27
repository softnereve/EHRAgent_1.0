package com.softnerve.epic.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiseaseApiResponse {
    private int statusCode;
    private String message;
    private DiseaseDTO data;

}
