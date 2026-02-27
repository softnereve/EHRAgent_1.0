package com.softnerve.epic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for creating a new patient in Epic")
public class CreatePatientRequest {

    @NotBlank
    @Schema(description = "Patient's first name", example = "Aniket")
    private String firstName;

    @NotBlank
    @Schema(description = "Patient's last name", example = "Gosavi")
    private String lastName;

    @NotBlank
    @Schema(description = "Patient's phone number", example = "08007991299")
    private String phone;

    @Email
    @Schema(description = "Patient's email address", example = "knixontestemail@epic.com")
    private String email;

    @Pattern(regexp = "male|female|other|unknown")
    @Schema(description = "Patient's gender", example = "male", allowableValues = {"male", "female", "other", "unknown"})
    private String gender;

    // YYYY-MM-DD
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
    @Schema(description = "Patient's birth date (YYYY-MM-DD)", example = "1994-07-20")
    private String birthDate;

    // 🔐 NEW
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=]).{8,}$",
            message = "Password must be strong"
    )
    @Schema(description = "Patient's account password", example = "Strong@123G")
    private String password;

//    private AddressDTO address;
    @Schema(description = "Patient's address list")
    private List<AddressDTO> address;
}

