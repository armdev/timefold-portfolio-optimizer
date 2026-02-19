---

# 🏦 Timefold Portfolio Optimizer

A **Spring Boot** application that optimizes investment portfolios using **Timefold (OptaPlanner)**.
It selects investments to **maximize expected return** while respecting constraints like **budget limits, risk, and sector diversification**.

---

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Tech Stack](#tech-stack)
4. [Domain Model](#domain-model)
5. [Getting Started](#getting-started)
6. [API](#api)
7. [Example Request & Response](#example-request--response)
8. [Frontend Demo](#frontend-demo)
9. [Development](#development)
10. [License](#license)

---

## Overview

`timefold-portfolio-optimizer` is a demo Spring Boot application demonstrating how to:

* Model a **portfolio optimization problem** with assets and investments.
* Define **hard constraints** (budget, max average risk, sector allocation).
* Define **soft constraints** (maximize expected return).
* Solve optimization using **Timefold solver**.

This is useful for **financial applications, fintech startups, or academic demos**.

---

## Features

* Hard constraints:

  * Total invested ≤ available cash
  * Weighted average risk ≤ max risk
  * Sector allocation ≤ max allowed per sector
* Soft constraint:

  * Maximize expected return
* REST API for solving portfolios
* Simple modern HTML/JS frontend demo
* JSON request/response support

---

## Tech Stack

* **Backend:** Java 25, Spring Boot 3.x
* **Solver:** [Timefold / OptaPlanner](https://www.optaplanner.org/)
* **JSON:** Jackson
* **Frontend:** Vanilla JS + HTML + Fetch API
* **Build:** Maven

---

## Domain Model

### `Asset`

Represents a financial asset.

| Field            | Type   | Description                          |
| ---------------- | ------ | ------------------------------------ |
| `id`             | String | Unique identifier (ticker)           |
| `name`           | String | Asset name                           |
| `sector`         | String | Industry/sector                      |
| `expectedReturn` | double | Expected yearly return (0–1)         |
| `risk`           | double | Risk score (0–1, standard deviation) |

---

### `Investment`

Planning entity representing investment in an asset.

| Field       | Type    | Description                                 |
| ----------- | ------- | ------------------------------------------- |
| `asset`     | Asset   | The asset being invested in                 |
| `allocated` | Boolean | Binary planning variable (`true`/`false`)   |
| `amount`    | double  | Investment amount (fixed per asset in demo) |

---

### `Portfolio`

Planning solution (entire portfolio).

| Field                 | Type             | Description                       |
| --------------------- | ---------------- | --------------------------------- |
| `cashAvailable`       | double           | Total cash available              |
| `maxAverageRisk`      | double           | Max allowed weighted average risk |
| `maxSectorAllocation` | double           | Max % of cash in one sector (0–1) |
| `investmentList`      | List<Investment> | Planning entities                 |
| `score`               | HardSoftScore    | Solver score (`hard/soft`)        |

---

## Getting Started

### Prerequisites

* Java 25+
* Maven 4+
* IDE: IntelliJ / VS Code (optional)
* Optional: Postman or browser for API testing

### Build & Run

```bash
# Clone repo
git clone https://github.com/your-org/timefold-portfolio-optimizer.git
cd timefold-portfolio-optimizer

# Build
mvn clean package

# Run
mvn spring-boot:run
```

The API will run at:

```
http://localhost:2025/api/portfolio/solve
```

---

## API

### POST `/api/portfolio/solve`

* **Description:** Solves the portfolio optimization problem.
* **Content-Type:** `application/json`
* **Request Body:**

```json
{
  "cashAvailable": 10000.0,
  "maxAverageRisk": 0.1,
  "maxSectorAllocation": 0.4,
  "investmentList": [
    { "asset": { "id": "AAPL", "name": "Apple Inc.", "sector": "Technology", "expectedReturn": 0.07, "risk": 0.12 }, "allocated": true },
    { "asset": { "id": "MSFT", "name": "Microsoft Corp.", "sector": "Technology", "expectedReturn": 0.06, "risk": 0.1 }, "allocated": false },
    { "asset": { "id": "JNJ", "name": "Johnson & Johnson", "sector": "Healthcare", "expectedReturn": 0.05, "risk": 0.08 }, "allocated": true }
  ],
  "score": "0hard/0soft",
  "allocationRange": [true, false]
}
```

* **Response Body:**

```json
{
  "cashAvailable": 10000,
  "maxAverageRisk": 0.1,
  "maxSectorAllocation": 0.4,
  "investmentList": [
    { "asset": { "id": "AAPL", "name": "Apple Inc.", "sector": "Technology", "expectedReturn": 0.07, "risk": 0.12 }, "allocated": true, "amount": 10000 },
    { "asset": { "id": "MSFT", "name": "Microsoft Corp.", "sector": "Technology", "expectedReturn": 0.06, "risk": 0.1 }, "allocated": false, "amount": 0 },
    { "asset": { "id": "JNJ", "name": "Johnson & Johnson", "sector": "Healthcare", "expectedReturn": 0.05, "risk": 0.08 }, "allocated": true, "amount": 10000 }
  ],
  "score": "0hard/1200000soft",
  "totalInvested": 20000,
  "allocationRange": [true, false],
  "averageRisk": 0.1
}
```

---

## Example Frontend

A single page HTML/JS frontend can send requests and display responses.

```html
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Portfolio Optimizer</title>
<style>
  body { font-family: Arial, sans-serif; padding: 20px; background: #f5f5f5; }
  textarea { width: 100%; height: 200px; font-family: monospace; }
  pre { background: #eee; padding: 10px; }
  button { margin-top: 10px; padding: 10px 20px; }
</style>
</head>
<body>
<h1>Portfolio Optimizer</h1>
<p>Modify the JSON below and click "Solve Portfolio".</p>
<textarea id="requestJson">
{
  "cashAvailable": 10000.0,
  "maxAverageRisk": 0.1,
  "maxSectorAllocation": 0.4,
  "investmentList": [
    { "asset": { "id": "AAPL", "name": "Apple Inc.", "sector": "Technology", "expectedReturn": 0.07, "risk": 0.12 }, "allocated": true },
    { "asset": { "id": "MSFT", "name": "Microsoft Corp.", "sector": "Technology", "expectedReturn": 0.06, "risk": 0.1 }, "allocated": false },
    { "asset": { "id": "JNJ", "name": "Johnson & Johnson", "sector": "Healthcare", "expectedReturn": 0.05, "risk": 0.08 }, "allocated": true }
  ],
  "score": "0hard/0soft",
  "allocationRange": [true, false]
}
</textarea>
<br>
<button onclick="solvePortfolio()">Solve Portfolio</button>
<h2>Response</h2>
<pre id="responseJson"></pre>

<script>
async function solvePortfolio() {
    const request = document.getElementById("requestJson").value;
    try {
        const res = await fetch("http://localhost:2025/api/portfolio/solve", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: request
        });
        const data = await res.json();
        document.getElementById("responseJson").textContent = JSON.stringify(data, null, 2);
    } catch(err) {
        document.getElementById("responseJson").textContent = "Error: " + err;
    }
}
</script>
</body>
</html>
```

---

## Development

* Use `mvn spring-boot:run` for hot reload.
* JSON request/response validation is done using Jackson.
* Timefold constraints are defined in `PortfolioConstraintProvider`.
* Adjust `Investment` `amount` or add new constraints to experiment with solver results.

---

## License

MIT License – free to use and modify.

---
[(1) Scaling Backend Web Applications: From One Server to Millions of Requests Per Second | LinkedIn](https://www.linkedin.com/pulse/scaling-backend-web-applications-from-one-server-per-second-maxim-dq6ac/)
