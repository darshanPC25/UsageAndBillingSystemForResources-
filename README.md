# Usage and Billing System for a Resource

This project provides a robust REST API built in Spring Boot for managing billable resources and calculating dynamic usage costs. The system allows you to create resources, attach varying pricing services to them, start and stop usage sessions for users, and automatically generate accurate invoices based on the time elapsed.

## Detailed Explanations of Data Structure

The domain logic is driven by four primary entities:

1. **`Resource`**
   - Represents a physical or digital asset that can be utilized (e.g., a gaming PC, rental equipment, room booking).
   - Defines a `capacity` (how many concurrent usages are permitted on this unit).
   - Holds a curated list of attached `Services` pricing tiers, and a managed list of currently `activeSession` tracking lists for boundary enforcement.

2. **`Services`**
   - Dictates the billing structures for a particular `Resource`.
   - Distinctly tracks a `firstHoursPrice` (base fee for entering into usage) and an `additionalHoursPrice` (the recurring cost for all subsequent hours).

3. **`UsageSession`**
   - Represents the active consumption of a `Resource` by an actor (`userId`), mapped to a particular pricing tier (`serviceId`).
   - Acts as a start/stop watch by capturing `startTime` alongside `endTime` to calculate the eventual duration upon finalizing the session.

4. **`Bill`**
   - Generated entirely imutably when a `UsageSession` officially ends.
   - Unifies historical context capturing actual duration, equivalent billable hours calculated by the server, pricing details referenced, and the finalized invoice `total`.

## Overview of Logic and Approaches

### 1. Controllers (API Lifecycle Entry-points)
We structured the REST API controllers to follow strict entity-resource definitions:
- **`ResourceController` (`/api/resources`)**: Endpoints to manage the system's stock and assign various pricing services to an item.
- **`UsageController` (`/api/sessions`)**: Core endpoint for controlling lifetimes. Endpoints inside this controller strictly enforce capacity limits on a resource and ensure individual user sessions aren't duplicated.
- **`BillController` (`/api/bills`)**: A read-oriented controller ensuring transparency for pulling calculated bills.

### 2. Service Layer (`BillingService.java`)
This singleton component encapsulates the entire suite of business logic checking validations ensuring:
- Capacity boundary exceptions are actively thrown instead of silently failing.
- Date tracking logic calculates total seconds precisely and normalizes it to "hours" using ceiling strategies properly distributing over time.
- Handles transitions smoothly, destroying active sessions off resource objects dynamically before calculating the total required invoice inside `stopUsage()`.

### 3. Emulated Repository Interfaces
Rather than strictly binding to a traditional RDBMS implementation like MySQL right away, we structured custom repositories inside the `repository/` package containing simple mapping collections (e.g., `LinkedHashMap`). They emulate traditional database paradigms allowing lightweight caching during execution without heavy overhead.

## Important Technical Assumptions or Details

- **In-Memory Volatility:** Because Custom maps run entirely inside Heap space, you must start fresh whenever the Spring server application restarts. To adapt for production, implementations referencing standard `JPA Repository` wrappers utilizing `H2` or `Postgres` dependencies are highly recommended as next-steps.
- **Time/Date Computations Calculation Limits:** For billing algorithms:
  - If a player uses a service for `30 seconds`, it dynamically assumes `1 billable hour`.
  - An elapsed time of `61 minutes` is subsequently treated as `2 billable hours`.
  - Calculations lean purely on standard `getFirstHoursPrice()` and map multiples on `getAdditionalHoursPrice()`.
- **Validation Fallbacks:** Although concurrency bugs are relatively suppressed thanks to straightforward state validations on initialization, extremely heavy concurrent API queries invoking identical resources might still experience edge-case "dirty-writes" given the system lacks optimistic locks. 

## Steps on How to Run and Test the Code

**System Requirements:**
- JDK 17+ installed.
- Maven.

### Step 1: Boot The Setup
Launch a terminal window within the project's root hierarchy block containing `pom.xml` and fire the following standard commands.
```bash
# Optional: compile and resolve any remaining local packages.
mvn clean install

# Startup Server.
mvn spring-boot:run
```
A Tomcat embedded web proxy operates on port `8080`.

### Step 2: Testing Workflows 

To map out a fully realized journey across the architecture run the HTTP requests below manually substituting details appropriately via Postman or `cURL`:

**1. Create a Resource Object**
```bash
curl -X POST http://localhost:8080/api/resources \
     -H "Content-Type: application/json" \
     -d '{"name": "Gaming PC Setup", "capacity": 5}'
```

**2. Attach Pricing Tiers (Services)**
Attach a premium pricing bundle tracking baseline costs assuming the created parent returned with ID `1`.
```bash
curl -X POST http://localhost:8080/api/resources/1/services \
     -H "Content-Type: application/json" \
     -d '{"serviceName": "Premium Pricing", "firstHourCost": 100, "additionalHourCost": 50}'
```

**3. Actively Start A Session**
Registering `userId` (`101`) to interact via `serviceId` (`1`) on `resourceId` (`1`).
```bash
curl -X POST http://localhost:8080/api/sessions \
     -H "Content-Type: application/json" \
     -d '{"resourceId": 1, "userId": 101, "serviceId": 1}'
```

*(You may optionally pause for brief second cycles to emulate actual duration).*

**4. Stop the Running Session & Generate A Finalized Invoice**
Assume the previous call initiated a session assigned an ID of `1`. Call `stop` which halts the watch.
```bash
curl -X POST http://localhost:8080/api/sessions/1/stop
```

**5. Read Generated Invoice History**
Inspect finalized math mappings:
```bash
curl -X GET http://localhost:8080/api/bills/1
```
