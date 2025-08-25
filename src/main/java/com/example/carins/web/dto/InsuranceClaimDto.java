package com.example.carins.web.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InsuranceClaimDto {
    public LocalDate getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(LocalDate claimDate) {
        this.claimDate = claimDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @NotNull(message = "claimDate is required!")
    private LocalDate claimDate;

    @NotNull(message = "description is required!")
    private String description;

    @NotNull(message = "amount is required!")
    private BigDecimal amount;
}
