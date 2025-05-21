package com.hcm.patient_service.service;

import com.hcm.patient_service.dto.PatientRequestDTO;
import com.hcm.patient_service.dto.PatientResponseDTO;
import com.hcm.patient_service.exception.EmailAlreadyExistsException;
import com.hcm.patient_service.exception.PatientNotFoundException;
import com.hcm.patient_service.grpc.BillingServiceGrpcClient;
import com.hcm.patient_service.kafka.KafkaProducer;
import com.hcm.patient_service.mapper.PatientMapper;
import com.hcm.patient_service.model.Patient;
import com.hcm.patient_service.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository,
                          BillingServiceGrpcClient billingServiceGrpcClient, KafkaProducer kafkaProducer){
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients = this.patientRepository.findAll();
        return patients.stream()
                .map(PatientMapper::toDTO).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){
        this.isEmailExist(patientRequestDTO.getEmail());
        Patient patient = this.patientRepository.save(PatientMapper.toModel(patientRequestDTO));

        billingServiceGrpcClient.createBillingAccount(patient.getId().toString(),patient.getName(),patient.getEmail());

        kafkaProducer.sendEvent(patient);

        return PatientMapper.toDTO(patient);
    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO){
        Patient patient = this.patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient not found with ID: "+id));
        /*if(this.patientRepository.existsByEmail(patientRequestDTO.getEmail())){
            throw new EmailAlreadyExistsException("A patient with this email already exist "+patientRequestDTO.getEmail());
        }*/

        if(this.patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(),id)){
            throw new EmailAlreadyExistsException("A patient with this email already exist "+patientRequestDTO.getEmail());
        }

        patient.setName(patientRequestDTO.getName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient =  this.patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id){
        this.patientRepository.deleteById(id);
    }

    private void isEmailExist(String email){
        if(this.patientRepository.existsByEmail(email)){
            throw new EmailAlreadyExistsException("A patient with this email already exist "+email);
        }
    }


}

