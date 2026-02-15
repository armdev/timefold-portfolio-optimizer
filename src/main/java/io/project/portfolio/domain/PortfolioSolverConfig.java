/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.project.portfolio.domain;


import ai.timefold.solver.core.api.solver.*;
import ai.timefold.solver.core.config.solver.SolverConfig;
import ai.timefold.solver.core.config.solver.termination.TerminationConfig;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;

@Configuration
public class PortfolioSolverConfig {

    /**
     * Build the {@link SolverFactory} that Timefold will use.
     * The factory is a Spring bean and will be injected wherever needed.
     * @return 
     */
    @Bean
    public SolverFactory<Portfolio> solverFactory() {
        SolverConfig solverConfig = new SolverConfig()
                .withSolutionClass(Portfolio.class)
                .withEntityClasses(Investment.class)
                .withConstraintProviderClass(PortfolioConstraintProvider.class)
                // ---- General solver settings -------------------------------------------------
                .withTerminationConfig(
                        new TerminationConfig()
                                .withBestScoreLimit("0hard/*soft") // stop as soon as a feasible solution is found
                                .withSecondsSpentLimit(30L));      // or after 30 s, whichever comes first
        return SolverFactory.create(solverConfig);
    }

    /**
     * The actual {@link Solver} bean – one per request (prototype scope) because the solver
     * mutates the solution.
     * @param factory
     * @return 
     */
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Solver<Portfolio> solver(SolverFactory<Portfolio> factory) {
        return factory.buildSolver();
    }
}