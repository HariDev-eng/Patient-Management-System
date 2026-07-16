package model

import (
	"time"

	"github.com/google/uuid"
)

type Notification struct {
	ID uuid.UUID `gorm:"type:uuid;default:gen_random_uuid();primaryKey"`

	// Recipient of the notification
	Recipient string `gorm:"not null"`

	// Business event
	EventType string `gorm:"size:100;not null"`

	// Appointment ID, Bill ID, Prescription ID...
	ReferenceID string `gorm:"size:100"`

	// Email subject / Notification title
	Subject string `gorm:"size:255"`

	// Notification content
	Message string `gorm:"type:text;not null"`

	CreatedAt time.Time
	UpdatedAt time.Time

	// One Notification -> Many Deliveries
	Deliveries []NotificationDelivery `gorm:"foreignKey:NotificationID"`
}
