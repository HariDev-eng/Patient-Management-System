# ADR-001: Choosing Microservices over a Monolithic Architecture

**Status:** Accepted

**Date:** July 2026

**Category:** System Design

**Decision Makers:** Hari Pradap V

---

# 1. Context

Before implementing the Hospital Management System, the first and most fundamental architectural decision was selecting the application architecture.

The system needed to support multiple business domains:

- Patient Management
- Doctor Management
- Appointment Scheduling
- Billing
- Prescription Management
- Inventory
- Analytics
- Notifications
- Authentication
- API Gateway

The question was whether to build all these capabilities inside a single application (Monolith) or split them into multiple independently deployable services (Microservices).

This decision would influence every architectural decision made afterward, including communication patterns, database ownership, deployment strategy, scalability, and technology choices.

---

# 2. Problem Statement

How should the Hospital Management System be structured so that it:

- can scale as the application grows,
- demonstrates production-grade backend architecture,
- allows different technologies to be used where appropriate,
- remains maintainable as new modules are introduced?

---

# 3. Requirements

The chosen architecture should satisfy the following requirements:

- Separation of business domains
- Independent deployment
- Independent scalability
- Technology flexibility
- Fault isolation
- Event-driven communication
- Production-ready architecture
- Strong learning value

---

# 4. Option A — Monolithic Architecture

## Architecture

```
                    Hospital Management System

    ┌───────────────────────────────────────────────────┐
    │                                                   │
    │  Patient Module                                   │
    │  Doctor Module                                    │
    │  Appointment Module                               │
    │  Billing Module                                   │
    │  Prescription Module                              │
    │  Inventory Module                                 │
    │  Analytics Module                                 │
    │  Notification Module                              │
    │                                                   │
    └───────────────────────────────────────────────────┘

                    Single Database
```

### Communication

```
AppointmentService
        │
        ▼
BillingService.generateBill()

        │
        ▼
NotificationService.sendEmail()
```

Everything communicates through direct Java method calls.

---

## Advantages

- Very easy to build
- Single codebase
- Single deployment
- Easier debugging
- No distributed system complexity
- Simple transaction management
- Lower infrastructure cost

---

## Disadvantages

### Tight Coupling

Every module lives inside the same application.

Changing Billing could unexpectedly affect Appointment.

---

### Scaling Issues

Suppose:

Appointment Service receives

1000 requests/second

Billing receives

20 requests/second

A monolith requires scaling the entire application instead of only the Appointment module.

---

### Technology Lock-In

Everything would have to be implemented using the same technology stack.

For this project, that would mean Java everywhere.

There would be no opportunity to introduce Go where it provides advantages.

---

### Large Deployments

Changing a single line inside Billing requires redeploying:

- Patient
- Doctor
- Appointment
- Inventory
- Analytics
- Notification

even though none of those services changed.

---

### Learning Limitations

A monolith would not expose us to:

- Kafka
- RabbitMQ
- gRPC
- CQRS
- Event-driven architecture
- Service discovery
- Kubernetes
- Distributed tracing

These were major learning objectives for this project.

---

# 5. Option B — Modular Monolith

Instead of separating services physically, separate them logically.

```
Hospital Management

├── patient
├── doctor
├── appointment
├── billing
├── inventory
├── notification
```

Each module has clear boundaries but still runs inside one application.

---

## Advantages

- Cleaner than a traditional monolith
- Easier testing
- Shared transactions
- Easier future migration

---

## Disadvantages

Still suffers from:

- Single deployment
- Single runtime
- Single database
- No independent scaling

While cleaner than a traditional monolith, it still does not provide experience with distributed systems.

---

# 6. Option C — Microservices (Chosen)

## Architecture

```
                   Hospital Management System

          ┌──────────────┐
          │ API Gateway  │
          └──────┬───────┘
                 │
─────────────────┼──────────────────────────────────────

 Patient Service

 Doctor Service

 Appointment Service

 Billing Service

 Inventory Service

 Prescription Service

 Analytics Service

 Notification Service (Go)

 Authentication Service

───────────────────────────────────────────────────────

Kafka

RabbitMQ

gRPC

PostgreSQL
```

Each service owns a single business capability.

---

# 7. Why This Option Was Chosen

The primary objective of this project was not simply to build a hospital application.

The objective was to design and implement a production-inspired distributed backend system that demonstrates modern backend engineering concepts.

Microservices align with that objective by enabling:

- independent service ownership,
- event-driven communication,
- technology diversity,
- realistic deployment workflows,
- distributed systems learning.

---

# 8. Consequences

## Positive Consequences

### Independent Deployment

Each service can be deployed without redeploying the rest of the system.

---

### Independent Scaling

Appointment Service can scale independently of Billing or Inventory.

---

### Technology Flexibility

Java Spring Boot was selected for core business services.

Go was selected for the Notification Service because it is lightweight, highly concurrent, and well suited for background processing.

---

### Better Fault Isolation

If Notification Service becomes unavailable, Patient Service can continue operating.

The failure remains isolated instead of affecting the entire application.

---

### Event-Driven Architecture

Microservices naturally support asynchronous communication using Kafka.

Instead of direct service calls:

```
Appointment Service

↓

Kafka

↓

Billing

Analytics

Notification
```

This reduces runtime dependencies.

---

### Portfolio Value

This architecture better reflects the systems used in medium and large engineering organizations.

It demonstrates practical knowledge of distributed backend systems rather than only CRUD application development.

---

# 9. Trade-offs Accepted

Choosing microservices introduced additional complexity.

Examples include:

- Docker Compose
- Multiple databases
- Kafka infrastructure
- RabbitMQ
- gRPC
- Service communication
- Eventual consistency
- Network failures
- Monitoring

These complexities were accepted because understanding and solving them was one of the primary goals of the project.

---

# 10. Lessons Learned

One of the biggest lessons from this decision was:

> Architecture should be chosen based on the goals of the project, not because a particular architecture is popular.

If this application were intended for:

- a small clinic,
- one developer,
- a few hundred users,

then a monolith would likely be the better engineering decision.

Microservices were chosen because the project aims to explore distributed systems, event-driven architecture, and production backend patterns.

---

# 11. Future Decisions Enabled

Choosing microservices made the following architectural decisions possible:

- Kafka for asynchronous communication
- RabbitMQ for notification processing
- Go Notification Service
- CQRS Read Models
- Event-Carried State Transfer
- Independent databases
- API Gateway
- Kubernetes deployment
- Independent scaling
- Distributed observability

Without ADR-001, many of the subsequent architecture decisions would not have been necessary.

---

# 12. Interview Questions

### Why did you choose microservices instead of a monolith?

### When would a monolith be the better choice?

### What additional complexity did microservices introduce?

### How do your services communicate?

### Why did you separate Notification into a Go service?

### How would you deploy these services independently?

### How do you handle failures between services?

---

# 13. Final Decision

The Hospital Management System will be implemented as a collection of independently deployable microservices.

This decision best satisfies the project's learning objectives while closely reflecting modern backend architectures used in production systems.