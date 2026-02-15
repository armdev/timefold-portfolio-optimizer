/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.project.portfolio.domain;

import ai.timefold.solver.core.api.solver.Solver;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final Solver<Portfolio> solver;

    public PortfolioController(Solver<Portfolio> solver) {
        this.solver = solver;
    }

    /**
     * POST a JSON payload describing the portfolio problem. The solver runs
     * synchronously (blocking) and returns the solved portfolio.
     *
     * Example payload can be found in
     * {@code src/main/resources/data/sample-portfolio.json}.
     * @param unsolvedPortfolio
     * @return 
     */
    @PostMapping("/solve")
    public ResponseEntity<Portfolio> solve(@RequestBody Portfolio unsolvedPortfolio) {
        // Defensive copy – Timefold mutates the supplied instance.    
        Portfolio solved = solver.solve(unsolvedPortfolio);
        return ResponseEntity.ok(solved);
    }
}
