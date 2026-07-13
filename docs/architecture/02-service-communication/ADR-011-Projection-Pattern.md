# ADR-011: Introducing the Projection Pattern

**Status:** Accepted

**Date:** July 2026

**Category:** Service Communication

**Decision Makers:** Hari Pradap V

---

# 1. Context

After adopting CQRS (ADR-010), Notification Service maintains local read models for Patient and Doctor information.

Incoming Kafka events must now update these read models.

The next architectural question was:

> Who should update the read models?

Initially, the Kafka Consumer itself was responsible for updating the database.

However, this quickly mixed infrastructure code with business logic.

---

# 2. Problem Statement

Should Kafka Consumers write directly to the database,

or

should another layer be responsible for synchronizing read models?

---

# 3. Initial Approach

Initially the consumer looked like this.

```
Kafka Consumer

↓

Read Event

↓

Parse Protobuf

↓

Find Record

↓

Insert

↓

Update

↓

Delete
```

Everything happened inside one file.

Example:

```
PatientConsumer

↓

Read Message

↓

proto.Unmarshal()

↓

repository.Find()

↓

repository.Update()

↓

repository.Create()

↓

repository.Delete()
```

---

# 4. Why This Looked Attractive

Advantages

- Less code
- Fewer files
- Easy to understand
- Faster to build

Initially this looked like the simplest architecture.

---

# 5. Problems

After implementing several consumers,

the disadvantages became obvious.

---

## Problem 1 — Mixed Responsibilities

Consumer should only consume messages.

Instead it now:

- reads Kafka
- parses protobuf
- validates events
- performs business logic
- updates database

One component has multiple responsibilities.

This violates the Single Responsibility Principle.

---

## Problem 2 — Duplicate Logic

Imagine:

```
Patient Consumer

↓

Create Patient

Update Patient

Delete Patient
```

Later another consumer needs identical logic.

Now business logic exists in multiple places.

---

## Problem 3 — Difficult Testing

Testing a consumer now requires:

- Kafka
- Database
- Protobuf

Instead of testing business logic independently.

---

## Problem 4 — Tight Coupling

Consumer now depends directly on:

- Repository
- Database
- DTOs
- Business rules

Changing persistence logic requires changing Kafka code.

---

# 6. Alternative Options

---

## Option A — Consumer Updates Database

```
Kafka

↓

Consumer

↓

Repository
```

Advantages

- Simple
- Few layers

Disadvantages

- Mixed responsibilities
- Hard testing
- Duplicate code

---

## Option B — Projection Layer (Chosen)

```
Kafka

↓

Consumer

↓

Projection

↓

Repository

↓

Read Model
```

The Consumer only consumes.

Projection owns synchronization logic.

Repository owns persistence.

---

# 7. What Is a Projection?

A Projection transforms events into a query model.

Example:

Patient Updated Event

↓

Projection

↓

notification_patients

The Projection decides:

- Create
- Update
- Delete

based on the incoming event.

It represents the synchronization logic between the event stream and the read model.

---

# 8. Architecture

```
Patient Service

↓

PATIENT_UPDATED

↓

Kafka

↓

Patient Consumer

↓

Patient Projection

↓

Patient Repository

↓

notification_patients
```

Notice the responsibilities.

Consumer

↓

Infrastructure

Projection

↓

Business Synchronization

Repository

↓

Persistence

---

# 9. Why This Option Was Chosen

Separating responsibilities makes each layer easier to understand.

Consumer

Only reads Kafka.

Projection

Knows how events affect read models.

Repository

Knows how to talk to PostgreSQL.

Each layer has one responsibility.

---

# 10. Benefits

## Better Separation of Concerns

Infrastructure remains independent from business logic.

---

## Easier Testing

Projection can be tested without Kafka.

Consumer can be tested without PostgreSQL.

Repository can be tested independently.

---

## Better Maintainability

Changing synchronization rules requires modifying only Projection.

Kafka code remains untouched.

---

## Reusability

Multiple consumers can reuse Projection logic if necessary.

Business logic is centralized.

---

## Cleaner Architecture

Responsibilities become obvious.

```
Consumer

↓

Projection

↓

Repository
```

instead of

```
Consumer

↓

Everything
```

---

# 11. Trade-offs Accepted

Introducing Projections means:

- More files
- More interfaces
- More abstractions

The project becomes slightly larger.

However,

the architecture becomes significantly cleaner.

---

# 12. Lessons Learned

One important lesson from this decision was:

> Infrastructure should transport data.

> Business layers should decide what to do with the data.

Kafka's responsibility ends after delivering the message.

Projection decides how the system reacts.

---

# 13. Relationship with Previous ADRs

ADR-009

Event-Carried State Transfer

↓

Provides data.

ADR-010

CQRS

↓

Defines read models.

ADR-011

Projection Pattern

↓

Keeps those read models synchronized.

The three decisions work together.

---

# 14. Future Decisions Enabled

Projection Pattern enables:

- Patient Projection
- Doctor Projection
- Analytics Projection
- Inventory Projection
- Easier event versioning
- Easier projection rebuilding

---

# 15. Interview Questions

### What is a Projection?

### Is Projection the same as CQRS?

### Why not update the database directly from Kafka Consumer?

### Why introduce another layer?

### Can one Projection update multiple tables?

### How would you rebuild projections?

### What happens if Projection fails?

---

# 16. Final Decision

The Hospital Management System introduces a dedicated Projection layer between Kafka Consumers and Repositories.

Consumers are responsible only for receiving events.

Projections synchronize those events into service-specific read models.

Repositories remain responsible only for database persistence.

This separation results in cleaner architecture, improved testability, and better maintainability as the system grows.