package builder

import "github.com/haridev-eng/patient-management/notification-service/internal/model"

type NotificationBuilder struct{}

func NewNotificationBuilder() *NotificationBuilder {
	return &NotificationBuilder{}
}

func (b *NotificationBuilder) Build(
	request NotificationRequest,
	channel model.NotificationChannel,
) *model.Notification {

	return &model.Notification{
		Recipient:   request.Recipient,
		Channel:     channel,
		EventType:   request.EventType,
		ReferenceID: request.ReferenceID,
		Subject:     request.Subject,
		Message:     request.Message,
		Status:      model.StatusPending,
	}
}
