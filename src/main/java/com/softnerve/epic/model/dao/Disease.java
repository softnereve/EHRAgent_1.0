package com.softnerve.epic.model.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Disease {
    private String diseaseName;
    private String category;
    private List<String> symptoms;
}
