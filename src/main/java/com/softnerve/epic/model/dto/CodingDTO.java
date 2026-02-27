package com.softnerve.epic.model.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodingDTO {
    private String system;
    private String code;
    private String display;
}
