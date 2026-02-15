/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.project.portfolio.domain;

public class PortfolioHolder {

    private static Portfolio portfolio;

    public static void setPortfolio(Portfolio p) {
        portfolio = p;
    }

    public static double getCashAvailable() {
        return portfolio.getCashAvailable();
    }

    public static double getMaxAverageRisk() {
        return portfolio.getMaxAverageRisk();
    }

    public static int getMaxSectorLimit() {
        return (int) Math.round(portfolio.getCashAvailable() * portfolio.getMaxSectorAllocation());
    }
}
