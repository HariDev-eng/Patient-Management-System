# ADR-003: Choosing Kafka for Domain Events and RabbitMQ for Task Processing

**Status:** Accepted

**Date:** July 2026

**Category:** Service Communication

**Decision Makers:** Hari Pradap V

---

# 1. Context

After deciding to use event-driven communication (ADR-002), the next question was:

> Which messaging system should the Hospital Management System use?

Initially, two technologies were considered:

- Apache Kafka
- RabbitMQ

At first glance they appear to solve the same problem.

Both:

- move messages
- connect services
- support producers and consumers

However, after understanding their architecture, it became clear that they are designed for different purposes.

The objective became choosing the right tool for each type of communication rather than forcing a single technology to solve every problem.

---

# 2. Problem Statement

The Hospital Management System requires two different communication patterns.

### Pattern 1

Business events.

Example:

```
Appointment Created

↓

Billing

Analytics

Notification
```

One event should be visible to multiple services.

---

### Pattern 2

Background work.

Example:

```
Send Email

↓

Worker

↓

AWS SES
```

One task should be executed by one worker.

These are fundamentally different communication models.

---

# 3. Requirements

The messaging solution should support:

- loose coupling
- scalability
- reliability
- fault tolerance
- retries
- multiple consumers where necessary
- background processing

---

# 4. Option A — RabbitMQ Everywhere

Initially we considered using RabbitMQ for everything.

```
Appointment

↓

RabbitMQ

↓

Billing

Analytics

Notification
```

---

## Advantages

- Easy to configure
- Reliable queues
- Supports retries
- Dead Letter Queues
- Excellent work queue implementation

---

## Problems

RabbitMQ is designed around queues.

Messages are consumed.

After consumption,

they disappear.

This is ideal for work queues.

However,

business events often need multiple independent consumers.

Imagine:

```
Appointment Created
```

Needs to reach

- Billing
- Notification
- Analytics
- Audit

RabbitMQ can support this,

but it requires exchanges, bindings and routing rules.

As the number of services grows,

configuration becomes increasingly complex.

---

# 5. Option B — Kafka Everywhere

Another possibility was using Kafka for everything.

```
Appointment

↓

Kafka

↓

Everything
```

Including

```
Email

SMS

Push Notifications
```

---

## Advantages

Kafka excels at broadcasting events.

It supports:

- multiple consumers
- replay
- consumer groups
- high throughput
- event streaming

---

## Problems

Kafka is not designed to be a job queue.

Consider sending an email.

```
Send Email
```

Only one worker should perform that task.

Retries should be automatic.

Failed emails should move to a Dead Letter Queue.

RabbitMQ provides these capabilities naturally.

Using Kafka for task execution would require significantly more custom infrastructure.

---

# 6. Option C — Kafka + RabbitMQ (Chosen)

Instead of choosing one technology,

the architecture uses each where it fits best.

```
Business Events

↓

Kafka

↓

Services
```

```
Notification Dispatch

↓

RabbitMQ

↓

Workers
```

---

# 7. Final Architecture

```
                 Appointment Service

                          │

          APPOINTMENT_CREATED Event

                          │

                          ▼

                       Kafka

        ┌───────────────┼─────────────────┐

        ▼               ▼                 ▼

    Billing        Analytics      Notification

                                          │

                                          ▼

                               Notification Dispatcher

                                          │

                                          ▼

                                     RabbitMQ

                            ┌────────┼────────┐

                            ▼        ▼        ▼

                        Email     SMS     In-App
```

---

# 8. Why Kafka Was Chosen

Kafka represents business facts.

Example:

```
Appointment Created

Patient Registered

Bill Paid

Prescription Created
```

These events describe something that happened in the business domain.

Multiple services may react independently.

Kafka allows this naturally.

---

# 9. Why RabbitMQ Was Chosen

RabbitMQ represents work.

Examples:

```
Send Email

Send SMS

Generate PDF

Upload Report
```

These are tasks.

Each task should be completed once.

RabbitMQ provides:

- acknowledgements
- retries
- dead letter queues
- worker queues

without additional complexity.

---

# 10. Comparison

| Feature | Kafka | RabbitMQ |
|----------|--------|-----------|
| Purpose | Event Streaming | Task Processing |
| Multiple Consumers | Excellent | Possible but less natural |
| Event Replay | Yes | No |
| Ordering | Strong | Queue-based |
| Retry Support | Manual | Built-in |
| Dead Letter Queue | Limited | Excellent |
| Background Jobs | Possible | Excellent |
| High Throughput | Excellent | Good |
| Long-term Storage | Yes | No |

---

# 11. Consequences

## Positive

### Right Tool for Each Problem

Instead of forcing one technology,

each messaging system is responsible for the workload it handles best.

---

### Cleaner Architecture

Kafka handles business communication.

RabbitMQ handles execution.

Responsibilities remain clear.

---

### Better Scalability

Kafka consumers scale independently.

RabbitMQ workers scale independently.

Both layers can evolve without affecting one another.

---

### Better Reliability

Notification failures no longer affect Appointment Service.

Email workers retry automatically.

Critical business events remain safely stored in Kafka.

---

# 12. Trade-offs Accepted

Operating two messaging systems introduces:

- additional Docker containers
- more configuration
- more monitoring
- more operational complexity

This complexity was accepted because each technology provides capabilities that would otherwise require custom implementation.

---

# 13. Lessons Learned

The most important realization was:

> Kafka and RabbitMQ are complementary technologies, not competing technologies.

Initially,

it seemed necessary to choose one.

After understanding their architecture,

it became clear that they solve different engineering problems.

Kafka distributes business events.

RabbitMQ distributes work.

Using both results in a cleaner, more maintainable system.

---

# 14. Future Decisions Enabled

This decision directly enables:

- Notification Dispatcher
- Email Worker
- SMS Worker
- WebSocket Worker
- Retry Mechanism
- Dead Letter Queue
- Event Replay
- Event Sourcing
- CQRS Read Models

---

# 15. Interview Questions

### Why didn't you use Kafka for emails?

### Why didn't you use RabbitMQ for all communication?

### What is the difference between an event and a task?

### What problems does RabbitMQ solve better?

### What problems does Kafka solve better?

### When would you choose only Kafka?

### When would you choose only RabbitMQ?

---

# 16. Final Decision

The Hospital Management System will use **Kafka** as the event backbone for business communication between microservices.

**RabbitMQ** will be used exclusively for asynchronous task execution inside the Notification Service.

This architecture allows business events and background work to be handled using technologies specifically designed for those workloads.