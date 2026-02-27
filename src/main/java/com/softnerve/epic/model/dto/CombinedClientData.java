package com.softnerve.epic.model.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CombinedClientData {
    private SecureDto securityData;
    private UiData uiData;
}
