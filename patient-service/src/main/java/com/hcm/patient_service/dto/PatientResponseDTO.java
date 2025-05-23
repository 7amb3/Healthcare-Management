package com.hcm.patient_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientResponseDTO {
    private String id;
    private String name;
    private String email;
    private String address;
    private String dateOfBirth;
}
