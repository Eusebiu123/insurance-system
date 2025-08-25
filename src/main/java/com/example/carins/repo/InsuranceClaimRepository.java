package com.example.carins.repo;

import com.example.carins.model.InsuranceClaim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsuranceClaimRepository extends JpaRepository<InsuranceClaim,Long> {
}
