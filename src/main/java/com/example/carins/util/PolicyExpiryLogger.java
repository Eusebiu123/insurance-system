package com.example.carins.util;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.InsurancePolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PolicyExpiryLogger {
    private final InsurancePolicyRepository policyRepository;
    private final Logger logger = LoggerFactory.getLogger(PolicyExpiryLogger.class);
    public final Set<Long> loggedPolicyIds = new HashSet<>();

    public PolicyExpiryLogger(InsurancePolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    public Set<Long> getLoggedPolicyIds() {
        return loggedPolicyIds;
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void logExpiredPolicies() {
        LocalDateTime now = LocalDateTime.now();

        List<InsurancePolicy> expiredPolicies = policyRepository.findAll()
                .stream()
                .filter(p -> p.getEndDate() != null)
                .filter(p -> {
                    LocalDateTime expiryDateTime = p.getEndDate().atTime(23, 59, 59);
                    Duration duration = Duration.between(expiryDateTime, now);
                    return !loggedPolicyIds.contains(p.getId()) && !duration.isNegative() && duration.toMinutes() <= 60;
                })
                .collect(Collectors.toList());

        for (InsurancePolicy policy : expiredPolicies) {
            logger.info("Policy {} for car {} expired on {}",
                    policy.getId(), policy.getCar().getId(), policy.getEndDate());
            loggedPolicyIds.add(policy.getId());
        }

    }
}
