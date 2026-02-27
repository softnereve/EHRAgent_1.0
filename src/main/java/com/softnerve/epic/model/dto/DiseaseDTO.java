package com.softnerve.epic.model.dto;

import com.softnerve.epic.model.dao.Disease;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiseaseDTO {

    private String diseaseName;
    private String category;
    private List<String> symptoms;

    public static DiseaseDTO convertToDTO(Disease disease) {
        return DiseaseDTO.builder()
                .diseaseName(disease.getDiseaseName())
                .category(disease.getCategory())
                .symptoms(disease.getSymptoms())
                .build();
    }

    public static Disease convertToEntity(DiseaseDTO diseaseDTO) {
        return Disease.builder()
                .diseaseName(diseaseDTO.getDiseaseName())
                .category(diseaseDTO.getCategory())
                .symptoms(diseaseDTO.getSymptoms())
                .build();
    }
}
