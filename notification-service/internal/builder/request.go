package builder

import "github.com/haridev-eng/patient-management/notification-service/internal/model"

type NotificationRequest struct {
	Recipient string

	Channels []model.NotificationChannel

	EventType string

	ReferenceID string

	Subject string

	Message string
}
