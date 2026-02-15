package io.project.portfolio.domain;

import java.util.Objects;

public record Asset(
        String id,
        String name,
        String sector,
        double expectedReturn,
        double risk
) {
    public Asset {
        Objects.requireNonNull(id);
        Objects.requireNonNull(name);
        Objects.requireNonNull(sector);
        if (expectedReturn < 0 || risk < 0) {
            throw new IllegalArgumentException("Return and risk must be non-negative");
        }
    }
}
