package com.example.carins.util;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class InsuranceEndDateFixer implements CommandLineRunner {
    private final InsurancePolicyRepository insurancePolicyRepository;

    public InsuranceEndDateFixer(InsurancePolicyRepository insurancePolicyRepository) {
        this.insurancePolicyRepository = insurancePolicyRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<InsurancePolicy> policies = insurancePolicyRepository.findByEndDateIsNull();
        for(InsurancePolicy policy : policies)
        {
            LocalDate endDate = policy.getStartDate().plusYears(1);
            policy.setEndDate(endDate);
        }
        insurancePolicyRepository.saveAll(policies);
    }
}
