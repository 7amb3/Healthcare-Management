package com.hcm.patient_service.dto;

import com.hcm.patient_service.dto.validators.CreatePatientValidatorsGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientRequestDTO {
    @NotBlank
    @Size(max=100,message="Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message="Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Date of birth is required")
    private String dateOfBirth;

    @NotBlank(groups= CreatePatientValidatorsGroup.class, message="Registered date is required")
    private String registeredDate;


}
