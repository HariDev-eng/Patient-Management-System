# ADR-008: Why gRPC Was Not Used Everywhere

**Status:** Accepted

**Date:** July 2026

**Category:** Service Communication

**Decision Makers:** Hari Pradap V

---

# 1. Context

After deciding to introduce gRPC for synchronous communication (ADR-007), another architectural question naturally followed.

> If gRPC is faster than REST, why not replace every communication with gRPC?

At first glance, this appears to be the obvious decision.

```
Everything

↓

gRPC
```

However, after analysing the responsibilities of each interaction inside the Hospital Management System, it became clear that not every communication pattern benefits from gRPC.

Different interactions have different requirements.

---

# 2. Problem Statement

Should every communication between services use gRPC,

or

should multiple communication patterns coexist depending on the business requirement?

---

# 3. Communication Patterns Identified

While designing the system we identified three completely different communication patterns.

---

## Pattern 1 — Client → Backend

Examples:

- Login
- Create Patient
- Create Appointment
- Search Doctors
- Pay Bill

Characteristics:

- Request/Response
- Human initiated
- Internet facing
- Browser or Mobile application

---

## Pattern 2 — Service → Service (Immediate)

Examples:

- Validate JWT
- Check User Permissions
- Verify Doctor Availability
- Fetch Configuration

Characteristics:

- Internal
- Immediate response required
- Low latency
- Strong contracts

---

## Pattern 3 — Business Events

Examples:

```
Appointment Created

Bill Paid

Prescription Issued

Inventory Low Stock
```

Characteristics:

- Asynchronous
- Multiple consumers
- Event driven

---

These patterns are fundamentally different.

Trying to solve all of them with one technology would lead to unnecessary complexity.

---

# 4. Initial Approach

Initially we considered replacing REST entirely.

```
Frontend

↓

gRPC

↓

Gateway

↓

gRPC

↓

Everything
```

This looked attractive because gRPC is faster than REST.

---

# 5. Problems

---

## Problem 1 — Browsers

Modern browsers do not natively communicate with standard gRPC.

They require:

- gRPC-Web
- Proxy layers
- Additional configuration

For browser-based applications,

REST remains significantly simpler.

---

## Problem 2 — Public APIs

REST is universally understood.

Third-party developers can easily consume REST APIs using:

- Postman
- curl
- Swagger
- Browser

Replacing public APIs with gRPC would increase the barrier to integration.

---

## Problem 3 — Business Events

Business events are not request-response interactions.

Example:

```
Appointment Created
```

The producer does not require an immediate response.

Using gRPC here would tightly couple services again.

Kafka is a much better fit.

---

## Problem 4 — Operational Complexity

If every communication becomes gRPC:

```
Client

↓

Gateway

↓

gRPC

↓

Internal

↓

Kafka

↓

RabbitMQ
```

The architecture becomes unnecessarily complicated.

Not every interaction benefits from binary protocols.

---

# 6. Alternative Options

---

## Option A — REST Everywhere

```
Everything

↓

REST
```

### Advantages

- Simple
- Universal
- Easy debugging

### Disadvantages

- Larger payloads
- Slower serialization
- Weak contracts
- Less efficient internal communication

---

## Option B — gRPC Everywhere

```
Everything

↓

gRPC
```

### Advantages

- High performance
- Binary protocol
- Strong contracts

### Disadvantages

- Browser limitations
- More tooling
- Poor fit for asynchronous workflows
- More difficult external integrations

---

## Option C — Hybrid Communication (Chosen)

Use the communication style that best fits the interaction.

```
Client

↓

REST

↓

API Gateway

↓

gRPC

↓

Internal Services

↓

Kafka

↓

Business Events

↓

RabbitMQ

↓

Background Workers
```

---

# 7. Why This Option Was Chosen

The architecture follows one important principle:

> **Communication should be selected based on business requirements rather than technology preference.**

Different interactions solve different problems.

---

## REST

Used when:

- external clients communicate with the system
- human-readable APIs are valuable
- public integrations exist

---

## gRPC

Used when:

- one service requires an immediate response
- strong contracts are important
- low latency matters
- internal communication only

---

## Kafka

Used when:

- business events occur
- multiple services react
- loose coupling is required

---

## RabbitMQ

Used when:

- background work must be executed
- retries are required
- workers process independent tasks

---

# 8. Final Communication Matrix

| Communication | Technology |
|---------------|------------|
| Browser → API Gateway | REST |
| Mobile → API Gateway | REST |
| API Gateway → Internal Services | gRPC |
| Service → Service (Immediate) | gRPC |
| Business Events | Kafka |
| Notification Jobs | RabbitMQ |
| Email Workers | RabbitMQ |
| SMS Workers | RabbitMQ |

---

# 9. Architecture

```
                 Browser / Mobile

                        │

                     REST API

                        │

                  API Gateway

                        │

                 ───── gRPC ─────

        Patient      Doctor      Billing

             │           │

             └──── Kafka ─────┐

                              │

                        Notification

                              │

                         RabbitMQ

                   ┌────────┼─────────┐

                   ▼        ▼         ▼

                Email     SMS     In-App
```

---

# 10. Benefits

## Right Tool for the Right Problem

Each communication technology is responsible only for the workload it handles best.

---

## Better Maintainability

Instead of forcing one technology everywhere,

the architecture remains easier to understand.

---

## Better Performance

Internal services benefit from gRPC.

External APIs remain easy to consume.

---

## Better Scalability

Business events remain asynchronous.

Long-running tasks do not block user requests.

---

# 11. Trade-offs Accepted

Maintaining multiple communication mechanisms means:

- REST
- gRPC
- Kafka
- RabbitMQ

Each requires:

- monitoring
- documentation
- testing
- deployment

This complexity was accepted because each technology solves a different architectural problem.

---

# 12. Lessons Learned

One of the biggest lessons from this decision was:

> There is no single communication technology that is ideal for every scenario.

Choosing technologies based on benchmarks alone often leads to poor architecture.

Instead, communication should be driven by:

- latency requirements,
- consistency requirements,
- consumer patterns,
- operational needs.

---

# 13. Future Decisions Enabled

This decision directly supports:

- API Gateway
- Authentication Service
- Notification Service
- Analytics
- CQRS
- Event-Driven Architecture
- Background Workers

---

# 14. Interview Questions

### Why didn't you replace REST with gRPC?

### Why not use Kafka instead of gRPC?

### When should REST be preferred?

### Why are browsers still commonly using REST?

### Why are business events not implemented using gRPC?

### How do you decide which communication technology to use?

### What communication style does your Notification Service use?

---

# 15. Final Decision

The Hospital Management System intentionally uses multiple communication mechanisms.

- **REST** for external client communication.
- **gRPC** for synchronous internal service communication.
- **Kafka** for business event propagation.
- **RabbitMQ** for background task execution.

Rather than standardizing on a single technology, the architecture selects the communication mechanism that best matches the business requirement.