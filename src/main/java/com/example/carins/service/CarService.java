package com.example.carins.service;

import com.example.carins.exceptions.ResourceNotFoundException;
import com.example.carins.model.Car;
import com.example.carins.model.InsuranceClaim;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsuranceClaimRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.web.dto.ClaimResponseDto;
import com.example.carins.web.dto.InsuranceClaimDto;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarService {

    private final CarRepository carRepository;
    private final InsurancePolicyRepository policyRepository;
    private final InsuranceClaimRepository insuranceClaimRepository;
    private final InsurancePolicyRepository insurancePolicyRepository;

    public CarService(CarRepository carRepository, InsurancePolicyRepository policyRepository, InsuranceClaimRepository insuranceClaimRepository, InsurancePolicyRepository insurancePolicyRepository) {
        this.carRepository = carRepository;
        this.policyRepository = policyRepository;
        this.insuranceClaimRepository = insuranceClaimRepository;
        this.insurancePolicyRepository = insurancePolicyRepository;
    }

    public List<Car> listCars() {
        return carRepository.findAll();
    }

    public boolean isInsuranceValid(Long carId, LocalDate date) {
        if (carId == null || date == null) return false;
        // TODO: optionally throw NotFound if car does not exist
        return policyRepository.existsActiveOnDate(carId, date);
    }

    public InsuranceClaim registerInsuranceClaim(Long carId, @Valid InsuranceClaimDto request) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        InsuranceClaim claim = new InsuranceClaim(
                car,
                request.getClaimDate(),
                request.getDescription(),
                request.getAmount()
        );
        InsuranceClaim saved = insuranceClaimRepository.save(claim);
        return saved;
    }

    public List<ClaimResponseDto> getHistoryOfCar(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));
        List<ClaimResponseDto> historyList = insuranceClaimRepository.findByCarOrderByClaimDateAsc(car)
                .stream()
                .map(c -> new ClaimResponseDto(c.getId(), c.getClaimDate(), c.getDescription(), c.getAmount()))
                .collect(Collectors.toList());

        return historyList;
    }

    public Boolean checkInsuranceValid(Long carId, String date) throws BadRequestException {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found"));
        LocalDate queryDate;
        try {
            queryDate = LocalDate.parse(date); // ISO_LOCAL_DATE (YYYY-MM-DD)
        } catch (Exception e) {
            throw new BadRequestException("Invalid date format. Use YYYY-MM-DD");
        }

        if (queryDate.getYear() < 2000 || queryDate.getYear() > 2100) {
            throw new BadRequestException("Date year out of supported range (2000-2100)");
        }
        boolean isValid = insurancePolicyRepository.existsActiveOnDate(carId, queryDate);
        return isValid;
    }
}
