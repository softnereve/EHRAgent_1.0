package com.softnerve.epic.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientDto {
    @Indexed(unique = true)
    private String clientId;
    private String clientEmail;
    @NotBlank
    private String clientName;
}
