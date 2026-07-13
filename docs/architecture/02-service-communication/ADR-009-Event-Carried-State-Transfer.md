# ADR-009: Event-Carried State Transfer

**Status:** Accepted

**Date:** July 2026

**Category:** Service Communication

**Decision Makers:** Hari Pradap V

---

# 1. Context

While designing the Notification Service, a new problem emerged.

The Notification Service is responsible for sending notifications such as:

- Appointment Confirmed
- Appointment Cancelled
- Bill Paid
- Prescription Ready

To send an email, Notification Service requires information such as:

- Patient Email
- Patient Name
- Doctor Name

However,

the Notification Service does not own this data.

Patient information belongs to Patient Service.

Doctor information belongs to Doctor Service.

This introduced an architectural question:

> How should Notification Service obtain data owned by another service?

---

# 2. Problem Statement

Should Notification Service retrieve patient information from Patient Service whenever a notification is required,

or

should the required information already exist inside Notification Service?

The decision directly impacts:

- latency
- availability
- coupling
- scalability
- fault tolerance

---

# 3. Initial Approach

Initially we considered making synchronous requests.

```
Appointment Created

↓

Notification Service

↓

REST / gRPC

↓

Patient Service

↓

Patient Email

↓

Notification Service

↓

Send Email
```

This seemed natural because Patient Service already owns the data.

---

# 4. Why This Looked Attractive

Advantages:

- Single source of truth
- No duplicated data
- No synchronization
- Easy to understand

Many developers choose this architecture initially.

---

# 5. Problems

After analysing failure scenarios,

several issues became apparent.

---

## Problem 1 — Runtime Dependency

Every notification now depends on Patient Service.

```
Notification

↓

Patient Service
```

If Patient Service is unavailable,

Notification Service cannot function.

---

## Problem 2 — Increased Latency

Sending one notification becomes:

```
Receive Event

↓

Call Patient Service

↓

Receive Response

↓

Build Email

↓

Send Email
```

Every notification requires an additional network call.

---

## Problem 3 — Cascading Failures

Suppose:

Patient Service becomes unavailable.

```
Notification

↓

Patient Service ❌
```

Notifications stop,

even though Notification Service itself is healthy.

A failure in one service propagates to another.

---

## Problem 4 — High Traffic

Imagine:

```
Appointment Created

↓

5000 Patients
```

Notification Service now sends:

```
5000 REST Requests
```

to Patient Service.

Patient Service experiences unnecessary load,

even though the information changes very rarely.

---

## Problem 5 — Tight Coupling

Notification Service now knows:

- Patient API
- Authentication
- DTOs
- Endpoints

The services become tightly coupled.

---

# 6. Alternative Options

---

## Option A — REST

```
Notification

↓

Patient Service
```

Advantages:

- No duplicate data
- Simple implementation

Disadvantages:

- Runtime dependency
- Higher latency
- Cascading failures

---

## Option B — gRPC

```
Notification

↓

Patient Service
```

using Protocol Buffers.

Advantages:

- Faster
- Strong contracts

Disadvantages:

Exactly the same architectural dependency remains.

Only the transport changes.

---

## Option C — Event-Carried State Transfer (Chosen)

Instead of asking Patient Service for information,

Patient Service publishes the required information whenever it changes.

```
Patient Created

↓

Kafka

↓

Notification Service

↓

Local Database
```

Notification Service now owns a local read model containing only the fields it needs.

---

# 7. What Is Event-Carried State Transfer?

Instead of publishing only an event identifier,

the producer includes enough state for consumers.

Instead of:

```
Patient Created

↓

PatientId
```

publish:

```
Patient Created

↓

PatientId

FirstName

LastName

Email

Phone
```

Consumers no longer need to fetch additional information.

The event carries the required state.

---

# 8. Final Architecture

```
Patient Service

↓

PATIENT_CREATED

↓

Kafka

↓

Notification Service

↓

notification_patients

────────────────────────────

Appointment Created

↓

Notification Service

↓

notification_patients

↓

Email

↓

RabbitMQ
```

Notification Service never contacts Patient Service during notification delivery.

---

# 9. Benefits

## Loose Coupling

Notification Service no longer depends on Patient Service being online.

---

## Better Availability

Even if Patient Service is unavailable,

Notification Service continues sending emails.

---

## Lower Latency

No additional network request is required.

Everything is local.

---

## Better Scalability

Patient Service is no longer overwhelmed by repeated lookup requests.

The event is published once.

Every consumer benefits.

---

## Independent Deployments

Patient Service API changes do not affect Notification Service.

Only the event contract must remain compatible.

---

# 10. Trade-offs Accepted

This approach introduces new responsibilities.

---

### Data Duplication

Patient information now exists in multiple places.

Example:

```
Patient Service

↓

Notification Database

↓

Analytics Database
```

The duplication is intentional.

---

### Eventual Consistency

If a patient's email changes,

Notification Service receives the update asynchronously.

For a brief period,

the read model may contain stale information.

---

### Larger Events

Events now contain more data.

This increases message size,

but eliminates additional network calls.

---

### Event Schema Management

Changing event contracts requires careful versioning.

Consumers rely on the published schema.

---

# 11. Lessons Learned

One of the biggest lessons from this decision was:

> In distributed systems, duplicating data is often preferable to duplicating network requests.

Initially,

data duplication felt like a design mistake.

After understanding distributed systems,

it became clear that local read models improve:

- resilience
- scalability
- availability
- latency

The goal is not to eliminate duplication,

but to eliminate unnecessary runtime dependencies.

---

# 12. Future Decisions Enabled

This decision directly enabled:

- CQRS Read Models
- Patient Projection
- Doctor Projection
- Analytics Projection
- Generic Kafka Consumers
- Notification Dispatcher
- Offline Notification Processing

Without this decision,

Notification Service would require synchronous calls before every notification.

---

# 13. Interview Questions

### What is Event-Carried State Transfer?

### Why didn't Notification Service call Patient Service?

### Isn't data duplication bad?

### What happens if patient email changes?

### How do projections stay synchronized?

### How do you version events?

### What are the disadvantages?

### How is this different from CQRS?

---

# 14. Final Decision

The Hospital Management System adopts the Event-Carried State Transfer pattern.

Instead of retrieving data synchronously from other services,

producers publish the business data required by downstream consumers.

Consumers maintain local read models synchronized through Kafka events.

This removes runtime dependencies, improves resilience, reduces latency, and supports a scalable event-driven architecture.