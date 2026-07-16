package model

import (
	"time"

	"github.com/google/uuid"
)

type DeliveryStatus string

const (
	DeliveryPending    DeliveryStatus = "PENDING"
	DeliveryProcessing DeliveryStatus = "PROCESSING"
	DeliverySent       DeliveryStatus = "SENT"
	DeliveryFailed     DeliveryStatus = "FAILED"
)

type NotificationChannel string

const (
	ChannelEmail NotificationChannel = "EMAIL"
	ChannelSMS   NotificationChannel = "SMS"
	ChannelInApp NotificationChannel = "IN_APP"
)

type NotificationDelivery struct {
	ID uuid.UUID `gorm:"type:uuid;default:gen_random_uuid();primaryKey"`

	NotificationID uuid.UUID `gorm:"type:uuid;not null;index"`

	Notification Notification `gorm:"foreignKey:NotificationID"`

	Channel NotificationChannel `gorm:"type:varchar(20);not null"`

	Status DeliveryStatus `gorm:"type:varchar(20);not null;default:'PENDING'"`

	RetryCount int `gorm:"default:0"`

	ErrorMessage string `gorm:"type:text"`

	ProviderMessageID string `gorm:"size:255"`

	SentAt *time.Time

	CreatedAt time.Time

	UpdatedAt time.Time
}
