package com.example.carins.repo;

import com.example.carins.model.Car;
import com.example.carins.model.InsuranceClaim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface InsuranceClaimRepository extends JpaRepository<InsuranceClaim,Long> {
    Collection<InsuranceClaim> findByCarOrderByClaimDateAsc(Car car);
}
