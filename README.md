# E-Commerce Fulfillment System (Assignment 4: Facade Pattern)

## 1. Overview
This application is a Java-based implementation of the **Facade Design Pattern** applied to an **E-Commerce Fulfillment** workflow. The system coordinates three independent subsystems:

1. **Subsystem A (Intake / Inventory):** `InventoryManager`, `InventoryRepository`, `OrderValidator`, `InventoryItem`
2. **Subsystem B (Processing / Warehouse):** `PackingStation`, `PackingRepository`, `WarehousePicker`, `PackingTask`
3. **Subsystem C (Finalization / Shipping):** `ShippingCarrierAPI`, `ShippingRepository`, `PaymentGateway`, `ShipmentRecord`

The application enforces durable persistence using **SQLite** (`fulfillment.db`), state transaction management across a 3-stage chain (A -> B -> C), two depth levels of automated rollbacks (compensation), and an admin inspection layer.

---

## 2. Key Features & Architecture

### Durable State & Persistence
- Every subsystem directly manages its own persistent state in an embedded SQLite database (`fulfillment.db`).
- Progress state (PENDING_B, PENDING_C, COMPLETED, COMPENSATED) is explicitly persisted in `chain_progress` table.
- If the application crashes or restarts mid-chain, the system recovers state directly from disk to determine whether steps need to be completed or compensated.

### Cross-Subsystem Rollback (Two Compensation Depths)
- **Failure at Step B (After A commits):** The facade detects the failure and unwinds Step A alone (`CANCELLED` status).
- **Failure at Step C (After A and B commit):** The facade detects the failure and executes a two-step rollback sequence (C -> B -> A) in strict reverse order.

---

## 3. Principle of Least Knowledge (Law of Demeter)

### Before (Without Facade)
Without a facade, the client code must possess deep knowledge of all internal subsystem classes, repositories, validators, and gateway components:

```java
// Client forced to manage initialization and internal interactions of 9+ classes
OrderValidator validator = new OrderValidator();
InventoryRepository invRepo = new InventoryRepository("jdbc:sqlite:fulfillment.db");
InventoryManager invManager = new InventoryManager(invRepo, validator);
invManager.reserveInventory("ORD-101", "SKU-55", 2);

WarehousePicker picker = new WarehousePicker();
PackingRepository packRepo = new PackingRepository("jdbc:sqlite:fulfillment.db");
PackingStation packStation = new PackingStation(packRepo, picker);
packStation.packOrder("ORD-101", 2);

PaymentGateway gateway = new PaymentGateway();
ShippingRepository shipRepo = new ShippingRepository("jdbc:sqlite:fulfillment.db");
ShippingCarrierAPI carrier = new ShippingCarrierAPI(shipRepo, gateway);
carrier.dispatchShipment("ORD-101", 150.0);

After (With Facade)
With the FulfillmentFacade, the client communicates only with a single unified entry point:

Java
// Client interacts with immediate friend only
FulfillmentFacade facade = getFulfillmentFacade();
boolean success = facade.processFulfillment("ORD-101", "SKU-55", 2, 150.0);
4. Facade vs. Adapter Written Reflection
Why Facade over Adapter?
This domain requires a Facade rather than an Adapter due to the fundamentally different intents of the two design patterns:

Intent of Adapter: An Adapter translates an existing, incompatible interface into another interface that a client expects. It typically operates over a single class or service to achieve structural interface compatibility without altering underlying business behavior.

Intent of Facade: A Facade provides a higher-level, simplified interface over a complex ecosystem of multiple independent subsystems. It encapsulates multi-step workflows, orchestration rules, state persistence, and transactional rollbacks.

In this E-Commerce Fulfillment system, we do not need to adapt an incompatible interface to match a client signature. Instead, we need to unify three completely unrelated subsystems (InventoryManager, PackingStation, ShippingCarrierAPI) into a single, cohesive A -> B -> C workflow with compensating rollbacks. Therefore, the problem inherently calls for a Facade.

5. How to Run & Live Demo Instructions
Requirements
Java JDK 17 or higher

Apache Maven

Build & Run Application

# Compile and build project
mvn clean package

# Run CLI Application
mvn exec:java -Dexec.mainClass="org.example.Main"
Resetting Persistent Storage
To reset the SQLite database to a completely clean state before running fresh demonstrations:


# On Linux/macOS
rm -f fulfillment.db test_*.db

# On Windows (PowerShell)
Remove-Item -Force fulfillment.db, test_*.db -ErrorAction SilentlyContinue
6. Running Unit Tests & JaCoCo Coverage
Run all FIRST-compliant unit tests and generate code coverage reports using Maven:


mvn clean test
