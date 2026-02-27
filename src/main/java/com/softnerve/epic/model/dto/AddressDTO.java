package com.softnerve.epic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Address information for the patient")
public class AddressDTO {
    @Schema(description = "Type of address usage", example = "home")
    private String use;

    @Schema(description = "Address lines", example = "[\"100 Milky Way\", \"Learning Campus\"]")
    private List<String> line;

    @Schema(description = "City", example = "Verona")
    private String city;

    @Schema(description = "State/Province", example = "WI")
    private String state;

    @Schema(description = "Postal code", example = "53593")
    private String postalCode;

    @Schema(description = "Country", example = "USA")
    private String country;
}

