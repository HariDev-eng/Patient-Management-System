# ADR-007: Choosing gRPC for Synchronous Service Communication

**Status:** Accepted

**Date:** July 2026

**Category:** Service Communication

**Decision Makers:** Hari Pradap V

---

# 1. Context

After adopting an event-driven architecture using Kafka (ADR-002), the Hospital Management System already supported asynchronous communication.

Example:

```
Appointment Created

↓

Kafka

↓

Billing

Analytics

Notification
```

However, while designing future services, another requirement emerged.

Some operations require an immediate response.

For example:

- Authentication
- Authorization
- Token validation
- User lookup
- Permission checks

These operations cannot wait for asynchronous event processing.

The requesting service must receive an immediate answer.

This introduced another architectural question:

> How should services communicate when an immediate response is required?

---

# 2. Problem Statement

How should synchronous communication be implemented between microservices while maintaining:

- high performance,
- strong contracts,
- language interoperability,
- scalability?

---

# 3. Requirements

Synchronous communication should provide:

- Low latency
- Efficient serialization
- Strong API contracts
- Cross-language support
- Easy versioning
- Reliable communication

---

# 4. Initial Approach

The obvious solution was REST.

```
Appointment Service

↓

HTTP

↓

Patient Service

↓

JSON Response
```

This approach is extremely common.

Initially it appeared sufficient.

---

# 5. Problems with REST

Although REST works well,

several disadvantages became apparent.

---

## Problem 1 — JSON Serialization

REST communicates using JSON.

Example:

```
Patient

↓

JSON

↓

Network

↓

JSON

↓

Object
```

JSON is:

- human readable
- flexible

but larger than binary protocols.

---

## Problem 2 — Performance

Every request requires:

- JSON serialization
- HTTP parsing
- JSON deserialization

For frequent service-to-service communication,

this introduces unnecessary overhead.

---

## Problem 3 — Weak Contracts

REST documentation usually exists separately.

Example:

```
Swagger

↓

Controller

↓

Implementation
```

If documentation becomes outdated,

services may disagree about request formats.

---

## Problem 4 — Cross-language Type Safety

Our architecture now contains:

- Java
- Go

Keeping DTOs synchronized manually becomes increasingly difficult.

---

# 6. Alternative Options

---

## Option A — REST

```
Service

↓

HTTP

↓

JSON
```

### Advantages

- Easy to understand
- Human readable
- Browser friendly
- Excellent tooling

### Disadvantages

- Larger payloads
- Slower serialization
- Separate API documentation
- Manual DTO synchronization

---

## Option B — gRPC (Chosen)

```
Service

↓

HTTP/2

↓

Protocol Buffers

↓

Response
```

---

### Advantages

- Binary protocol
- Very fast
- HTTP/2
- Streaming support
- Strong contracts
- Automatic code generation
- Excellent Java ↔ Go interoperability

---

### Disadvantages

- Harder to debug manually
- Not browser friendly
- Requires protobuf definitions
- Learning curve

---

# 7. Why gRPC Was Chosen

The project already uses Protocol Buffers for Kafka events.

Using the same technology for synchronous communication provides several advantages.

Instead of manually maintaining DTOs in multiple languages,

both Java and Go generate code from the same `.proto` definitions.

This guarantees consistency across services.

---

# 8. Architecture

```
Patient Service

↓

patient.proto

↓

Java Stub

↓

Go Stub

↓

gRPC Communication
```

The `.proto` file becomes the contract shared by all services.

---

# 9. Benefits

## Strong Contracts

The interface is defined once.

Both Java and Go generate their implementations automatically.

---

## Better Performance

Binary serialization is significantly smaller than JSON.

Network bandwidth decreases.

Latency decreases.

---

## Cross-language Support

Java and Go communicate using exactly the same contract.

No manual DTO conversion is required.

---

## Compile-time Safety

Breaking changes are detected during compilation rather than at runtime.

---

# 10. Trade-offs Accepted

Choosing gRPC introduces:

- Protocol Buffers
- Code generation
- Additional tooling
- More complex debugging

These trade-offs were accepted because the benefits outweigh the additional complexity for internal service communication.

---

# 11. Lessons Learned

One important lesson was:

> REST is designed primarily for client-server communication.

gRPC is designed primarily for service-to-service communication.

Both have their place.

The communication pattern determines the technology—not the other way around.

---

# 12. Future Decisions Enabled

Choosing gRPC enables:

- Shared protobuf contracts
- Java ↔ Go interoperability
- Authentication Service
- API Gateway integrations
- Internal service APIs

---

# 13. Interview Questions

### Why did you introduce gRPC?

### Why not REST?

### What advantages does Protocol Buffers provide?

### Why is gRPC popular in microservices?

### How does gRPC support multiple languages?

### What is HTTP/2?

### What are the disadvantages of gRPC?

---

# 14. Final Decision

The Hospital Management System will use **gRPC** for synchronous communication where an immediate response is required.

REST remains available for external client APIs,

while gRPC is reserved for internal service-to-service communication where performance, type safety, and strong contracts are more important.