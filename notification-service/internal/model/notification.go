package model

import (
	"time"

	"github.com/google/uuid"
)

type NotificationStatus string

const (
	StatusPending NotificationStatus = "PENDING"
	StatusSent    NotificationStatus = "SENT"
	StatusFailed  NotificationStatus = "FAILED"
)

type NotificationChannel string

const (
	ChannelEmail     NotificationChannel = "EMAIL"
	ChannelSMS       NotificationChannel = "SMS"
	ChannelWebSocket NotificationChannel = "WEBSOCKET"
)

type Notification struct {
	ID uuid.UUID `gorm:"type:uuid;default:gen_random_uuid();primaryKey"`

	Recipient string

	Channel string

	Subject string

	Message string

	Status string

	RetryCount int

	CreatedAt time.Time

	SentAt *time.Time
}
