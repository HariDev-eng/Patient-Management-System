# ADR-006: Choosing Go for the Notification Service

**Status:** Accepted

**Date:** July 2026

**Category:** Service Communication

**Decision Makers:** Hari Pradap V

---

# 1. Context

After deciding to adopt a polyglot microservice architecture (ADR-005), the next architectural question was:

> Which service should be implemented using Go?

Initially, every service in the Hospital Management System was planned using Java and Spring Boot.

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

This approach provided consistency and leveraged a single technology stack across the entire project.

However, while designing the Notification Service, it became clear that its workload differed significantly from the other business services.

Unlike Patient or Billing services, Notification Service spends most of its time:

- consuming Kafka events,
- processing RabbitMQ messages,
- performing network I/O,
- sending emails,
- pushing WebSocket notifications,
- communicating with AWS services.

This raised an important question:

> Is Spring Boot the best technology for this particular workload?

---

# 2. Problem Statement

Should the Notification Service continue using Spring Boot for consistency,

or

should it use Go because of its workload characteristics?

---

# 3. Requirements

The Notification Service should:

- process thousands of asynchronous events efficiently,
- support high concurrency,
- consume Kafka events,
- process RabbitMQ workers,
- perform many outbound network requests,
- use minimal resources,
- remain simple to deploy.

---

# 4. Initial Approach

Initially, Notification Service was designed exactly like every other service.

```
Notification Service

↓

Spring Boot

↓

Kafka Consumer

↓

RabbitMQ Consumer

↓

SMTP

↓

WebSocket
```

Advantages:

- Same language as the rest of the system.
- Shared libraries.
- Familiar development experience.
- Easier onboarding.

At first this appeared to be the simplest solution.

---

# 5. Problems

After analysing the service responsibilities, several observations emerged.

---

## Problem 1 — Mostly I/O Bound

Notification Service performs very little business logic.

Instead it waits for:

- Kafka
- RabbitMQ
- SMTP
- AWS SES
- AWS SNS
- WebSocket connections

Most of its time is spent waiting for network operations.

---

## Problem 2 — High Concurrency

Imagine:

```
Appointment Created

↓

1000 Notifications

↓

1000 Emails
```

The service must process many independent tasks simultaneously.

This workload benefits from lightweight concurrency.

---

## Problem 3 — Resource Usage

A notification service may run many workers simultaneously.

Running hundreds or thousands of concurrent operations efficiently is important.

Reducing memory usage allows more workers to execute on the same machine.

---

## Problem 4 — Learning Objective

One objective of this project was mastering Go.

Rather than creating an artificial Go project,

integrating Go into a real production-inspired architecture provides significantly better learning value.

---

# 6. Alternatives Considered

---

## Option A — Spring Boot

```
Notification

↓

Spring Boot
```

### Advantages

- Consistency
- Mature ecosystem
- Excellent Spring integrations
- Easy dependency injection

### Disadvantages

- Heavier runtime
- More resources
- Concurrency model not as lightweight as Go
- Less opportunity to learn Go

---

## Option B — Go (Chosen)

```
Notification

↓

Go

↓

Gin

↓

Kafka

↓

RabbitMQ
```

### Advantages

- Lightweight runtime
- Excellent concurrency using goroutines
- Fast startup
- Low memory usage
- Simple deployment
- Excellent fit for background workers

### Disadvantages

- Smaller ecosystem compared to Spring
- Less familiar initially
- Different tooling
- Additional language in the project

---

# 7. Why Go Was Chosen

The Notification Service behaves differently from traditional CRUD services.

Unlike Patient Service:

```
Receive Request

↓

Validate

↓

Store Database

↓

Return Response
```

Notification Service behaves like:

```
Consume Event

↓

Build Notification

↓

Dispatch

↓

Wait for Network

↓

Repeat
```

This type of workload is dominated by concurrency rather than business logic.

Go was designed specifically for this style of programming.

---

# 8. Architectural Impact

The architecture now becomes:

```
                 Spring Boot

        Patient Service

        Doctor Service

        Appointment Service

        Billing Service

        Inventory Service

        Prescription Service

        Analytics Service

──────────────────────────────────────────────

                     Go

             Notification Service

          Kafka Consumers

          RabbitMQ Workers

          Dispatcher

          Templates

          AWS SES

          AWS SNS

          WebSocket
```

Each language is used where it provides the greatest benefit.

---

# 9. Benefits

## Better Performance

Go's lightweight goroutines allow many concurrent notification tasks to execute efficiently.

---

## Lower Resource Consumption

The Notification Service requires less memory compared to running the same workload using traditional threads.

---

## Better Concurrency Model

The service naturally maps to Go's concurrency primitives.

Examples include:

- Kafka consumers
- RabbitMQ workers
- WebSocket connections
- Email workers

---

## Independent Evolution

The Notification Service can evolve independently of the Java services.

It can adopt Go-specific libraries without affecting the remainder of the system.

---

## Improved Learning

This project now demonstrates practical experience with:

- Spring Boot
- Java
- Go
- Gin
- Kafka
- RabbitMQ

rather than only a single backend technology.

---

# 10. Trade-offs Accepted

Choosing Go introduces additional complexity.

Examples:

- Different build system
- Different dependency management
- Separate CI pipeline
- Separate testing strategy

The team (or developer) must maintain knowledge of two programming ecosystems.

This complexity was accepted because the Notification Service strongly benefits from Go's strengths.

---

# 11. Lessons Learned

One important lesson from this decision was:

> Services should be implemented using technologies that best fit their responsibilities.

Technology consistency is valuable,

but forcing every service to use the same stack may ignore important workload differences.

The Notification Service demonstrates that architecture should drive technology choices—not the other way around.

---

# 12. Future Decisions Enabled

Choosing Go enables:

- Goroutine-based worker pools
- Lightweight Kafka consumers
- Efficient RabbitMQ workers
- Concurrent email delivery
- Concurrent WebSocket broadcasting
- Better scalability for notification workloads

---

# 13. Interview Questions

### Why did you implement only the Notification Service in Go?

### Why didn't you rewrite every service in Go?

### Why is Go well suited for notification systems?

### What makes Notification Service different from Patient Service?

### What are goroutines?

### What are the disadvantages of introducing multiple languages?

### Would you still choose Go if the Notification Service only sent a few emails per day?

---

# 14. Final Decision

The Notification Service will be implemented using Go.

This decision is based on the service's workload characteristics rather than maintaining language consistency across the system.

Business services continue using Spring Boot,

while Go is used where lightweight concurrency and asynchronous processing provide clear architectural advantages.