package io.project.portfolio.domain;

import ai.timefold.solver.core.api.domain.solution.*;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;

import java.util.List;

@PlanningSolution
@JsonIgnoreProperties(ignoreUnknown = true)
public class Portfolio {

    private double cashAvailable;
    private double maxAverageRisk;      // e.g. 0.12 = 12%
    private double maxSectorAllocation; // e.g. 0.4 = 40%

    @PlanningEntityCollectionProperty
    private List<Investment> investmentList;

    @PlanningScore
    private HardSoftScore score;

    public Portfolio() {
    }

    public Portfolio(double cashAvailable,
            double maxAverageRisk,
            double maxSectorAllocation,
            List<Investment> investmentList) {
        this.cashAvailable = cashAvailable;
        this.maxAverageRisk = maxAverageRisk;
        this.maxSectorAllocation = maxSectorAllocation;
        this.investmentList = investmentList;
    }

    // ---- Getters ----
    public double getCashAvailable() {
        return cashAvailable;
    }

    public double getMaxAverageRisk() {
        return maxAverageRisk;
    }

    public double getMaxSectorAllocation() {
        return maxSectorAllocation;
    }

    public List<Investment> getInvestmentList() {
        return investmentList;
    }

    public HardSoftScore getScore() {
        return score;
    }

    // ---- Allocation range for binary variable ----
    @ValueRangeProvider(id = "allocationRange")
    @ProblemFactCollectionProperty
    public List<Boolean> getAllocationRange() {
        return new ArrayList<>(List.of(Boolean.TRUE, Boolean.FALSE));
    }

    // ---- Helper methods ----
    public double getTotalInvested() {
        return investmentList.stream()
                .filter(Investment::isAllocated)
                .mapToDouble(Investment::getAmount)
                .sum();
    }

    public double getAverageRisk() {
        double total = getTotalInvested();
        if (total == 0) {
            return 0;
        }
        double riskSum = investmentList.stream()
                .filter(Investment::isAllocated)
                .mapToDouble(i -> i.getAmount() * i.getAsset().risk())
                .sum();
        return riskSum / total;
    }
}
