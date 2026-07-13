# ADR-012: Introducing the Dispatcher Pattern

**Status:** Accepted

**Date:** July 2026

**Category:** Notification Architecture

**Decision Makers:** Hari Pradap V

---

# 1. Context

The Notification Service receives business events from Kafka.

Examples include:

- Appointment Created
- Appointment Cancelled
- Bill Paid
- Prescription Ready
- Low Inventory

Each event may need to notify users through one or more communication channels.

Initially, handlers were responsible for sending notifications directly.

Example:

```
Appointment Handler

↓

Send Email
```

At first this appeared simple.

However, as additional notification channels were introduced, this design became increasingly difficult to maintain.

---

# 2. Problem Statement

Who should decide **how** a notification is delivered?

Should every event handler directly call:

- Email
- SMS
- WebSocket

or

should there be a dedicated component responsible for notification delivery?

---

# 3. Initial Design

Initially each handler controlled notification delivery.

```
Appointment Handler

↓

SMTP

↓

Email
```

Billing Handler

```
↓

SMTP

↓

Email
```

Prescription Handler

```
↓

SMTP

↓

Email
```

Every handler duplicated notification logic.

---

# 4. Why This Looked Attractive

Advantages:

- Very little code
- Easy to understand
- Direct implementation
- No additional abstractions

For a small application this architecture is acceptable.

---

# 5. Problems

As more notification types were introduced, several issues appeared.

---

## Problem 1 — Duplicate Logic

Every handler contained:

- Email logic
- Error handling
- Status updates

The same code existed repeatedly.

---

## Problem 2 — Multiple Channels

Suppose Appointment Created requires:

- Email
- In-App Notification

Now every handler must know how to send both.

Later SMS is added.

Every handler changes again.

---

## Problem 3 — Violates Single Responsibility

Appointment Handler should answer one question:

> What notification should be created?

Instead it also answered:

> How should it be delivered?

These are different responsibilities.

---

## Problem 4 — Difficult Extension

Imagine adding:

```
Push Notification
```

Every handler requires modification.

Appointment.

Billing.

Prescription.

Inventory.

Authentication.

The same change must be repeated everywhere.

---

## Problem 5 — Tight Coupling

Business logic becomes tightly coupled to infrastructure.

```
Appointment Handler

↓

SMTP

↓

AWS SES
```

Changing email providers affects business handlers.

---

# 6. Alternative Options

---

## Option A — Direct Delivery

```
Handler

↓

SMTP
```

Advantages

- Simple
- Fewer components

Disadvantages

- Duplicate code
- Poor scalability
- Hard to extend
- Mixed responsibilities

---

## Option B — Dispatcher Pattern (Chosen)

```
Handler

↓

Dispatcher

↓

Email

SMS

In-App
```

Handlers no longer know how notifications are delivered.

They simply describe **what** should be delivered.

---

# 7. What Is the Dispatcher?

The Dispatcher acts as the central orchestration layer for notification delivery.

Its responsibilities include:

- determining delivery channels,
- creating notification records,
- publishing delivery jobs,
- updating notification status.

Handlers never communicate directly with Email or SMS providers.

---

# 8. Architecture

```
Appointment Handler

↓

Notification Request

↓

Dispatcher

↓

Notification Repository

↓

RabbitMQ

↓

Workers
```

Notice that the handler no longer knows whether the notification is delivered through:

- Email
- SMS
- In-App
- Push

The Dispatcher decides.

---

# 9. Responsibilities

## Event Handler

Responsible for:

- understanding the business event,
- selecting the notification template,
- building the notification request.

---

## Dispatcher

Responsible for:

- validating the request,
- creating notification records,
- selecting delivery channels,
- publishing work to RabbitMQ.

---

## Workers

Responsible for:

- sending Email,
- sending SMS,
- sending In-App notifications.

---

# 10. Benefits

## Better Separation of Concerns

Business logic remains separate from delivery infrastructure.

---

## Easy Channel Expansion

Adding Push Notifications only requires:

```
Dispatcher

↓

Push Worker
```

Business handlers remain unchanged.

---

## Centralized Delivery Logic

Retry policies.

Logging.

Status updates.

Persistence.

Everything exists in one place.

---

## Improved Maintainability

Notification delivery rules evolve independently of business events.

---

# 11. Trade-offs Accepted

Introducing a Dispatcher adds:

- one additional abstraction,
- one additional layer,
- slightly more code.

This complexity was accepted because it dramatically reduces duplication and simplifies future expansion.

---

# 12. Lessons Learned

One of the biggest lessons from this decision was:

> Business events should describe **what happened**, not **how notifications are delivered**.

The Appointment Handler knows an appointment was created.

The Dispatcher knows how that information reaches the user.

Keeping these responsibilities separate produces a cleaner architecture.

---

# 13. Future Decisions Enabled

The Dispatcher directly enables:

- Multi-channel notifications
- RabbitMQ workers
- Retry strategies
- Dead Letter Queues
- Notification auditing
- Delivery metrics

Without the Dispatcher, every new delivery mechanism would require modifying every event handler.

---

# 14. Interview Questions

### Why introduce a Dispatcher?

### Why not send emails directly?

### What responsibilities belong inside the Dispatcher?

### What happens when Push Notifications are added?

### How does the Dispatcher improve maintainability?

### Is the Dispatcher aware of business logic?

---

# 15. Final Decision

The Notification Service introduces a dedicated Dispatcher layer responsible for orchestrating notification delivery.

Event Handlers are responsible only for constructing notification requests.

The Dispatcher determines how those requests are persisted, routed, and delivered, resulting in a more maintainable and extensible notification architecture.