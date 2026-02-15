package io.project.portfolio.domain;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.*;

public class PortfolioConstraintProvider implements ConstraintProvider {

    private static final int SCALE = 1000; // scale to convert double -> int

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[]{
                budgetConstraint(factory),
                averageRiskConstraint(factory),
                sectorDiversificationConstraint(factory),
                maximiseReturn(factory)
        };
    }

    // 1️⃣ Total invested ≤ cashAvailable
    private Constraint budgetConstraint(ConstraintFactory factory) {
        return factory.forEach(Investment.class)
                .filter(Investment::isAllocated)
                .groupBy(ConstraintCollectors.sum(i -> (int) Math.round(i.getAmount() * SCALE)))
                .filter(totalInvested -> totalInvested > 100_000 * SCALE) // demo limit
                .penalize("Budget exceeded", HardSoftScore.ONE_HARD,
                        totalInvested -> totalInvested - 100_000 * SCALE);
    }

    // 2️⃣ Weighted average risk ≤ maxAverageRisk
    private Constraint averageRiskConstraint(ConstraintFactory factory) {
        return factory.forEach(Investment.class)
                .filter(Investment::isAllocated)
                .groupBy(
                        ConstraintCollectors.sum(i -> (int) Math.round(i.getAmount() * SCALE)),
                        ConstraintCollectors.sum(i -> (int) Math.round(i.getAmount() * i.getAsset().risk() * SCALE))
                )
                .filter((total, weightedRisk) -> total > 0 && weightedRisk > total * 0.12)
                .penalize("Average risk exceeded", HardSoftScore.ONE_HARD,
                        (total, weightedRisk) -> weightedRisk - (int) Math.round(total * 0.12));
    }

    // 3️⃣ Sector allocation ≤ maxSectorAllocation
    private Constraint sectorDiversificationConstraint(ConstraintFactory factory) {
        return factory.forEach(Investment.class)
                .filter(Investment::isAllocated)
                .groupBy(
                        i -> i.getAsset().sector(),
                        ConstraintCollectors.sum(i -> (int) Math.round(i.getAmount() * SCALE))
                )
                .filter((sector, totalInvested) -> totalInvested > 40_000 * SCALE)
                .penalize("Sector allocation exceeded", HardSoftScore.ONE_HARD,
                        (sector, totalInvested) -> totalInvested - 40_000 * SCALE);
    }

    // 4️⃣ Maximise expected return
    private Constraint maximiseReturn(ConstraintFactory factory) {
        return factory.forEach(Investment.class)
                .filter(Investment::isAllocated)
                .groupBy(
                        ConstraintCollectors.sum(i -> (int) Math.round(i.getAmount() * i.getAsset().expectedReturn() * SCALE))
                )
                .reward("Maximise return", HardSoftScore.ONE_SOFT,
                        totalReturn -> totalReturn);
    }
}
