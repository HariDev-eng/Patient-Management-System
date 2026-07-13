# ADR-005: Polyglot Microservices (Java + Go)

**Status:** Accepted

**Date:** July 2026

**Category:** System Design

**Decision Makers:** Hari Pradap V

---

# 1. Context

After deciding to adopt a microservice architecture and separate databases, another architectural decision emerged.

Should every service use the same programming language?

or

Should each service be implemented using the technology that best fits its workload?

Initially, the entire Hospital Management System was planned using Spring Boot.

However, while designing the Notification Service, it became clear that not every service has identical characteristics.

Different services perform different types of work.

---

# 2. Problem Statement

Should every microservice use a single technology stack,

or

should different services use different languages depending on their responsibilities?

---

# 3. Initial Approach

Everything in Java.

```
Patient Service

Doctor Service

Appointment Service

Billing Service

Inventory Service

Prescription Service

Analytics Service

Notification Service

↓

Spring Boot
```

Advantages:

- One language
- One framework
- Easier onboarding
- Easier dependency management

This is the architecture followed by many organizations.

---

# 4. Problems

Although consistency is valuable,

every service has different workload characteristics.

Examples:

Patient Service

- CRUD
- Transactions
- Validation

Appointment Service

- CRUD
- Scheduling
- Business logic

Notification Service

- Background workers
- Kafka consumers
- RabbitMQ consumers
- Thousands of concurrent network requests
- Email delivery
- WebSockets

The Notification Service behaves fundamentally differently.

Treating every service identically means ignoring these differences.

---

# 5. Option A — Single Language

```
Everything

↓

Spring Boot
```

## Advantages

- Consistency
- Shared libraries
- Easier hiring
- Easier onboarding
- Simpler CI/CD

## Disadvantages

- Every workload receives the same runtime
- Less opportunity to optimize for specific problems
- Limits technology exploration

---

# 6. Option B — Polyglot Microservices (Chosen)

```
Business Services

↓

Spring Boot

-------------------------

Notification Service

↓

Go
```

The architecture chooses the technology based on the nature of the service rather than enforcing one language across the entire system.

---

# 7. Why This Option Was Chosen

Not every microservice has the same responsibilities.

Business services are dominated by:

- validation
- business rules
- transactions
- persistence

Spring Boot provides an excellent ecosystem for these requirements.

Notification Service is dominated by:

- concurrency
- networking
- asynchronous processing
- worker execution

Go is particularly well suited for these workloads.

Therefore,

using different technologies provides a better fit for each service.

---

# 8. Benefits

## Better Technology Fit

Instead of asking:

> Which language should we use?

The question becomes:

> Which language best solves this problem?

---

## Independent Evolution

Changing Notification Service does not affect Billing.

Changing Billing does not affect Appointment.

Each service evolves independently.

---

## Better Learning

The project now demonstrates experience with:

- Java
- Spring Boot
- Go
- Gin
- Kafka
- RabbitMQ
- gRPC

rather than only one technology stack.

---

## Realistic Architecture

Many production systems are polyglot.

Examples include combinations of:

- Java
- Go
- Node.js
- Python
- Rust

Different teams choose technologies appropriate for their workloads.

---

# 9. Trade-offs Accepted

Using multiple languages introduces:

- different build systems
- different dependency managers
- different debugging tools
- different deployment pipelines

Developers need to understand more than one ecosystem.

This complexity was accepted because the Notification Service benefits significantly from Go's strengths.

---

# 10. Lessons Learned

One important lesson from this decision was:

> Standardization is valuable, but it should not prevent choosing the right tool for a specific problem.

Technology should support the architecture,

not dictate it.

---

# 11. Future Decisions Enabled

This decision enables:

- Go Notification Service
- Go concurrency
- Worker pools
- Lightweight background processing
- Efficient Kafka consumers
- Efficient RabbitMQ workers

---

# 12. Interview Questions

### Why didn't you build every service in Java?

### What is a polyglot architecture?

### What are the disadvantages?

### When would you avoid multiple languages?

### How do services written in different languages communicate?

### Does using multiple languages increase operational complexity?

---

# 13. Final Decision

The Hospital Management System will use a polyglot microservice architecture.

Core business services are implemented using Spring Boot,

while services with workloads better suited to Go may be implemented using Go.

Technology selection is driven by service responsibilities rather than enforcing a single language across the entire system.