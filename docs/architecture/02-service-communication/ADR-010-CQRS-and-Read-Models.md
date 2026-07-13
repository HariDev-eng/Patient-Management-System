# ADR-010: CQRS and Read Models

**Status:** Accepted

**Date:** July 2026

**Category:** Service Communication

**Decision Makers:** Hari Pradap V

---

# 1. Context

After adopting Event-Carried State Transfer (ADR-009), the Notification Service began receiving patient and doctor events through Kafka.

These events contained the information required to send notifications.

The next architectural decision was:

> How should this replicated data be stored and accessed?

Initially,

it seemed sufficient to simply insert the incoming event into a table.

However,

after analysing future requirements, we realised the Notification Service required a dedicated read model rather than treating these tables as copies of the source databases.

---

# 2. Problem Statement

Should Notification Service query other services whenever information is required,

or

should it maintain its own optimized view of the data?

---

# 3. Initial Architecture

Initially,

Notification Service looked like this:

```
Appointment Event

↓

Notification Service

↓

REST / gRPC

↓

Patient Service

↓

Patient Information

↓

Send Notification
```

This architecture required every notification to communicate with another service.

After ADR-009,

the runtime dependency disappeared.

Now another decision appeared.

Should Notification simply cache patient data,

or

should it own a dedicated read model?

---

# 4. Understanding CQRS

CQRS stands for

**Command Query Responsibility Segregation.**

The core idea is:

> The model used to write data does not have to be the same model used to read data.

Traditional systems use one model.

```
Database

↓

Read

Write
```

CQRS separates them.

```
Write Model

↓

Events

↓

Read Model
```

The read model is optimized for queries.

The write model remains optimized for business rules and transactions.

---

# 5. Write Model

Patient Service owns the write model.

```
Patient Service

↓

patient_db

↓

Business Rules

↓

Validation

↓

Transactions
```

Only Patient Service modifies patient information.

No other service writes to this database.

---

# 6. Read Model

Notification Service owns its own read model.

```
notification_patients

ID

First Name

Last Name

Email

Phone
```

Notice what is missing.

There is no:

- Medical History
- Insurance
- Allergies
- Emergency Contact
- Blood Pressure

Because Notification Service never needs those fields.

The read model contains only the information required by the Notification domain.

---

# 7. Why Not Store Everything?

Initially,

we considered copying the complete Patient table.

```
Patients

↓

Copy Entire Table
```

Advantages:

- Easy synchronization

Disadvantages:

- Unnecessary storage
- Larger events
- Sensitive information duplicated
- Harder schema evolution

The Notification Service should only store data it actually requires.

---

# 8. Architecture

```
                Patient Service

                      │

             Patient Updated

                      │

                    Kafka

                      │

                      ▼

           Patient Projection

                      │

                      ▼

         notification_patients
```

The projection transforms business events into a read model optimized for Notification Service.

---

# 9. Benefits

## Fast Queries

Notification Service performs local lookups.

```
Appointment Event

↓

Local Database

↓

Email

↓

Done
```

No network communication.

---

## Better Performance

Database access is local.

Latency decreases significantly.

---

## Better Availability

Notification Service continues operating even if Patient Service is unavailable.

---

## Smaller Database

Only relevant fields are stored.

This reduces storage requirements and simplifies maintenance.

---

## Better Security

Sensitive patient information is not unnecessarily replicated.

The Notification Service stores only what it needs.

---

# 10. Trade-offs Accepted

CQRS introduces several challenges.

---

## Eventual Consistency

Read models are updated asynchronously.

Immediately after an update,

the read model may briefly contain stale information.

---

## Synchronization Logic

Projection code must remain correct.

If projections fail,

the read model becomes outdated.

---

## More Components

Instead of one database,

the architecture now includes:

- Producer
- Kafka
- Consumer
- Projection
- Read Model

This increases architectural complexity.

---

# 11. Relationship with Event-Carried State Transfer

These two patterns complement each other.

Event-Carried State Transfer answers:

> How does Notification receive data?

Answer:

Through Kafka events containing business data.

CQRS answers:

> How does Notification organize that data?

Answer:

By maintaining a dedicated read model optimized for queries.

Without ADR-009,

CQRS would have no data source.

Without ADR-010,

the received events would have no structured storage strategy.

---

# 12. Lessons Learned

One of the most important lessons was:

> Every service should own the data model that best serves its own responsibilities.

The Notification Service does not need the same representation of a patient as the Patient Service.

Each service is free to organize data according to its own requirements.

This reduces coupling and improves maintainability.

---

# 13. Future Decisions Enabled

CQRS directly enables:

- Patient Projection
- Doctor Projection
- Notification Dispatcher
- Analytics Read Models
- Dashboard Optimization
- Offline Notification Processing

---

# 14. Interview Questions

### What is CQRS?

### What is a Read Model?

### Why does Notification have its own Patient table?

### How is CQRS different from Event-Carried State Transfer?

### Does CQRS require Event Sourcing?

### What are the disadvantages?

### How do you rebuild a read model?

### What happens if projections fail?

---

# 15. Final Decision

The Hospital Management System adopts the CQRS pattern for consumer services.

Business services remain the source of truth for write operations,

while downstream services such as Notification and Analytics maintain dedicated read models synchronized through Kafka events.

Each read model contains only the data required by its owning service, providing faster queries, better availability, and clear service boundaries.