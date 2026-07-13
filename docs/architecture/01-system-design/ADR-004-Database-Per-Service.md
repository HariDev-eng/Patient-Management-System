# ADR-004: Database per Service Pattern

**Status:** Accepted

**Date:** July 2026

**Category:** System Design

**Decision Makers:** Hari Pradap V

---

# 1. Context

After deciding to build the Hospital Management System as a collection of microservices (ADR-001), another critical architectural question emerged:

> Should all services share a single database, or should each service own its own database?

The system contains several independent business domains:

- Patient Management
- Doctor Management
- Appointment Scheduling
- Billing
- Inventory
- Prescription
- Analytics
- Notification

Each service manages its own business data.

The way this data is stored directly impacts service independence, scalability, and maintainability.

---

# 2. Problem Statement

How should data be stored so that services remain:

- independent,
- loosely coupled,
- scalable,
- secure,
- maintainable?

---

# 3. Initial Approach

The simplest approach was using one PostgreSQL database shared by every service.

```
                 PostgreSQL

 ┌──────────────────────────────────────┐

 Patients

 Doctors

 Appointments

 Bills

 Inventory

 Prescriptions

 Notifications

 Analytics

 └──────────────────────────────────────┘

        ▲
        ▲
        ▲

 All services connect here
```

Every service connects directly to the same database.

---

# 4. Why This Looked Attractive

Advantages:

- One database server
- Easier joins
- Easier reporting
- Simpler transactions
- Less infrastructure
- Easy to develop

For many CRUD applications, this is perfectly acceptable.

---

# 5. Problems with a Shared Database

After discussing real production systems, several major issues became apparent.

---

## Problem 1 — Tight Coupling

Suppose Billing directly reads the Patient table.

```
Billing Service

↓

Patient Table
```

Now Billing depends on the Patient database schema.

If Patient Service renames a column,

Billing breaks.

Although services are separated,

their databases are still tightly coupled.

---

## Problem 2 — Broken Ownership

Who owns the Patients table?

Patient Service?

Billing?

Analytics?

If multiple services modify the same tables,

there is no clear ownership.

Business rules become scattered across services.

---

## Problem 3 — Hidden Dependencies

Imagine Appointment Service updates:

```
Patients

Appointments

Bills
```

directly.

Now changes happen without the owning service even knowing.

The service boundary becomes meaningless.

---

## Problem 4 — Independent Deployment Becomes Difficult

Suppose Patient Service changes:

```
email VARCHAR(100)

↓

email VARCHAR(255)
```

Every service accessing that table must now be tested.

A schema change in one service affects every other service.

---

## Problem 5 — Security

Should Notification Service have permission to modify patient records?

No.

With a shared database,

every service potentially has access to everything.

This violates the Principle of Least Privilege.

---

# 6. Option A — Shared Database

```
Patient

Doctor

Appointment

Billing

↓

One PostgreSQL
```

## Advantages

- Simple
- Easy joins
- ACID transactions
- Less infrastructure

## Disadvantages

- Tight coupling
- Shared ownership
- Difficult scaling
- Schema dependencies
- Weak service boundaries

---

# 7. Option B — Database per Service (Chosen)

Each service owns its own database schema.

```
Patient Service

↓

patient_db

-------------------------

Doctor Service

↓

doctor_db

-------------------------

Appointment Service

↓

appointment_db

-------------------------

Billing Service

↓

billing_db

-------------------------

Inventory Service

↓

inventory_db

-------------------------

Notification Service

↓

notification_db

-------------------------

Analytics Service

↓

analytics_db
```

No service directly accesses another service's database.

---

# 8. Why This Option Was Chosen

A microservice should own not only its business logic, but also its data.

Other services communicate through APIs or events—not SQL queries.

This ensures clear ownership and prevents accidental coupling.

---

# 9. Communication Changes

Instead of:

```
Billing

↓

SELECT * FROM Patients
```

Billing now receives information through events.

```
Patient Created

↓

Kafka

↓

Billing
```

Or through a synchronous API when immediate consistency is required.

The database is no longer the integration point.

The service becomes the integration point.

---

# 10. Benefits

## Independent Schema Evolution

Patient Service can redesign its database without affecting Billing.

Only the public API and event contracts remain stable.

---

## Clear Ownership

Each service owns:

- tables
- indexes
- migrations
- business rules

No ambiguity exists.

---

## Better Security

Notification Service never receives database credentials for Patient Service.

It only stores the information it actually needs.

---

## Independent Scaling

Large databases can be scaled independently.

Inventory may require different indexing strategies than Analytics.

Each service can optimize its own storage.

---

## Technology Flexibility

Although every service currently uses PostgreSQL,

this architecture allows future flexibility.

For example:

```
Analytics

↓

ClickHouse

or

Elasticsearch
```

without affecting the rest of the system.

---

# 11. Trade-offs Accepted

Database-per-service introduces challenges.

---

### No Cross-Service SQL Joins

Instead of:

```
SELECT
Patients.name,
Appointments.date
```

across databases,

services must communicate through APIs or events.

---

### Eventual Consistency

Patient information is replicated into Analytics and Notification.

Updates are no longer instantaneous.

They become eventually consistent.

---

### More Infrastructure

Instead of one database,

the project manages multiple databases or schemas.

This increases operational complexity.

---

# 12. Lessons Learned

One of the most important lessons was:

> A microservice is not truly independent if another service depends directly on its database.

Service ownership includes:

- code,
- APIs,
- business rules,
- **and the database**.

Sharing the database undermines the purpose of microservices.

---

# 13. Future Decisions Enabled

Choosing database-per-service directly enabled:

- Kafka-based communication
- CQRS Read Models
- Notification Projections
- Analytics Projections
- Event-Carried State Transfer
- Independent database migrations
- Independent deployments

Without this decision, later architectural patterns such as projections and read models would not have been necessary.

---

# 14. Interview Questions

### Why shouldn't microservices share a database?

### What are the disadvantages of a shared database?

### How do services access another service's data?

### How do you perform reporting across multiple services?

### What is data ownership?

### What happens if Patient Service changes its schema?

### How do you handle joins across services?

---

# 15. Final Decision

Every microservice in the Hospital Management System owns its own database.

Services never read or write another service's database directly.

Communication between services occurs through APIs or event streams, preserving service boundaries, enabling independent evolution, and supporting a scalable distributed architecture.