 # Disaster Response Console

A console-based disaster response management system built in Java, developed as part of **CSI 142 – Introduction to Object-Oriented Programming**.

---

 # Table of Contents

- [Project Overview](#project-overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Class Hierarchy & Design](#class-hierarchy--design)
  - [Disaster Hierarchy](#disaster-hierarchy)
  - [Responder Hierarchy](#responder-hierarchy)
  - [The Dispatchable Interface](#the-dispatchable-interface)
  - [Enums](#enums)
  - [The Console / Registry](#the-console--registry)
  - [The Dispatch Engine](#the-dispatch-engine)
  - [ID Generator Utility](#id-generator-utility)
- [OOP Concepts Demonstrated](#oop-concepts-demonstrated)
- [How to Run](#how-to-run)
- [Sample Workflow](#sample-workflow)
- [Planned & Stretch Features](#planned--stretch-features)
- [Team Members](#team-members)

---

# Project Overview

The **Disaster Response Console** simulates a real-world emergency coordination system. An operative (the user) interacts with the program through a text-based console to:

1. Register new disasters (e.g. floods, wildfires, medical emergencies).
2. Register available responders (e.g. fire units, medical teams, rescue crews).
3. Let the system **automatically dispatch** the most appropriate responders based on the disaster's priority and required resources.

The project is a practical demonstration of core Java OOP principles — abstraction, inheritance, polymorphism, encapsulation, and interfaces — applied to a realistic and meaningful problem domain.

---

## Features

# Core Features
- Register disasters with a type, location, and priority level
- Register responders with a category and availability status
- Automatic priority-based dispatch engine that matches disasters to suitable responders
- Enum-driven status management (`PENDING`, `ACTIVE`, `RESOLVED`)
- Unique ID generation with uniqueness validation

# Extended Features
- Search and filter disasters by type
- Search and filter responders by category
- Lookup any entity by its unique ID

# Stretch Goals
- Timed dispatch expiry — dispatch records automatically close after a set time has elapsed
- Simulated disaster generation — random scenarios generated for realistic testing

---

# Project Structure

```
CSI-142-GroupProject/
│
├── src/
│   └── com/
│       └── ub/
│           └── project/
│               │
│               ├── Main.java                  ← Entry point; runs the console loop
│               │
│               ├── disasters/
│               │   ├── Disaster.java          ← Abstract base class for all disasters
│               │   ├── Flood.java             ← Concrete disaster: Flood
│               │   ├── Wildfire.java          ← Concrete disaster: Wildfire
│               │   └── Medical.java           ← Concrete disaster: Medical Emergency
│               │
│               ├── responders/
│               │   ├── Responder.java         ← Abstract base class for all responders
│               │   ├── FireUnit.java          ← Concrete responder: Fire Unit
│               │   ├── MedicalTeam.java       ← Concrete responder: Medical Team
│               │   └── RescueCrew.java        ← Concrete responder: Rescue Crew
│               │
│               ├── interfaces/
│               │   └── Dispatchable.java      ← Interface defining dispatch contracts
│               │
│               ├── enums/
│               │   ├── DisasterStatus.java    ← Enum: PENDING, ACTIVE, RESOLVED
│               │   ├── PriorityLevel.java     ← Enum: LOW, MEDIUM, HIGH, CRITICAL
│               │   └── DurationCategory.java  ← Enum: SHORT, MEDIUM, LONG
│               │
│               ├── registry/
│               │   └── Console.java           ← Central manager; holds all collections
│               │
│               └── utils/
│                   └── IdGenerator.java       ← Utility for generating unique IDs
│
├── build_cmd.txt                              ← Build/run commands
├── .gitignore
└── README.md
```

> **Note:** The package structure follows Java conventions (`com.ub.project`) and all source files live under `src/`.

---

# Class Hierarchy & Design

## Disaster Hierarchy

```
Disaster  (abstract)
├── Flood
├── Wildfire
└── Medical
```

### `Disaster.java` — Abstract Base Class

This is the **foundation** for every disaster in the system. Because no disaster should ever be a plain "Disaster" (it must be a specific type), this class is declared `abstract` — meaning you cannot create an instance of it directly.

**Fields (shared by all disaster subclasses):**

| Field | Type | Description |
|-------|------|-------------|
| `id` | `String` | Auto-generated unique identifier |
| `location` | `String` | Where the disaster is occurring |
| `priorityLevel` | `PriorityLevel` | Enum value: LOW, MEDIUM, HIGH, or CRITICAL |
| `status` | `DisasterStatus` | Enum value: PENDING, ACTIVE, or RESOLVED |

**Key methods:**
- `getters/setters` for all fields (encapsulation)
- `abstract String getType()` — forces every subclass to declare its own type name
- `abstract String[] getRequiredResources()` — each subclass specifies what it needs (via `Dispatchable`)
- `displayInfo()` — prints the disaster's details to the console

---

#### `Flood.java`, `Wildfire.java`, `Medical.java` — Concrete Subclasses

Each subclass **extends** `Disaster` and provides its own implementation of the abstract methods. They may also carry additional fields specific to their type.

**Example — `Wildfire.java`:**
```java
public class Wildfire extends Disaster implements Dispatchable {

    private int estimatedAcres;   // extra field specific to wildfires

    public Wildfire(String location, PriorityLevel priority, int estimatedAcres) {
        super(location, priority);  // calls Disaster constructor
        this.estimatedAcres = estimatedAcres;
    }

    @Override
    public String getType() {
        return "Wildfire";
    }

    @Override
    public String[] getRequiredResources() {
        return new String[]{"FireUnit", "RescueCrew"};  // wildfires need fire units & rescue
    }
}
```

Each disaster knows what responder categories it needs. This list is read by the dispatch engine when assigning responders.

---

### Responder Hierarchy

```
Responder  (abstract)
├── FireUnit
├── MedicalTeam
└── RescueCrew
```

#### `Responder.java` — Abstract Base Class

Mirrors the `Disaster` class structure but for response personnel and vehicles. Again declared `abstract` so only specific types of responders can be instantiated.

**Fields:**

| Field | Type | Description |
|-------|------|-------------|
| `id` | `String` | Auto-generated unique identifier |
| `name` | `String` | Name or call-sign of the unit |
| `isAvailable` | `boolean` | Whether this responder is free to be dispatched |
| `deploymentTime` | `DurationCategory` | Estimated time to deploy: SHORT, MEDIUM, or LONG |

**Key methods:**
- `abstract String getCategory()` — returns the responder's category (e.g. `"FireUnit"`)
- `dispatch()` — marks the responder as unavailable (deployed)
- `returnToBase()` — marks the responder as available again

---

#### `FireUnit.java`, `MedicalTeam.java`, `RescueCrew.java` — Concrete Subclasses

Each subclass extends `Responder` and provides its own category identifier and any type-specific behaviour or attributes.

**Example — `MedicalTeam.java`:**
```java
public class MedicalTeam extends Responder {

    private int numberOfParamedics;

    public MedicalTeam(String name, int numberOfParamedics) {
        super(name);
        this.numberOfParamedics = numberOfParamedics;
    }

    @Override
    public String getCategory() {
        return "MedicalTeam";
    }
}
```

The `getCategory()` return value is what the dispatch engine compares against the disaster's `getRequiredResources()` list to find a match.

---

### The Dispatchable Interface

```java
public interface Dispatchable {
    String[] getRequiredResources();
    int getPriorityScore();
}
```

The `Dispatchable` interface is a **contract**. Any class that implements it must provide:

1. `getRequiredResources()` — an array of responder category names that this disaster needs.
2. `getPriorityScore()` — a numeric score derived from the priority level, used to order dispatch.

All three disaster subclasses (`Flood`, `Wildfire`, `Medical`) implement `Dispatchable`. This means the dispatch engine can treat every disaster uniformly through the interface — it doesn't need to know whether it's dealing with a Flood or a Wildfire, it just calls `getRequiredResources()` and acts on the result. This is **polymorphism** at work.

---

### Enums

Enums are used instead of plain strings or magic numbers to represent fixed sets of values. This prevents typos and makes the code much easier to read and maintain.

#### `DisasterStatus`
```java
public enum DisasterStatus {
    PENDING,   // disaster registered, responders not yet assigned
    ACTIVE,    // responders dispatched, emergency in progress
    RESOLVED   // situation handled, dispatch record closed
}
```

#### `PriorityLevel`
```java
public enum PriorityLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
```

#### `DurationCategory`
```java
public enum DurationCategory {
    SHORT,   // e.g. under 1 hour deployment time
    MEDIUM,  // e.g. 1–4 hours
    LONG     // e.g. over 4 hours
}
```

Using enums also enables clean `switch` statements in the dispatch logic and status transitions.

---

### The Console / Registry

`Console.java` is the **central manager** of the entire system. Think of it as the command centre — everything passes through it.

**What it holds:**
```java
private ArrayList<Disaster> disasters;
private ArrayList<Responder> responders;
```

**What it does:**

| Method | Description |
|--------|-------------|
| `addDisaster(Disaster d)` | Validates ID uniqueness, adds to list, triggers dispatch |
| `addResponder(Responder r)` | Validates ID uniqueness, adds to list |
| `viewAllDisasters()` | Prints all registered disasters |
| `viewAllResponders()` | Prints all registered responders |
| `findDisasterById(String id)` | Returns a specific disaster or null |
| `findResponderById(String id)` | Returns a specific responder or null |
| `filterDisastersByType(String type)` | Returns filtered list by disaster type |
| `filterRespondersByCategory(String cat)` | Returns filtered list by responder category |

When `addDisaster()` is called, it automatically invokes the dispatch engine before returning. This means the operative doesn't need to manually trigger dispatching — it's always automatic.

---

# The Dispatch Engine

The dispatch logic lives inside `Console.java` (or a dedicated `DispatchEngine` helper). Here is how it works step by step:

```
1. A new Disaster is registered via addDisaster().

2. The disaster's getRequiredResources() is called (via the Dispatchable interface).
   → Returns e.g. ["FireUnit", "RescueCrew"]

3. The disaster's getPriorityScore() is checked.
   → Higher priority disasters are dispatched first.

4. For each required resource type:
   a. Search the responders ArrayList for an available responder
      whose getCategory() matches the required type.
   b. If found: call responder.dispatch() to mark them unavailable,
      and add them to the disaster's assigned responders list.
   c. If not found: log that no responder is currently available.

5. Update the disaster's status:
   → At least one responder assigned → ACTIVE
   → No responders available        → remains PENDING
```

**Why this design is powerful:** Because both disasters and responders are referenced through their abstract base classes and interfaces, the engine works for any new disaster or responder type added in the future — without changing the dispatch logic itself.

---

# ID Generator Utility

`IdGenerator.java` is a **utility class** (all static methods, never instantiated) that generates unique IDs for both disasters and responders.

```java
// Generate a random 10-character alphanumeric ID
String id = IdGenerator.generateShortId(10);

// Generate a standard UUID (used as a database-style primary key)
String uuid = IdGenerator.generateUUID();

// Generate a 6-digit random numeric ID
long numericId = IdGenerator.generateNumericId(6);
```

The `Console` class calls this when creating new entities and checks that the generated ID doesn't already exist in either ArrayList before accepting it.

---

## OOP Concepts Demonstrated

| Concept | Where it appears |
|---------|-----------------|
| **Abstract Classes** | `Disaster` and `Responder` — shared structure without direct instantiation |
| **Interfaces** | `Dispatchable` — defines dispatch contract independently of class hierarchy |
| **Inheritance** | `Flood`, `Wildfire`, `Medical` extend `Disaster`; `FireUnit` etc. extend `Responder` |
| **Polymorphism** | Dispatch engine calls `getRequiredResources()` on any `Dispatchable` object; `getCategory()` on any `Responder` |
| **Encapsulation** | All fields are `private` with `public` getters/setters; internal state never exposed directly |
| **Enums** | `DisasterStatus`, `PriorityLevel`, `DurationCategory` — type-safe constants with built-in methods |
| **Collections** | `ArrayList<Disaster>` and `ArrayList<Responder>` in `Console` for dynamic, resizable storage |

---

## How to Run

### Prerequisites
- **Java 11 or higher** installed
- A terminal or IDE (e.g. IntelliJ IDEA, Eclipse, VS Code with Java extension)

### Option 1: From the terminal

```bash
# 1. Clone the repository
git clone https://github.com/MVMC4/CSI-142-GroupProject.git
cd CSI-142-GroupProject

# 2. Compile all source files
javac -d out src/com/ub/project/**/*.java src/com/ub/project/*.java

# 3. Run the application
java -cp out com.ub.project.Main
```

> See `build_cmd.txt` in the root of the repo for the exact compile/run commands used during development.

### Option 2: From an IDE

1. Open the project folder in your IDE.
2. Mark `src/` as the **Sources Root**.
3. Run `Main.java`.

---

## Sample Workflow

```
=== DISASTER RESPONSE CONSOLE ===
1. Register Disaster
2. Register Responder
3. View All Disasters
4. View All Responders
5. Search by ID
6. Exit

> 2
Enter responder name: Alpha Fire Unit
Enter category (FireUnit/MedicalTeam/RescueCrew): FireUnit
Enter deployment time (SHORT/MEDIUM/LONG): SHORT
✔ Responder registered. ID: RF-00421

> 1
Enter disaster type (Flood/Wildfire/Medical): Wildfire
Enter location: Mokolodi Nature Reserve
Enter priority (LOW/MEDIUM/HIGH/CRITICAL): HIGH
✔ Disaster registered. ID: DW-00834
 Dispatching...
  → FireUnit [Alpha Fire Unit] assigned. Status: ACTIVE
```

---

## Planned & Stretch Features

| Feature | Status |
|---------|--------|
| Priority-based dispatch engine |  Core |
| Enum-driven status transitions |  Core |
| Unique ID generation & validation |  Core |
| Filter/search by type or category |  Extended |
| Lookup by ID |  Extended |
| Timed dispatch expiry | Stretch |
| Simulated disaster generation | Stretch |

---

## Team Members

| Name | Student ID |
|------|-----------|
| Abel Makwapa | 202505288 |
| Maatla Mothobi | 202500816 |
| Ben Mudevayiri | 202503552 |
| Mooketsi Magwaza | 202505647 |

**Course:** CSI 142 — Introduction to Object-Oriented Programming  
**Institution:** University of Botswana  
**Submission:** Milestone 1 — 10 April 2026  
**Repository:** [github.com/MVMC4/CSI-142-GroupProject](https://github.com/MVMC4/CSI-142-GroupProject)