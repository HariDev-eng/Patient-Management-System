package model

import (
	"time"

	"github.com/google/uuid"
)

type NotificationStatus string

const (
	StatusPending    NotificationStatus = "PENDING"
	StatusProcessing NotificationStatus = "PROCESSING"
	StatusSent       NotificationStatus = "SENT"
	StatusFailed     NotificationStatus = "FAILED"
)

type NotificationChannel string

const (
	ChannelEmail NotificationChannel = "EMAIL"
	ChannelSMS   NotificationChannel = "SMS"
	ChannelInApp NotificationChannel = "IN_APP"
)

type Notification struct {
	ID uuid.UUID `gorm:"type:uuid;default:gen_random_uuid();primaryKey"`

	// Who should receive the notification
	Recipient string `gorm:"not null"`

	// EMAIL | SMS | WEBSOCKET
	Channel NotificationChannel `gorm:"type:varchar(20);not null"`

	// Business event
	EventType string `gorm:"size:100;not null"`

	// Appointment ID, Bill ID, Patient ID, etc.
	ReferenceID string `gorm:"size:100"`

	// Email subject (unused for SMS/WebSocket)
	Subject string `gorm:"size:255"`

	// Notification content
	Message string `gorm:"type:text;not null"`

	// PENDING | PROCESSING | SENT | FAILED
	Status NotificationStatus `gorm:"type:varchar(20);not null;default:'PENDING'"`

	// Number of retry attempts
	RetryCount int `gorm:"default:0"`

	// Error message if delivery fails
	ErrorMessage string `gorm:"type:text"`

	CreatedAt time.Time

	SentAt *time.Time
}
