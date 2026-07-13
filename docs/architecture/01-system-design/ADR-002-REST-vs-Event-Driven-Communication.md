# ADR-002: Choosing Event-Driven Communication over Synchronous REST

**Status:** Accepted

**Date:** July 2026

**Category:** Service Communication

**Decision Makers:** Hari Pradap V

---

# 1. Context

After deciding to build the Hospital Management System as a collection of microservices, the next architectural question was:

> **How should services communicate with each other?**

Every business operation affects multiple services.

For example:

When an appointment is created:

- Appointment Service stores the appointment.
- Billing Service may generate an invoice.
- Analytics Service updates dashboard statistics.
- Notification Service informs the patient.

These services need a reliable mechanism to exchange information.

---

# 2. Problem Statement

How should microservices communicate while keeping the system:

- loosely coupled,
- scalable,
- resilient to failures,
- easy to extend?

---

# 3. Initial Approach

The most natural solution was synchronous REST communication.

```
Appointment Service

↓

POST /billing

↓

Billing Service

↓

POST /notification

↓

Notification Service

↓

POST /analytics

↓

Analytics Service
```

Every service directly calls another service.

---

# 4. Advantages of REST Communication

REST is:

- Easy to understand
- Easy to debug
- Widely supported
- Request/response based
- Suitable for CRUD operations

Initially it appeared to be the simplest solution.

---

# 5. Problems with REST

After discussing several real-world scenarios, multiple issues became apparent.

---

## Problem 1 — Tight Coupling

Appointment Service must know:

- Billing Service URL
- Notification Service URL
- Analytics Service URL

```
Appointment

↓

Billing

↓

Notification

↓

Analytics
```

Appointment now depends on all three services.

This violates one of the primary goals of microservices: independence.

---

## Problem 2 — Cascading Failures

Suppose Notification Service is unavailable.

```
Appointment

↓

Billing

↓

Notification ❌
```

Should appointment creation fail?

If yes,

patients cannot book appointments because the notification system is offline.

A non-critical service causes a critical business process to fail.

---

## Problem 3 — Latency

Every network call adds latency.

```
Appointment

↓

Billing

↓

Notification

↓

Analytics
```

Instead of completing one database transaction,

the request waits for several network operations.

Response time grows with every downstream dependency.

---

## Problem 4 — Difficult Extension

Imagine adding Audit Service.

With REST:

Appointment Service must now also call:

```
Audit Service
```

Later,

suppose Fraud Detection is introduced.

Appointment Service changes again.

Every new consumer requires changing the producer.

---

## Problem 5 — Availability

If Billing Service is under maintenance,

Appointment Service cannot complete its workflow.

Availability of one service becomes dependent on another.

---

# 6. Alternative Options Considered

---

## Option A — REST

```
Appointment

↓

Billing

↓

Notification

↓

Analytics
```

### Advantages

- Simple
- Easy debugging
- Immediate response
- Familiar architecture

### Disadvantages

- Tight coupling
- Runtime dependencies
- Cascading failures
- Higher latency

---

## Option B — gRPC

We also considered gRPC.

```
Appointment

↓

Billing
```

gRPC solves some REST problems:

- Smaller payloads
- Faster serialization
- HTTP/2
- Strong contracts

However,

it is still synchronous communication.

If Billing Service is unavailable,

Appointment still waits.

The coupling problem remains.

---

## Option C — Event-Driven Communication (Chosen)

Instead of calling services directly,

Appointment Service publishes an event.

```
Appointment Service

↓

Appointment Created Event

↓

Kafka

↓

Billing

Analytics

Notification
```

Appointment does not know

who receives the event.

It simply announces:

> "An appointment has been created."

---

# 7. Why Event-Driven Architecture Was Chosen

This approach aligns with one of the core principles of distributed systems:

> Producers should not need to know who consumes their events.

Instead of asking another service to perform work,

a service simply announces that something happened.

Consumers independently decide whether they are interested.

---

# 8. How It Works

When a patient books an appointment:

```
Patient

↓

Appointment Service

↓

Save Appointment

↓

Publish APPOINTMENT_CREATED

↓

Kafka
```

Kafka distributes the event to all interested consumers.

```
Billing Service

↓

Generate Bill
```

```
Notification Service

↓

Notify Patient
```

```
Analytics Service

↓

Update Dashboard
```

None of these services communicate directly.

---

# 9. Benefits

## Loose Coupling

Appointment Service does not know:

- Billing exists
- Notification exists
- Analytics exists

It only knows Kafka.

---

## Independent Evolution

Adding another service requires:

```
Subscribe to Kafka
```

No changes to Appointment Service.

---

## Better Scalability

Consumers scale independently.

Notification may run:

```
10 instances
```

Analytics:

```
2 instances
```

Billing:

```
3 instances
```

Each scales according to workload.

---

## Better Fault Isolation

Suppose Notification Service crashes.

Appointment creation still succeeds.

Notification resumes processing when it becomes available.

Critical business operations continue.

---

## Better Extensibility

Future services become easy to introduce.

Example:

```
Audit Service

↓

Subscribe

↓

Done
```

Producer remains unchanged.

---

# 10. Trade-offs Accepted

Event-driven architecture introduces new challenges.

---

### Eventual Consistency

Immediately after appointment creation,

Billing data may not yet exist.

Services become eventually consistent.

---

### Harder Debugging

Instead of tracing one HTTP request,

developers trace events flowing through Kafka.

---

### Duplicate Processing

Consumers may receive duplicate events.

Handlers must therefore be idempotent.

---

### Event Schema Management

Changing an event affects multiple consumers.

Contracts become extremely important.

---

# 11. Lessons Learned

One of the most important lessons from this decision was:

> Services should communicate through business events rather than implementation details.

Instead of saying:

> Generate a bill.

The producer says:

> An appointment has been created.

The meaning shifts from commanding another service to informing the system of a business fact.

This significantly reduces coupling.

---

# 12. Future Decisions Enabled

This decision directly enabled:

- Kafka as the messaging backbone
- CQRS Read Models
- Analytics projections
- Notification projections
- Event-Carried State Transfer
- Generic Kafka consumers
- RabbitMQ notification pipeline

Without ADR-002,

many of the later architectural decisions would not have existed.

---

# 13. Interview Questions

### Why did you choose Kafka over REST?

### Why not call Billing directly?

### What is loose coupling?

### What happens if Notification Service is down?

### What is eventual consistency?

### How do you add another consumer?

### What problems does event-driven architecture introduce?

### How do you prevent duplicate processing?

---

# 14. Final Decision

The Hospital Management System will use **event-driven communication** for asynchronous business workflows.

Instead of services calling one another directly, services publish business events to Kafka.

Consumers independently subscribe to the events they require.

This architecture improves scalability, resilience, extensibility, and aligns with modern distributed system design.