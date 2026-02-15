package io.project.portfolio.domain;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class Investment {

    private Asset asset;

    /** Binary planning variable: true = allocated, false = not allocated */
    @PlanningVariable(valueRangeProviderRefs = {"allocationRange"})
    private Boolean allocated;

    public Investment() {
        // Required by Timefold
    }

    public Investment(Asset asset) {
        this.asset = asset;
        this.allocated = false;
    }

    public Asset getAsset() {
        return asset;
    }

    public Boolean isAllocated() {
        return Boolean.TRUE.equals(allocated);
    }

    public void setAllocated(Boolean allocated) {
        this.allocated = allocated;
    }

    /** Fixed demo investment amount per asset */
    public double getAmount() {
        return isAllocated() ? 10_000.0 : 0.0;
    }
}
