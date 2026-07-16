package outbox

import (
	"time"

	"github.com/google/uuid"
)

type OutboxStatus string

const (
	StatusPending   OutboxStatus = "PENDING"
	StatusPublished OutboxStatus = "PUBLISHED"
	StatusFailed    OutboxStatus = "FAILED"
)

type OutboxEvent struct {
	ID uuid.UUID `gorm:"type:uuid;default:gen_random_uuid();primaryKey"`

	// Business event
	EventType string `gorm:"size:100;not null"`

	// Notification, Appointment, Billing...
	AggregateID string `gorm:"size:100;not null"`

	// EMAIL / SMS / IN_APP
	Destination string `gorm:"size:100;not null"`

	// Serialized RabbitMQ message
	Payload []byte `gorm:"type:bytea;not null"`

	Status OutboxStatus `gorm:"type:varchar(20);default:'PENDING'"`

	RetryCount int `gorm:"default:0"`

	ErrorMessage string `gorm:"type:text"`

	PublishedAt *time.Time

	CreatedAt time.Time

	UpdatedAt time.Time
}
