package com.example.carins.controller;

import com.example.carins.model.Car;
import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.web.dto.InsurancePolicyDto;
import com.example.carins.web.dto.InsurancePolicyResponseDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/insurance-policies")
public class InsurancePolicyController {

    @Autowired
    private InsurancePolicyRepository insurancePolicyRepository;
    @Autowired
    private CarRepository carRepository;


    @PostMapping("/add")
    public ResponseEntity<?> createInsurancePolicy(@Valid @RequestBody InsurancePolicyDto dto) {
        Optional<Car> optionalCar = carRepository.findById(dto.getCar_id());
        if (optionalCar.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Car with id " + dto.getCar_id() + " not found!");
        }
        Car car = optionalCar.get();

        InsurancePolicy insurancePolicy = new InsurancePolicy();
        insurancePolicy.setCar(car);
        insurancePolicy.setProvider(dto.getProvider());
        insurancePolicy.setStartDate(dto.getStartDate());
        insurancePolicy.setEndDate(dto.getEndDate());
        insurancePolicy.setLogged(false);

        InsurancePolicy savedPolicy = insurancePolicyRepository.save(insurancePolicy);

        InsurancePolicyResponseDto responseDto = new InsurancePolicyResponseDto(
                savedPolicy.getId(),
                savedPolicy.getCar().getId(),
                savedPolicy.getProvider(),
                savedPolicy.getStartDate(),
                savedPolicy.getEndDate(),
                savedPolicy.getLogged()
        );

        return ResponseEntity.ok(responseDto);
    }
}
